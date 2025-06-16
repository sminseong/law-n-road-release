package com.lawnroad.payment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawnroad.payment.dto.*;
import com.lawnroad.payment.model.OrdersVO;
import com.lawnroad.payment.service.OrdersService;
import com.lawnroad.payment.service.PaymentService;
import com.lawnroad.payment.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/confirm")
public class PaymentController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RefundService refundService;

    private final String SECRET_KEY = "test_sk_4yKeq5bgrpoROnDY0L4XVGX0lzW6";
    private final String TOSS_URL = "https://api.tosspayments.com/v1/payments/confirm";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/payment")
    public ResponseEntity<?> confirmPayment(@RequestBody PaymentConfirmRequestDTO request) {
        try {
            // Toss API 호출
            RestTemplate restTemplate = new RestTemplate();
            String encodedKey = Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + encodedKey);

            Map<String, Object> payload = Map.of(
                    "paymentKey", request.getPaymentKey(),
                    "orderId", request.getOrderId(),
                    "amount", request.getAmount()
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<PaymentResponseDTO> tossResponse = restTemplate.postForEntity(
                    TOSS_URL, entity, PaymentResponseDTO.class
            );

            PaymentResponseDTO response = tossResponse.getBody();
            if (response == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(Map.of("message", "Toss 응답이 비어 있습니다."));
            }

            OrdersVO order = ordersService.getOrderByCode(response.getOrderId());
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "해당 주문이 존재하지 않습니다."));
            }

            Long orderNo = order.getNo();
            JsonNode tossResponseJson = objectMapper.convertValue(response, JsonNode.class);
            paymentService.savePaymentFromToss(tossResponseJson, orderNo);

            OrdersStatusUpdateDTO dto = new OrdersStatusUpdateDTO();
            dto.setOrderNo(orderNo);

            switch (response.getStatus()) {
                case "DONE" -> dto.setStatus("PAID");
                case "CANCELED" -> {
                    refundService.saveRefundFromToss(tossResponseJson);
                    dto.setStatus("FAILED");
                }
                default -> dto.setStatus("FAILED");
            }

            ordersService.changeStatus(dto);
            return ResponseEntity.status(tossResponse.getStatusCode()).body(response);

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            // Toss 자체에서 실패 응답 (예: 결제 만료, 카드 오류)
            try {
                JsonNode errorJson = objectMapper.readTree(ex.getResponseBodyAsString());
                String orderId = request.getOrderId();
                OrdersVO order = ordersService.getOrderByCode(orderId);
                if (order != null) {
                    OrdersStatusUpdateDTO dto = new OrdersStatusUpdateDTO();
                    dto.setOrderNo(order.getNo());
                    dto.setStatus("FAILED");
                    ordersService.changeStatus(dto);
                }

                return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                        "message", "결제 승인 실패",
                        "error", errorJson.get("message").asText()
                ));
            } catch (Exception parseEx) {
                return ResponseEntity.status(500).body(Map.of(
                        "message", "결제 승인 실패 및 오류 응답 처리 실패",
                        "error", parseEx.getMessage()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "결제 승인 실패",
                    "error", e.getMessage()
            ));
        }
    }


    @PostMapping("/cancel")
    public ResponseEntity<?> cancelPayment(@RequestParam String paymentKey,
                                           @RequestParam String reason) {
        try {
            // 1. Toss 요청
            String encodedKey = Base64.getEncoder()
                    .encodeToString((SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"))
                    .header("Authorization", "Basic " + encodedKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"cancelReason\":\"" + reason + "\"}"))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode responseJson = objectMapper.readTree(response.body());

            // 2. 환불 저장
            refundService.saveRefundFromToss(responseJson);

            // 🔥 3. 주문 상태도 업데이트 필요
            String orderId = responseJson.get("orderId").asText();
            OrdersVO order = ordersService.getOrderByCode(orderId);
            if (order != null) {
                OrdersStatusUpdateDTO dto = new OrdersStatusUpdateDTO();
                dto.setOrderNo(order.getNo());
                dto.setStatus("CANCELED");
                ordersService.changeStatus(dto);
            }

            return ResponseEntity.ok(responseJson);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "결제 취소 실패",
                    "error", e.getMessage()
            ));
        }
    }

}

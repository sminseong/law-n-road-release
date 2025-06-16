package com.lawnroad.payment.controller;

import com.lawnroad.payment.dto.OrdersStatusUpdateDTO;
import com.lawnroad.payment.dto.PaymentConfirmRequestDTO;
import com.lawnroad.payment.dto.PaymentResponseDTO;
import com.lawnroad.payment.mapper.PaymentMapper;
import com.lawnroad.payment.model.OrdersVO;
import com.lawnroad.payment.model.PaymentVO;
import com.lawnroad.payment.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/confirm")
public class PaymentController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private PaymentMapper paymentMapper;

    private final String SECRET_KEY = "test_sk_4yKeq5bgrpoROnDY0L4XVGX0lzW6";
    private final String TOSS_URL = "https://api.tosspayments.com/v1/payments/confirm";

    @PostMapping("/payment")
    public ResponseEntity<?> confirmPayment(@RequestBody PaymentConfirmRequestDTO request) {
        try {
            // 1. Toss API 호출
            RestTemplate restTemplate = new RestTemplate();

            String encodedKey = Base64.getEncoder()
                    .encodeToString((SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));

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

            // 2. 주문 조회
            OrdersVO order = ordersService.getOrderByCode(response.getOrderId());
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "해당 주문이 존재하지 않습니다."));
            }

            Long orderNo = order.getNo();

            // 3. 결제 정보 저장
            PaymentVO payment = new PaymentVO();
            payment.setOrderNo(orderNo);
            payment.setPaymentKey(response.getPaymentKey());
            payment.setOrderCode(response.getOrderId());
            payment.setAmount(response.getTotalAmount().longValue());
            payment.setStatus(response.getStatus());
            payment.setCardCompany(response.getCard() != null ? response.getCard().getCompany() : null);
            payment.setInstallmentMonth(response.getCard() != null ? response.getCard().getInstallmentPlanMonths() : null);

            try {
                payment.setPurchasedAt(LocalDateTime.parse(response.getApprovedAt()));
            } catch (DateTimeParseException e) {
                payment.setPurchasedAt(null); // 또는 현재 시간 등 fallback
            }

            payment.setPg("TOSS");

            paymentMapper.insertPayment(payment);

            // 4. 주문 상태 업데이트
            if ("DONE".equalsIgnoreCase(response.getStatus())) {
                OrdersStatusUpdateDTO dto = new OrdersStatusUpdateDTO();
                dto.setOrderNo(orderNo);
                dto.setStatus("PAID");
                ordersService.changeStatus(dto);
            }

            return ResponseEntity.status(tossResponse.getStatusCode()).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "결제 승인 실패",
                    "error", e.getMessage()
            ));
        }
    }
}

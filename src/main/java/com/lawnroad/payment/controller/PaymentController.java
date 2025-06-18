package com.lawnroad.payment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawnroad.payment.dto.PaymentConfirmRequestDTO;
import com.lawnroad.payment.dto.PaymentResponseDTO;
import com.lawnroad.payment.dto.RefundRequestDTO;
import com.lawnroad.payment.dto.OrdersStatusUpdateDTO;
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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/confirm")
public class PaymentController {

    private final OrdersService   ordersService;
    private final PaymentService  paymentService;
    private final RefundService   refundService;

    private static final String SECRET_KEY = "test_sk_4yKeq5bgrpoROnDY0L4XVGX0lzW6";
    private static final String TOSS_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";

    public PaymentController(OrdersService ordersService,
                             PaymentService paymentService,
                             RefundService refundService) {
        this.ordersService  = ordersService;
        this.paymentService = paymentService;
        this.refundService  = refundService;
    }

    @PostMapping("/payment")
    public ResponseEntity<?> confirmPayment(@RequestBody PaymentConfirmRequestDTO req) {
        RestTemplate rt = new RestTemplate();
        String basic = Base64.getEncoder()
                .encodeToString((SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + basic);
        Map<String, Object> payload = Map.of(
                "paymentKey", req.getPaymentKey(),
                "orderId",    req.getOrderId(),
                "amount",     req.getAmount()
        );
        HttpEntity<Map<String,Object>> ent = new HttpEntity<>(payload, headers);

        JsonNode root;
        try {
            ResponseEntity<JsonNode> tossResp =
                    rt.postForEntity(TOSS_CONFIRM_URL, ent, JsonNode.class);
            root = tossResp.getBody();
            if (root == null) throw new IllegalStateException("Empty Toss response");
        } catch (HttpClientErrorException | HttpServerErrorException ex) {

            String body = ex.getResponseBodyAsString();
            // body 예시: {"message":"결제 승인 실패","error":"카드 한도가 초과되었습니다."}
            String msg = "결제 승인 실패";
            int idx = body.indexOf("\"message\"");
            if (idx >= 0) {
                int start = body.indexOf('"', idx + 9) + 1;
                int end   = body.indexOf('"', start);
                if (start>0 && end>start) {
                    msg = body.substring(start, end);
                }
            }
            return ResponseEntity.status(ex.getStatusCode())
                    .body(Map.of("message", msg));
        }


        // (1) order 조회
        String orderCode = root.path("orderId").asText();
        OrdersVO order = ordersService.getOrderByCode(orderCode);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "해당 주문이 없습니다."));
        }
        Long orderNo = order.getNo();

        // (2) 결제 저장
        paymentService.savePaymentFromToss(root, orderNo);

        // (3) 필요시 환불 저장
        if ("CANCELED".equals(root.path("status").asText())) {
            refundService.saveRefundFromToss(root);
        }

        // (4) 주문 상태 업데이트
        OrdersStatusUpdateDTO st = new OrdersStatusUpdateDTO();
        st.setOrderNo(orderNo);
        st.setStatus("DONE".equals(root.path("status").asText()) ? "PAID" : "FAILED");
        ordersService.changeStatus(st);

        return ResponseEntity.ok(root);
    }


    @PostMapping("/cancel")
    public ResponseEntity<?> cancelPayment(@RequestBody RefundRequestDTO req) {
        try {
            refundService.processRefund(req);
            return ResponseEntity.ok(Map.of("message","환불 처리 완료"));
        } catch (IllegalArgumentException ie) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ie.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message","환불 처리 실패","error",e.getMessage()));
        }
    }
}
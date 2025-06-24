package com.lawnroad.payment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.lawnroad.payment.dto.PaymentConfirmRequestDTO;
import com.lawnroad.payment.dto.RefundRequestDTO;
import com.lawnroad.payment.dto.OrdersStatusUpdateDTO;
import com.lawnroad.payment.handler.PostPaymentHandler;
import com.lawnroad.payment.model.OrdersVO;
import com.lawnroad.payment.service.OrdersService;
import com.lawnroad.payment.service.PaymentService;
import com.lawnroad.payment.service.RefundService;
import com.lawnroad.reservation.mapper.ReservationsMapper;
import com.lawnroad.payment.mapper.PaymentMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/confirm")
public class PaymentController {

    private final OrdersService          ordersService;
    private final PaymentService         paymentService;
    private final RefundService          refundService;
    private final ReservationsMapper     reservationsMapper;
    private final PaymentMapper          paymentMapper;
    private final List<PostPaymentHandler> handlers;

    private static final String SECRET_KEY       = "test_sk_4yKeq5bgrpoROnDY0L4XVGX0lzW6";
    private static final String TOSS_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";

    public PaymentController(
            OrdersService ordersService,
            PaymentService paymentService,
            RefundService refundService,
            ReservationsMapper reservationsMapper,
            PaymentMapper paymentMapper,
            List<PostPaymentHandler> handlers
    ) {
        this.ordersService      = ordersService;
        this.paymentService     = paymentService;
        this.refundService      = refundService;
        this.reservationsMapper = reservationsMapper;
        this.paymentMapper      = paymentMapper;
        this.handlers           = handlers;
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
            String msg = "결제 승인 실패";
            int idx = body.indexOf("\"message\"");
            if (idx >= 0) {
                int start = body.indexOf('"', idx + 9) + 1;
                int end   = body.indexOf('"', start);
                if (start > 0 && end > start) {
                    msg = body.substring(start, end);
                }
            }
            return ResponseEntity.status(ex.getStatusCode())
                    .body(Map.of("message", msg));
        }

        // (1) 주문 조회
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

        // (5) 도메인별 후처리
        handlers.stream()
                .filter(h -> h.getOrderType().equals(order.getOrderType()))
                .findFirst()
                .ifPresent(h -> h.handlePaymentSuccess(orderNo, root));

        return ResponseEntity.ok(root);
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelPayment(@RequestBody RefundRequestDTO req) {
        // 1) reservationNo -> orderNo, amount 조회
        Long reservationNo = req.getReservationNo();
        var res = reservationsMapper.selectReservationByNo(reservationNo);
        Long orderNo = res.getOrderNo();
        Long amount  = res.getAmount();

        // 2) orderNo -> paymentKey 조회
        String paymentKey = paymentMapper.findPaymentKeyByOrderNo(orderNo);

        // 3) DTO 세팅
        req.setOrderNo(orderNo);
        req.setPaymentKey(paymentKey);
        req.setAmount(amount);
        req.setCancelReason("사용자 취소 요청");

        try {
            refundService.processRefund(req);

            // (4) 도메인별 환불 후처리
            OrdersVO order = ordersService.getOrder(orderNo);
            handlers.stream()
                    .filter(h -> h.getOrderType().equals(order.getOrderType()))
                    .findFirst()
                    .ifPresent(h -> h.handleRefund(orderNo, req));

            return ResponseEntity.ok(Map.of("message","환불 처리 완료"));
        } catch (IllegalArgumentException ie) {
            String msg = ie.getMessage() != null ? ie.getMessage() : "리소스를 찾을 수 없습니다.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", msg));
        } catch (Exception e) {
            String err = e.getMessage() != null ? e.getMessage() : "환불 처리 실패";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message","환불 처리 실패","error", err));
        }
    }
}

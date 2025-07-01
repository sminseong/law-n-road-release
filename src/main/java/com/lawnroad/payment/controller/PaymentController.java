package com.lawnroad.payment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.lawnroad.payment.dto.PaymentConfirmRequestDTO;
import com.lawnroad.payment.dto.RefundRequestDTO;
import com.lawnroad.payment.dto.OrdersStatusUpdateDTO;
import com.lawnroad.payment.model.OrdersVO;
import com.lawnroad.payment.service.OrdersService;
import com.lawnroad.payment.service.PaymentService;
import com.lawnroad.payment.service.RefundService;
import com.lawnroad.reservation.mapper.ReservationsMapper;
import com.lawnroad.payment.mapper.PaymentMapper;
import com.lawnroad.reservation.mapper.TimeSlotMapper;
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

    private final OrdersService        ordersService;
    private final PaymentService       paymentService;
    private final RefundService        refundService;
    private final ReservationsMapper   reservationsMapper;
    private final PaymentMapper        paymentMapper;

    private static final String SECRET_KEY        = "test_sk_4yKeq5bgrpoROnDY0L4XVGX0lzW6";
    private static final String TOSS_CONFIRM_URL  = "https://api.tosspayments.com/v1/payments/confirm";
    private final TimeSlotMapper timeSlotMapper;

    public PaymentController(
            OrdersService ordersService,
            PaymentService paymentService,
            RefundService refundService,
            ReservationsMapper reservationsMapper,
            PaymentMapper paymentMapper,
            TimeSlotMapper timeSlotMapper) {
        this.ordersService       = ordersService;
        this.paymentService      = paymentService;
        this.refundService       = refundService;
        this.reservationsMapper  = reservationsMapper;
        this.paymentMapper       = paymentMapper;
        this.timeSlotMapper = timeSlotMapper;
    }

    @PostMapping("/payment")
    public ResponseEntity<?> confirmPayment(@RequestBody PaymentConfirmRequestDTO req) {
        // (기존 confirmPayment 로직 그대로)
        RestTemplate rt = new RestTemplate();
        String basic = Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
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
            // 1) reservationNo 기준—기존 예약 취소 흐름 유지
            if (req.getReservationNo() != null) {
                Long reservationNo = req.getReservationNo();
                // (1-1) reservationNo → orderNo, amount 조회
                var res = reservationsMapper.selectReservationByNo(reservationNo);
                Long orderNo  = res.getOrderNo();
                Long amount   = res.getAmount();
                // (1-2) orderNo → paymentKey 조회
                String paymentKey = paymentMapper.findPaymentKeyByOrderNo(orderNo);
                // (1-3) DTO 세팅
                req.setOrderNo(orderNo);
                req.setPaymentKey(paymentKey);
                req.setAmount(amount);
                req.setCancelReason("사용자 예약 취소 요청");
                // (1-4) 환불 처리
                refundService.processRefund(req);
                // (1-5) 예약 status 변경
                reservationsMapper.updateReservationStatus(reservationNo,"CANCELED");
                // (1-6) 해당 타임슬롯 status 변경
                timeSlotMapper.resetSlotStatusByResNo(reservationNo);
                return ResponseEntity.ok(Map.of("message", "예약 환불 처리 완료"));
            }

            // 2) orderNo 기준—템플릿/광고 환불 분기
            else if (req.getOrderNo() != null) {
                Long orderNo = req.getOrderNo();

                // ◼︎ (2-1) 주문 정보 조회
                OrdersVO order = ordersService.getOrder(orderNo);
                if (order == null) {
                    return ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .body(Map.of("message", "해당 주문이 없습니다."));
                }

                // ◼︎ (2-2) 타입 검증: orderType 필드 사용
                String type = order.getOrderType();
                if (!"TEMPLATE".equals(type) && !"ADVERTISEMENT".equals(type)) {
                    return ResponseEntity
                            .badRequest()
                            .body(Map.of("message", "해당 주문은 존재하지 않습니다."));
                }

                // ◼︎ (2-3) 결제키 · 금액 준비
                String paymentKey = paymentMapper.findPaymentKeyByOrderNo(orderNo);
                Long   amount     = order.getAmount();

                // ◼︎ (2-4) DTO 세팅 후 환불 처리
                req.setPaymentKey(paymentKey);
                req.setAmount(amount);
                req.setCancelReason("사용자 " + type.toLowerCase() + " 환불 요청");

                refundService.processRefund(req);

                return ResponseEntity.ok(
                        Map.of("message", type + " 환불 처리 완료")
                );
            }

            // 3) 둘 다 없으면 잘못된 요청
            else {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("message", "reservationNo 또는 orderNo 중 하나를 전달해야 합니다."));
            }

        } catch (IllegalArgumentException ie) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ie.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "환불 처리 중 오류가 발생했습니다.", "error", e.getMessage()));
        }
    }
}

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/confirm")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private static final String SECRET_KEY       = "test_sk_4yKeq5bgrpoROnDY0L4XVGX0lzW6";
    private static final String TOSS_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";

    private final OrdersService      ordersService;
    private final PaymentService     paymentService;
    private final RefundService      refundService;
    private final ReservationsMapper reservationsMapper;
    private final PaymentMapper      paymentMapper;
    private final TimeSlotMapper     timeSlotMapper;

    public PaymentController(
            OrdersService ordersService,
            PaymentService paymentService,
            RefundService refundService,
            ReservationsMapper reservationsMapper,
            PaymentMapper paymentMapper,
            TimeSlotMapper timeSlotMapper
    ) {
        this.ordersService      = ordersService;
        this.paymentService     = paymentService;
        this.refundService      = refundService;
        this.reservationsMapper = reservationsMapper;
        this.paymentMapper      = paymentMapper;
        this.timeSlotMapper     = timeSlotMapper;
    }

    @PostMapping("/payment")
    @Transactional
    public ResponseEntity<?> confirmPayment(@RequestBody PaymentConfirmRequestDTO req) {
        logger.debug("confirmPayment called with orderId={}, paymentKey={}, amount={}",
                req.getOrderId(), req.getPaymentKey(), req.getAmount());

        RestTemplate rt = new RestTemplate();
        String basic = Base64.getEncoder()
                .encodeToString((SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + basic);

        Map<String,Object> payload = Map.of(
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
            logger.debug("Toss confirm response: status={}, body={}",
                    tossResp.getStatusCode(), root);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            logger.error("Error calling Toss confirm API: status={}, body={}",
                    ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            handleCompensation(req.getOrderId());
            String msg = extractErrorMessage(ex.getResponseBodyAsString());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", msg));
        }

        // 결제 승인 성공 처리
        String orderCode = root.path("orderId").asText();
        OrdersVO order = ordersService.getOrderByCode(orderCode);
        if (order == null) {
            logger.warn("Order not found for code={}", orderCode);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "해당 주문이 없습니다."));
        }
        Long orderNo = order.getNo();

        long tossedAmount   = root.path("totalAmount").asLong();
        long expectedAmount = order.getAmount();
        if (tossedAmount != expectedAmount) {
            logger.warn("Amount mismatch: tossed={} expected={}", tossedAmount, expectedAmount);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "결제 금액 불일치");
        }

        paymentService.savePaymentFromToss(root, orderNo);
        if ("CANCELED".equals(root.path("status").asText())) {
            refundService.saveRefundFromToss(root);
        }
        OrdersStatusUpdateDTO st = new OrdersStatusUpdateDTO();
        st.setOrderNo(orderNo);
        st.setStatus("DONE".equals(root.path("status").asText()) ? "PAID" : "FAILED");
        ordersService.changeStatus(st);

        logger.debug("confirmPayment completed for orderNo={}, status={}",
                orderNo, root.path("status").asText());
        return ResponseEntity.ok(root);
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelPayment(@RequestBody RefundRequestDTO req) {
        logger.debug("cancelPayment called: request={}​", req);
        try {
            if (req.getReservationNo() != null) {
                Long resNo = req.getReservationNo();
                var res = reservationsMapper.selectReservationByNo(resNo);
                if (res == null) {
                    logger.warn("Reservation not found: {}", resNo);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("message", "해당 예약이 없습니다."));
                }
                Long orderNo = res.getOrderNo();
                req.setOrderNo(orderNo);
                req.setPaymentKey(paymentMapper.findPaymentKeyByOrderNo(orderNo));
                req.setAmount(res.getAmount());
                req.setCancelReason("사용자 예약 취소 요청");
                refundService.processRefund(req);
                logger.debug("Reservation refund processed for resNo={}", resNo);
                reservationsMapper.updateReservationStatus(resNo, "CANCELED");
                timeSlotMapper.resetSlotStatusByResNo(resNo);
                return ResponseEntity.ok(Map.of("message", "예약 환불 처리 완료"));
            } else if (req.getOrderNo() != null) {
                Long orderNo = req.getOrderNo();
                OrdersVO order = ordersService.getOrder(orderNo);
                if (order == null) {
                    logger.warn("Order not found for cancel: {}", orderNo);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("message", "해당 주문이 없습니다."));
                }
                String type = order.getOrderType();
                if (!"TEMPLATE".equals(type) && !"ADVERTISEMENT".equals(type)) {
                    logger.warn("Invalid order type for refund: {}", type);
                    return ResponseEntity.badRequest()
                            .body(Map.of("message", "해당 주문은 존재하지 않습니다."));
                }
                req.setPaymentKey(paymentMapper.findPaymentKeyByOrderNo(orderNo));
                req.setAmount(order.getAmount());
                req.setCancelReason("사용자 " + type.toLowerCase() + " 환불 요청");
                refundService.processRefund(req);
                logger.debug("Order refund processed for orderNo={}", orderNo);
                return ResponseEntity.ok(Map.of("message", type + " 환불 처리 완료"));
            } else {
                logger.warn("cancelPayment missing identifiers: {}", req);
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "reservationNo 또는 orderNo 중 하나를 전달해야 합니다."));
            }
        } catch (Exception e) {
            logger.error("Error during cancelPayment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "환불 처리 중 오류가 발생했습니다.", "error", e.getMessage()));
        }
    }

    // 에러 메시지 추출 헬퍼
    private String extractErrorMessage(String body) {
        String msg = "결제 승인 실패";
        int idx = body.indexOf("\"message\"");
        if (idx >= 0) {
            int start = body.indexOf('"', idx + 9) + 1;
            int end = body.indexOf('"', start);
            if (start > 0 && end > start) {
                msg = body.substring(start, end);
            }
        }
        return msg;
    }

    // 보상 처리 헬퍼
    private void handleCompensation(String orderCode) {
        logger.debug("handleCompensation start for orderCode={}", orderCode);
        OrdersVO order = ordersService.getOrderByCode(orderCode);
        if (order == null) {
            logger.warn("No order found for compensation: {}", orderCode);
            return;
        }
        OrdersStatusUpdateDTO st = new OrdersStatusUpdateDTO();
        st.setOrderNo(order.getNo());
        st.setStatus("CANCELED");
        ordersService.changeStatus(st);
        if ("RESERVATION".equals(order.getOrderType())) {
            var res = reservationsMapper.selectReservationByOrderNo(order.getNo());
            if (res != null) {
                reservationsMapper.updateReservationStatus(res.getNo(), "CANCELED");
                timeSlotMapper.resetSlotStatusByResNo(res.getNo());
                logger.debug("Reservation compensation completed: {}", res.getNo());
            }
        }
    }

    @PostMapping("/cancel-reservation")
    @Transactional
    public ResponseEntity<Map<String,String>> cancelReservationOnly(
            @RequestBody Map<String, Long> req
    ) {
        Long reservationNo = req.get("reservationNo");
        logger.debug("cancelReservationOnly called: {}", reservationNo);
        if (reservationNo == null) {
            logger.warn("cancelReservationOnly missing reservationNo");
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "reservationNo가 필요합니다."));
        }
        reservationsMapper.updateReservationStatus(reservationNo, "CANCELED");
        timeSlotMapper.resetSlotStatusByResNo(reservationNo);
        var res = reservationsMapper.selectReservationByNo(reservationNo);
        if (res != null && res.getOrderNo() != null) {
            OrdersStatusUpdateDTO upd = new OrdersStatusUpdateDTO();
            upd.setOrderNo(res.getOrderNo());
            upd.setStatus("CANCELED");
            ordersService.changeStatus(upd);
            logger.debug("cancelReservationOnly completed for orderNo={}", res.getOrderNo());
        }
        return ResponseEntity.ok(Map.of("message", "예약 및 주문이 취소되었습니다."));
    }
}

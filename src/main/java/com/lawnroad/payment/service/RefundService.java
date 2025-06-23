package com.lawnroad.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.lawnroad.payment.dto.RefundRequestDTO;
import com.lawnroad.payment.dto.RefundSaveDTO;
import com.lawnroad.payment.dto.OrdersStatusUpdateDTO;
import com.lawnroad.payment.mapper.PaymentMapper;
import com.lawnroad.reservation.mapper.ReservationsMapper;
import com.lawnroad.reservation.dto.ReservationsResponseDTO;
import com.lawnroad.reservation.mapper.TimeSlotMapper;
import com.lawnroad.reservation.model.TimeSlotVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

@Service
public class RefundService {

    private final PaymentMapper       paymentMapper;
    private final OrdersService       ordersService;
    private final ReservationsMapper  reservationsMapper;
    private final TimeSlotMapper      timeSlotMapper;
    private final RestTemplate        restTemplate;
    private final String              secretKey;

    public RefundService(
            PaymentMapper paymentMapper,
            OrdersService ordersService,
            ReservationsMapper reservationsMapper,
            TimeSlotMapper timeSlotMapper,
            @Value("${tosspayments.secret-key}") String secretKey
    ) {
        this.paymentMapper      = paymentMapper;
        this.ordersService      = ordersService;
        this.reservationsMapper = reservationsMapper;
        this.timeSlotMapper     = timeSlotMapper;
        this.restTemplate       = new RestTemplate();
        this.secretKey          = secretKey;
    }

    @Transactional
    public void saveRefundFromToss(JsonNode root) {
        // (1) 기존 환불 저장 로직
        String paymentKey = root.path("paymentKey").asText();
        Long paymentNo = paymentMapper.findPaymentNoByPaymentKey(paymentKey);
        if (paymentNo == null) {
            throw new IllegalArgumentException("존재하지 않는 paymentKey: " + paymentKey);
        }
        JsonNode cancel = root.path("cancels").get(0);

        RefundSaveDTO dto = new RefundSaveDTO();
        dto.setPaymentNo(paymentNo);
        dto.setRefundKey(cancel.path("transactionKey").asText());
        dto.setAmount(cancel.path("cancelAmount").asLong());
        dto.setStatus(cancel.path("cancelStatus").asText());
        dto.setReason(cancel.path("cancelReason").asText());
        String canceledAt = cancel.path("canceledAt").asText(null);
        if (canceledAt != null && !canceledAt.isEmpty()) {
            dto.setRefundedAt(OffsetDateTime
                    .parse(canceledAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .toLocalDateTime()
            );
        }
        paymentMapper.insertRefund(dto);

        // (2) 주문 상태를 CANCELED 로 변경
        Long orderNo = paymentMapper.findOrderNoByPaymentKey(paymentKey);
        OrdersStatusUpdateDTO upd = new OrdersStatusUpdateDTO();
        upd.setOrderNo(orderNo);
        upd.setStatus("CANCELED");
        ordersService.changeStatus(upd);

        // (3) 예약 정보 조회
        ReservationsResponseDTO reservation =
                reservationsMapper.selectReservationByOrderNo(orderNo);
        if (reservation != null) {
            // (3.1) 예약 상태를 CANCELED 로 변경
            reservationsMapper.updateReservationStatus(
                    reservation.getNo(),
                    "CANCELED"
            );

            Long slotNo = reservation.getSlotNo();
            // (4) 기존 슬롯 정보 조회
            TimeSlotVO existing = timeSlotMapper.selectBySlotNo(slotNo);
            // (5) 슬롯 상태를 1(예약가능)으로 복원
            TimeSlotVO restored = new TimeSlotVO(
                    existing.getNo(),
                    existing.getUserNo(),
                    existing.getSlotDate(),
                    existing.getSlotTime(),
                    1,
                    existing.getAmount()
            );
            timeSlotMapper.updateStatus(restored);

            paymentMapper.updatePaymentStatus(orderNo, "CANCELED");
        }
    }

    public void processRefund(RefundRequestDTO req) {
        // 1) Toss 환불 API 호출
        String url = "https://api.tosspayments.com/v1/payments/"
                + req.getPaymentKey() + "/cancel";
        String basic = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + basic);

        Map<String, String> payload = Map.of("cancelReason", req.getCancelReason());
        HttpEntity<Map<String, String>> ent = new HttpEntity<>(payload, headers);

        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                URI.create(url),
                HttpMethod.POST,
                ent,
                JsonNode.class
        );
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException(
                    "Toss 환불 API 실패: " + resp.getStatusCode()
            );
        }

        // 2) JsonNode → DB 저장 + 상태 복원
        saveRefundFromToss(resp.getBody());
    }
}

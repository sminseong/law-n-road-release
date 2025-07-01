package com.lawnroad.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.lawnroad.payment.dto.RefundRequestDTO;
import com.lawnroad.payment.dto.RefundSaveDTO;
import com.lawnroad.payment.dto.OrdersStatusUpdateDTO;
import com.lawnroad.payment.mapper.PaymentMapper;
import com.lawnroad.payment.service.OrdersService;
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

/**
 * 공통 환불 로직: Toss API 호출, DB 저장, 주문 상태 변경
 */
@Service
public class RefundService {

    private final PaymentMapper paymentMapper;
    private final OrdersService ordersService;
    private final RestTemplate restTemplate;
    private final String secretKey;

    public RefundService(
            PaymentMapper paymentMapper,
            OrdersService ordersService,
            @Value("${tosspayments.secret-key}") String secretKey
    ) {
        this.paymentMapper = paymentMapper;
        this.ordersService = ordersService;
        this.restTemplate = new RestTemplate();
        this.secretKey = secretKey;
    }

    /**
     * Toss 결제 취소 API 호출 → saveRefundFromToss 로 프록시
     */
    public void processRefund(RefundRequestDTO req) {
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
            throw new IllegalStateException("Toss 환불 API 실패: " + resp.getStatusCode());
        }

        saveRefundFromToss(resp.getBody());
    }

    /**
     * JsonNode → refunds 테이블 저장 + orders 상태 'CANCELED' 로 업데이트
     */
    @Transactional
    public void saveRefundFromToss(JsonNode root) {
        // 1) refund 레코드 저장
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
            dto.setRefundedAt(
                    OffsetDateTime.parse(canceledAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                            .toLocalDateTime()
            );
        }
        paymentMapper.insertRefund(dto);

        // 2) 주문 상태를 CANCELED 로 변경
        Long orderNo = paymentMapper.findOrderNoByPaymentKey(paymentKey);
        OrdersStatusUpdateDTO upd = new OrdersStatusUpdateDTO();
        upd.setOrderNo(orderNo);
        upd.setStatus("CANCELED");
        ordersService.changeStatus(upd);

        // 3) payments status 를 CANCELED 로 변경
        paymentMapper.updatePaymentStatus(orderNo,"CANCELED");
    }
}

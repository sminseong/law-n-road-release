package com.lawnroad.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.lawnroad.payment.dto.RefundRequestDTO;
import com.lawnroad.payment.dto.RefundSaveDTO;
import com.lawnroad.payment.dto.OrdersStatusUpdateDTO;
import com.lawnroad.payment.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

@Service
public class RefundService {

    private final PaymentMapper paymentMapper;
    private final OrdersService  ordersService;
    private final RestTemplate   restTemplate;
    private final String         secretKey;

    public RefundService(
            PaymentMapper paymentMapper,
            OrdersService ordersService,
            @Value("${tosspayments.secret-key}") String secretKey
    ) {
        this.paymentMapper = paymentMapper;
        this.ordersService = ordersService;
        this.restTemplate  = new RestTemplate();
        this.secretKey     = secretKey;
    }

    /**
     * Webhook 또는 controller 로부터 직접 환불 데이터를 받아 처리
     */
    public void saveRefundFromToss(JsonNode root) {
        // 1) payment_no 조회
        String paymentKey = root.path("paymentKey").asText();
        Long paymentNo = paymentMapper.findPaymentNoByPaymentKey(paymentKey);
        if (paymentNo == null) {
            throw new IllegalArgumentException("존재하지 않는 paymentKey: " + paymentKey);
        }

        // 2) 첫 번째 cancel 노드
        JsonNode cancel = root.path("cancels").get(0);

        // 3) DTO 생성
        RefundSaveDTO dto = new RefundSaveDTO();
        dto.setPaymentNo(paymentNo);
        dto.setRefundKey(cancel.path("transactionKey").asText());
        dto.setAmount(cancel.path("cancelAmount").asLong());
        dto.setStatus(cancel.path("cancelStatus").asText());
        dto.setReason(cancel.path("cancelReason").asText());

        // 4) 환불 시각 (오프셋 포함 문자열 파싱)
        String canceledAt = cancel.path("canceledAt").asText(null);
        if (canceledAt != null && !canceledAt.isEmpty()) {
            OffsetDateTime odt = OffsetDateTime.parse(
                    canceledAt,
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
            );
            dto.setRefundedAt(odt.toLocalDateTime());
        }

        // 5) 메타데이터
        JsonNode md = cancel.path("metadata");
        if (!md.isMissingNode() && !md.isNull()) {
            dto.setMetadata(md.toString());
        }

        // 6) DB 저장
        paymentMapper.insertRefund(dto);

        // 7) 주문 상태 업데이트
        Long orderNo = paymentMapper.findOrderNoByPaymentKey(paymentKey);
        OrdersStatusUpdateDTO st = new OrdersStatusUpdateDTO();
        st.setOrderNo(orderNo);
        st.setStatus("CANCELED");
        ordersService.changeStatus(st);
    }

    /**
     * Controller 로부터 paymentKey, amount, cancelReason 만 넘겨 받아
     * Toss API 호출 → saveRefundFromToss(JsonNode)로 처리
     */
    public void processRefund(RefundRequestDTO req) {
        // 1) Toss 환불 API 호출
        String url = "https://api.tosspayments.com/v1/payments/" + req.getPaymentKey() + "/cancel";
        String basic = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
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

        // 2) JsonNode → DB 저장 & 주문 상태 업데이트
        saveRefundFromToss(resp.getBody());
    }
}

package com.lawnroad.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawnroad.payment.dto.RefundSaveDTO;
import com.lawnroad.payment.mapper.PaymentMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class RefundService {

    private final PaymentMapper paymentMapper;
    private final ObjectMapper objectMapper;

    public RefundService(PaymentMapper paymentMapper, ObjectMapper objectMapper) {
        this.paymentMapper = paymentMapper;
        this.objectMapper = objectMapper;
    }

    public void saveRefundFromToss(JsonNode tossResponseJson) {
        // 1. paymentKey로 결제 번호 조회
        String paymentKey = tossResponseJson.get("paymentKey").asText();
        Long paymentNo = paymentMapper.findPaymentNoByPaymentKey(paymentKey);
        if (paymentNo == null) {
            throw new IllegalArgumentException("해당 paymentKey로 결제 정보를 찾을 수 없습니다: " + paymentKey);
        }

        // 2. 첫 번째 취소 내역 가져오기
        JsonNode cancelNode = tossResponseJson.get("cancels").get(0);

        // 3. DTO 생성
        RefundSaveDTO refund = new RefundSaveDTO();
        refund.setPaymentNo(paymentNo);
        refund.setRefundKey(cancelNode.get("transactionKey").asText());
        refund.setAmount(cancelNode.get("cancelAmount").asLong());
        refund.setStatus(cancelNode.get("cancelStatus").asText());
        refund.setReason(cancelNode.get("cancelReason").asText());

        // 4. 환불 완료 시간
        if (cancelNode.has("canceledAt") && !cancelNode.get("canceledAt").isNull()) {
            refund.setRefundedAt(LocalDateTime.parse(cancelNode.get("canceledAt").asText().replace("Z", "")));
        }

        // 5. 메타데이터 (선택)
        if (cancelNode.has("metadata") && !cancelNode.get("metadata").isNull()) {
            try {
                Map<String, Object> metadataMap = objectMapper.convertValue(
                        cancelNode.get("metadata"),
                        new TypeReference<>() {}
                );
                refund.setMetadata(objectMapper.writeValueAsString(metadataMap));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("메타데이터 직렬화 실패", e);
            }
        }

        // 6. DB 저장
        paymentMapper.insertRefund(refund);
    }
}

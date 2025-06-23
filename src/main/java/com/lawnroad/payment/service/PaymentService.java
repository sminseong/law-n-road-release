package com.lawnroad.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.lawnroad.payment.dto.PaymentSaveDTO;
import com.lawnroad.payment.mapper.PaymentMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PaymentService {

    private final PaymentMapper paymentMapper;

    public PaymentService(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }

    /**
     * JsonNode 에서 값을 직접 꺼내서 PaymentSaveDTO 조립 → INSERT
     */
    public void savePaymentFromToss(JsonNode root, Long orderNo) {
        PaymentSaveDTO dto = new PaymentSaveDTO();
        dto.setOrderNo(orderNo);
        dto.setPaymentKey(root.path("paymentKey").asText());
        dto.setOrderCode(root.path("orderId").asText());
        dto.setAmount(root.path("totalAmount").asLong());
        dto.setStatus(root.path("status").asText());

        // PG 정보 설정: method > easyPay.provider > mId
        String method = root.path("method").asText(null);
        if (method != null && !method.isEmpty()) {
            dto.setPg(method);
        } else if (!root.path("easyPay").isMissingNode() && !root.path("easyPay").path("provider").isNull()) {
            dto.setPg(root.path("easyPay").path("provider").asText());
        } else {
            dto.setPg(root.path("mId").asText(""));
        }

        // 카드 정보
        JsonNode card = root.path("card");
        if (!card.isMissingNode() && !card.isNull()) {
            dto.setInstallmentMonth(card.path("installmentPlanMonths").asInt());
        }

        // 구매 시각 (오프셋 포함 문자열 파싱)
        String approvedAt = root.path("approvedAt").asText(null);
        if (approvedAt != null && !approvedAt.isEmpty()) {
            OffsetDateTime odt = OffsetDateTime.parse(
                    approvedAt,
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
            );
            dto.setPurchasedAt(odt.toLocalDateTime());
        }

        // metadata JSON 문자열
        JsonNode md = root.path("metadata");
        if (!md.isMissingNode() && !md.isNull()) {
            dto.setMetadata(md.toString());
        }

        paymentMapper.insertPayment(dto);
    }
}

package com.lawnroad.payment.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawnroad.payment.dto.PaymentSaveDTO;
import com.lawnroad.payment.dto.RefundSaveDTO;
import com.lawnroad.payment.mapper.PaymentMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final ObjectMapper objectMapper;

    public PaymentService(PaymentMapper paymentMapper, ObjectMapper objectMapper) {
        this.paymentMapper = paymentMapper;
        this.objectMapper = objectMapper;
    }

    public void savePaymentFromToss(JsonNode tossResponseJson, Long orderNo) {
        PaymentSaveDTO dto = new PaymentSaveDTO();
        dto.setOrderNo(orderNo);
        dto.setPaymentKey(tossResponseJson.get("paymentKey").asText());
        dto.setOrderCode(tossResponseJson.get("orderId").asText());
        dto.setAmount(tossResponseJson.get("totalAmount").asLong());
        dto.setStatus(tossResponseJson.get("status").asText());
        dto.setPg(tossResponseJson.get("pg").asText());

        if (tossResponseJson.has("card")) {
            JsonNode card = tossResponseJson.get("card");
            dto.setCardCompany(card.get("company").asText());
            dto.setInstallmentMonth(card.get("installmentPlanMonths").asInt());
        }

        if (tossResponseJson.has("approvedAt")) {
            dto.setPurchasedAt(LocalDateTime.parse(tossResponseJson.get("approvedAt").asText().replace("Z", "")));
        }

        if (tossResponseJson.has("metadata")) {
            Map<String, Object> metadataMap = objectMapper.convertValue(
                    tossResponseJson.get("metadata"),
                    new TypeReference<>() {}
            );

            try {
                String metadataJson = objectMapper.writeValueAsString(metadataMap);
                dto.setMetadata(metadataJson);
            } catch (Exception e) {
                throw new RuntimeException("metadata 직렬화 실패", e);
            }
        }

        paymentMapper.insertPayment(dto);
    }


}

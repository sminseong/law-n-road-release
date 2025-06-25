package com.lawnroad.payment.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.lawnroad.payment.dto.RefundRequestDTO;

/**
 * 결제 성공 및 환불 후 도메인별 후처리를 담당하는 핸들러 인터페이스
 */
public interface PostPaymentHandler {
    /**
     * 이 핸들러가 처리할 주문 타입 (orders.order_type)
     * 예: "RESERVATION", "TEMPLATE", "ADVERTISEMENT"
     */
    String getOrderType();

    /**
     * 결제 성공 후 호출되어야 하는 후처리 로직
     *
     * @param orderNo 시스템 내 주문 PK
     * @param paymentResponse Toss API로부터 받은 결제 정보
     */
    void handlePaymentSuccess(Long orderNo, JsonNode paymentResponse);

    /**
     * 환불(취소) 완료 후 호출되어야 하는 후처리 로직
     *
     * @param orderNo 시스템 내 주문 PK
     * @param refundRequest 환불 요청 DTO (resourceNo, reason 등 담김)
     */
    void handleRefund(Long orderNo, RefundRequestDTO refundRequest);
}

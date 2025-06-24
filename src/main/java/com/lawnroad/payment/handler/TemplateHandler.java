//package com.lawnroad.payment.handler;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.lawnroad.payment.dto.RefundRequestDTO;
//import com.lawnroad.template.mapper.CartMapper;
//import com.lawnroad.template.mapper.CartMapper; // 혹은 tmpl_orders_history 전용 매퍼
//import com.lawnroad.template.mapper.CartMapper; // 위에서 사용한 동일 매퍼
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
///**
// * TEMPLATE 주문의 결제/환불 후처리를 담당합니다.
// */
//@Component
//public class TemplateHandler implements PostPaymentHandler {
//
//    private final CartMapper cartMapper;
//
//    public TemplateHandler(CartMapper cartMapper) {
//        this.cartMapper = cartMapper;
//    }
//
//    @Override
//    public String getOrderType() {
//        return "TEMPLATE";
//    }
//
//    @Override
//    public void handlePaymentSuccess(Long orderNo, JsonNode paymentResponse) {
//        // 로직 필요 없음
//    }
//
//    /**
//     * 환불 발생 시, 주문에 연결된 모든 tmpl_orders_history 레코드를 삭제하고
//     * 각 템플릿의 sales_count 를 복원합니다.
//     */
//    @Override
//    @Transactional
//    public void handleRefund(Long orderNo, RefundRequestDTO refundRequest) {
//        // 1) 이 주문의 히스토리에서 tmplNo 리스트를 조회
//        List<Long> tmplNos = cartMapper
//                .selectTmplNosByOrderNo(orderNo);
//
//        // 2) history 레코드 삭제
//        cartMapper.deleteHistoryByOrderNo(orderNo);
//
//        // 3) 해당 템플릿별로 sales_count 를 환불 수량만큼 감소
//        tmplNos.forEach(tmplNo ->
//                cartMapper.decrementSalesCount(tmplNo)
//        );
//    }
//}

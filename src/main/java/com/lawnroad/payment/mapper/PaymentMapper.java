package com.lawnroad.payment.mapper;

import com.lawnroad.payment.dto.PaymentSaveDTO;
import com.lawnroad.payment.dto.RefundSaveDTO;
import com.lawnroad.payment.model.PaymentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper {
    void insertPayment(PaymentSaveDTO paymentSaveDTO);

    Long findPaymentNoByPaymentKey(String paymentKey);

    void insertRefund(RefundSaveDTO refund);

    Long findOrderNoByPaymentKey(String paymentKey);

    String findPaymentKeyByOrderNo(@Param("orderNo") Long orderNo);

    void updatePaymentStatus(
            @Param("orderNo") Long orderNo,
            @Param("status") String status
    );

}

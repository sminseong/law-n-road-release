package com.lawnroad.payment.mapper;

import com.lawnroad.payment.model.PaymentVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {
    void insertPayment(PaymentVO paymentVO);
}

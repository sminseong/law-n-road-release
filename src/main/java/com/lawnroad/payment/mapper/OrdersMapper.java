package com.lawnroad.payment.mapper;

import com.lawnroad.payment.dto.OrdersCreateDTO;
import com.lawnroad.payment.model.OrdersVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrdersMapper {

    void insertOrder(OrdersCreateDTO dto);

    void updateOrderStatus(@Param("orderNo") Long orderNo,
                           @Param("status") String status);

    OrdersVO selectOrder(@Param("orderNo") Long orderNo);
}

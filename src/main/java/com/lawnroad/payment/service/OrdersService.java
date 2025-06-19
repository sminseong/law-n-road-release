package com.lawnroad.payment.service;

import com.lawnroad.payment.dto.OrdersCreateDTO;
import com.lawnroad.payment.dto.OrdersStatusUpdateDTO;
import com.lawnroad.payment.mapper.OrdersMapper;
import com.lawnroad.payment.model.OrdersVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdersService {

    private final OrdersMapper ordersMapper;

    public OrdersService(OrdersMapper ordersMapper) {
        this.ordersMapper = ordersMapper;
    }

    // ✅ 외부에서 모든 필드(orderCode, userNo, amount 등)를 세팅해서 넘겨주는 방식
    @Transactional
    public Long createOrder(OrdersCreateDTO dto) {
        ordersMapper.insertOrder(dto);  // insert 시 no 자동 주입되어야 함
        return dto.getNo();             // insert 시 <selectKey>로 dto.no에 값 주입 필요
    }

    // 주문 상태 변경
    @Transactional
    public void changeStatus(OrdersStatusUpdateDTO dto) {
        ordersMapper.updateOrderStatus(dto.getOrderNo(), dto.getStatus());
    }

    // 단일 주문 조회
    @Transactional(readOnly = true)
    public OrdersVO getOrder(Long orderNo) {
        return ordersMapper.selectOrder(orderNo);
    }

    @Transactional(readOnly = true)
    public OrdersVO getOrderByCode(String orderCode) {
        return ordersMapper.selectByOrderCode(orderCode);
    }
}

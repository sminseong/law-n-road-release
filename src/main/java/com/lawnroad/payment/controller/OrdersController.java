package com.lawnroad.payment.controller;

import com.lawnroad.payment.dto.OrdersCreateDTO;
import com.lawnroad.payment.dto.OrdersStatusUpdateDTO;
import com.lawnroad.payment.dto.OrdersResponseDTO;
import com.lawnroad.payment.model.OrdersVO;
import com.lawnroad.payment.service.OrdersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    // 주문 생성
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrdersCreateDTO dto) {
        Long orderNo = ordersService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderNo);
    }


    // 주문 상태 변경
    @PutMapping("/status")
    public ResponseEntity<Void> updateStatus(@RequestBody OrdersStatusUpdateDTO dto) {
        ordersService.changeStatus(dto);
        return ResponseEntity.noContent().build();
    }


    // 단일 주문 조회
    @GetMapping("/{orderNo}")
    public ResponseEntity<OrdersResponseDTO> getOrder(@PathVariable Long orderNo) {
        OrdersVO vo = ordersService.getOrder(orderNo);
        OrdersResponseDTO response = new OrdersResponseDTO();
        response.setNo(vo.getNo());
        response.setUserNo(vo.getUserNo());
        response.setAmount(vo.getAmount());
        response.setStatus(vo.getStatus());
        response.setOrderType(vo.getOrderType());
        response.setCreatedAt(vo.getCreatedAt());
        response.setUpdatedAt(vo.getUpdatedAt());
        return ResponseEntity.ok(response);
    }
}
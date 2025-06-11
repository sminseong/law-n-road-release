package com.lawnroad.reservation.service;

import com.lawnroad.payment.dto.OrdersCreateDTO;
import com.lawnroad.payment.dto.OrdersStatusUpdateDTO;
import com.lawnroad.payment.service.OrdersService;
import com.lawnroad.reservation.dto.ReservationsCreateDTO;
import com.lawnroad.reservation.dto.ReservationsResponseDTO;
import com.lawnroad.reservation.dto.ReservationsUpdateDTO;
import com.lawnroad.reservation.mapper.ReservationsMapper;
import com.lawnroad.reservation.mapper.TimeSlotMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationsService {

    private final OrdersService ordersService;
    private final ReservationsMapper reservationsMapper;
    private final TimeSlotMapper timeSlotMapper;

    public ReservationsService(
            OrdersService ordersService,
            ReservationsMapper reservationsMapper,
            TimeSlotMapper timeSlotMapper
    ) {
        this.ordersService       = ordersService;
        this.reservationsMapper  = reservationsMapper;
        this.timeSlotMapper      = timeSlotMapper;
    }

    // 예약 신청
    @Transactional
    public void createReservation(ReservationsCreateDTO dto) {
        // 1) slotNo 기반으로 금액 조회
        Long amount = timeSlotMapper.getAmountBySlotNo(dto.getSlotNo());

        // 2) OrdersCreateDTO 오더 세팅
        OrdersCreateDTO orderDto = new OrdersCreateDTO();
        orderDto.setUserNo(dto.getUserNo());
        orderDto.setTotalAmount(amount);
        orderDto.setStatus("ORDERED");
        orderDto.setOrderType("RESERVATION");
        Long orderNo = ordersService.createOrder(orderDto);

        // 3) 예약 생성
        dto.setOrderNo(orderNo);
        reservationsMapper.insertReservation(dto);
    }
    
    // 사용자별 예약 조회
    @Transactional(readOnly = true)
    public List<ReservationsResponseDTO> getReservationsByUser(Long userNo) {
        return reservationsMapper.selectReservationsByUser(userNo);
    }
    // 주문 상태 변경
    @Transactional
    public void changeStatus(ReservationsUpdateDTO dto) {
        reservationsMapper.updateReservationStatus(dto.getReservationNo(), dto.getStatus());
    }
}

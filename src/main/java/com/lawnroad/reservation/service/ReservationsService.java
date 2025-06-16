package com.lawnroad.reservation.service;

import com.lawnroad.payment.dto.OrdersCreateDTO;
import com.lawnroad.payment.dto.OrdersStatusUpdateDTO;
import com.lawnroad.payment.service.OrdersService;
import com.lawnroad.reservation.dto.ReservationCountDTO;
import com.lawnroad.reservation.dto.ReservationsCreateDTO;
import com.lawnroad.reservation.dto.ReservationsResponseDTO;
import com.lawnroad.reservation.dto.ReservationsUpdateDTO;
import com.lawnroad.reservation.mapper.ReservationsMapper;
import com.lawnroad.reservation.mapper.TimeSlotMapper;
import com.lawnroad.reservation.model.TimeSlotVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

        OrdersCreateDTO orderDto = new OrdersCreateDTO();
        orderDto.setUserNo(dto.getUserNo());
        orderDto.setTotalAmount(amount);
        orderDto.setOrderType("RESERVATION");
        orderDto.setStatus("ORDERED");
        orderDto.setOrderCode("ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        Long orderNo = ordersService.createOrder(orderDto);
        dto.setOrderNo(orderNo);

        // 2) reservations 삽입
        reservationsMapper.insertReservation(dto);

        // 3) 슬롯 상태 업데이트(0: 예약됨)
        TimeSlotVO slot = timeSlotMapper.selectBySlotNo(dto.getSlotNo());

        TimeSlotVO updatedSlot = new TimeSlotVO(
                slot.getNo(),
                slot.getUserNo(),
                slot.getSlotDate(),
                slot.getSlotTime(),
                0,
                slot.getAmount()
        );
        timeSlotMapper.updateStatus(updatedSlot);
    }
    
    // 사용자별 예약 조회
    @Transactional(readOnly = true)
    public List<ReservationsResponseDTO> getReservationsByUser(Long userNo) {
        return reservationsMapper.selectReservationsByUser(userNo);
    }

    @Transactional(readOnly = true)
    public ReservationCountDTO countByStatus(Long userNo) {
        int requested = reservationsMapper.countByUserNoAndStatus(userNo, "REQUESTED");
        int done      = reservationsMapper.countByUserNoAndStatus(userNo, "DONE");
        return new ReservationCountDTO(requested, done);
    }
    @Transactional(readOnly = true)
    public List<ReservationsResponseDTO> getReservationsByLawyer(Long lawyerNo) {
        return reservationsMapper.selectByLawyerNo(lawyerNo);
    }

    @Transactional
    public void changeToClosed(Long reservationNo) {
        reservationsMapper.updateReservationStatus(reservationNo, "DONE");
    }
}

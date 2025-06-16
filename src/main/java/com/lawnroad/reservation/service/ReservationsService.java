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
    public Long createReservation(ReservationsCreateDTO dto) {
        // 1) 금액 조회 + 주문 생성
        Long amount = timeSlotMapper.getAmountBySlotNo(dto.getSlotNo());

        OrdersCreateDTO orderDto = new OrdersCreateDTO();
        orderDto.setUserNo(dto.getUserNo());
        orderDto.setAmount(amount);
        orderDto.setOrderType("RESERVATION");
        orderDto.setStatus("ORDERED");
        orderDto.setOrderCode("ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        Long orderNo = ordersService.createOrder(orderDto);
        dto.setOrderNo(orderNo);

        // 2) 예약 저장
        reservationsMapper.insertReservation(dto);

        // 3) 슬롯 상태 변경
        TimeSlotVO slot = timeSlotMapper.selectBySlotNo(dto.getSlotNo());

        TimeSlotVO updatedSlot = new TimeSlotVO(
                slot.getNo(), slot.getUserNo(), slot.getSlotDate(),
                slot.getSlotTime(), 0, slot.getAmount()
        );
        timeSlotMapper.updateStatus(updatedSlot);

        // ⭐️ 슬롯의 시간/금액 값을 dto 에 주입 (컨트롤러 응답용)
        dto.setSlotTime(slot.getSlotTime());
        dto.setAmount(slot.getAmount());

        return dto.getNo(); // 예약 번호
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

    @Transactional(readOnly = true)
    public ReservationsResponseDTO getReservationByNo(Long reservationNo) {
        return reservationsMapper.selectReservationByNo(reservationNo);
    }

}

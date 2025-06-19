package com.lawnroad.reservation.service;

import com.lawnroad.payment.dto.OrdersCreateDTO;
import com.lawnroad.payment.service.OrdersService;
import com.lawnroad.reservation.dto.ReservationCountDTO;
import com.lawnroad.reservation.dto.ReservationsCreateDTO;
import com.lawnroad.reservation.dto.ReservationsResponseDTO;
import com.lawnroad.reservation.mapper.ReservationsMapper;
import com.lawnroad.reservation.mapper.TimeSlotMapper;
import com.lawnroad.reservation.model.TimeSlotVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ReservationsService {

    private final OrdersService      ordersService;
    private final ReservationsMapper reservationsMapper;
    private final TimeSlotMapper     timeSlotMapper;

    public ReservationsService(
            OrdersService ordersService,
            ReservationsMapper reservationsMapper,
            TimeSlotMapper timeSlotMapper
    ) {
        this.ordersService       = ordersService;
        this.reservationsMapper  = reservationsMapper;
        this.timeSlotMapper      = timeSlotMapper;
    }

    /** 1) 예약 신청: 주문 생성 → 예약 저장 → 슬롯 상태 변경 → 생성된 예약 PK 반환 */
    @Transactional
    public Long createReservation(ReservationsCreateDTO dto) {
        // (1) 해당 슬롯의 금액 조회
        Long amount = timeSlotMapper.getAmountBySlotNo(dto.getSlotNo());

        // (2) 주문 생성
        OrdersCreateDTO orderDto = new OrdersCreateDTO();
        orderDto.setUserNo(dto.getUserNo());
        orderDto.setAmount(amount);
        orderDto.setOrderType("RESERVATION");
        orderDto.setStatus("ORDERED");
        orderDto.setOrderCode("ORD-" +
                UUID.randomUUID().toString().replace("-", "").substring(0, 16)
        );
        Long orderNo = ordersService.createOrder(orderDto);
        dto.setOrderNo(orderNo);

        // (3) 예약 저장
        reservationsMapper.insertReservation(dto);

        // (4) 슬롯 정보 조회
        TimeSlotVO slot = timeSlotMapper.selectBySlotNo(dto.getSlotNo());

        // (5) 슬롯 상태 변경 (0: 예약불가)
        TimeSlotVO updatedSlot = new TimeSlotVO(
                slot.getNo(),
                slot.getUserNo(),
                slot.getSlotDate(),
                slot.getSlotTime(),
                0,
                slot.getAmount()
        );
        timeSlotMapper.updateStatus(updatedSlot);

        // (6) 컨트롤러 응답용 DTO 필드 주입
        dto.setSlotTime(slot.getSlotTime());
        dto.setAmount(slot.getAmount());
        dto.setOrderCode(orderDto.getOrderCode());

        // (7) 생성된 reservation PK 반환
        return dto.getNo();
    }

    /** 사용자별 예약 목록 조회 */
    @Transactional(readOnly = true)
    public List<ReservationsResponseDTO> getReservationsByUser(Long userNo) {
        return reservationsMapper.selectReservationsByUser(userNo);
    }

    /** 사용자별 상태별 카운트 */
    @Transactional(readOnly = true)
    public ReservationCountDTO countByStatus(Long userNo) {
        int requested = reservationsMapper.countByUserNoAndStatus(userNo, "REQUESTED");
        int done      = reservationsMapper.countByUserNoAndStatus(userNo, "DONE");
        return new ReservationCountDTO(requested, done);
    }

    /** 변호사별 예약 조회 */
    @Transactional(readOnly = true)
    public List<ReservationsResponseDTO> getReservationsByLawyer(Long lawyerNo) {
        return reservationsMapper.selectByLawyerNo(lawyerNo);
    }

    /** 예약 완료 처리 */
    @Transactional
    public void changeToClosed(Long reservationNo) {
        reservationsMapper.updateReservationStatus(reservationNo, "DONE");
    }

    /** 단일 예약 상세 조회 */
    @Transactional(readOnly = true)
    public ReservationsResponseDTO getReservationByNo(Long reservationNo) {
        return reservationsMapper.selectReservationByNo(reservationNo);
    }
}

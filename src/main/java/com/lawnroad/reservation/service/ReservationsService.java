package com.lawnroad.reservation.service;

import com.lawnroad.payment.dto.OrdersCreateDTO;
import com.lawnroad.payment.service.OrdersService;
import com.lawnroad.reservation.dto.ReservationCountDTO;
import com.lawnroad.reservation.dto.ReservationsCreateDTO;
import com.lawnroad.reservation.dto.ReservationsResponseDTO;
import com.lawnroad.reservation.mapper.ReservationsMapper;
import com.lawnroad.reservation.mapper.TimeSlotMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
        this.ordersService = ordersService;
        this.reservationsMapper = reservationsMapper;
        this.timeSlotMapper = timeSlotMapper;
    }

    /**
     * 1) 예약 신청: 슬롯 원자적 예약 → 주문 생성 → 예약 저장
     */
    @Transactional
    public Long createReservation(ReservationsCreateDTO dto) {
        try {
            // 1) 조건부 UPDATE – status=1인 경우에만 status=0으로 변경
            int updated = timeSlotMapper.reserveSlotIfAvailable(dto.getSlotNo());
            if (updated == 0) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "이미 예약된 시간입니다. 다른 시간을 선택해 주세요."
                );
            }

            // 2) 예약 금액 조회
            Long amount = timeSlotMapper.getAmountBySlotNo(dto.getSlotNo());

            // 3) 주문 생성
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

            // 4) 예약 저장
            reservationsMapper.insertReservation(dto);

            return dto.getNo();

        } catch (DataAccessException | ResponseStatusException ex) {
            // 데드락, 제약 위반 등 모든 DB 예외를 충돌로 간주
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 예약된 시간입니다. 다른 시간을 선택해 주세요."
            );
        }
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
        int done = reservationsMapper.countByUserNoAndStatus(userNo, "DONE");
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

    public ReservationsResponseDTO getReservationDetail(Long reservationNo) {
        return reservationsMapper.selectReservationByNo(reservationNo);
    }
}

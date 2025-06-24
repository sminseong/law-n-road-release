package com.lawnroad.payment.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.lawnroad.payment.dto.RefundRequestDTO;
import com.lawnroad.payment.handler.PostPaymentHandler;
import com.lawnroad.reservation.dto.ReservationsResponseDTO;
import com.lawnroad.reservation.mapper.ReservationsMapper;
import com.lawnroad.reservation.mapper.TimeSlotMapper;
import com.lawnroad.reservation.model.TimeSlotVO;
import com.lawnroad.payment.mapper.PaymentMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reservation 도메인의 결제 및 환불 후처리를 담당합니다.
 */
@Component
public class ReservationHandler implements PostPaymentHandler {

    private final ReservationsMapper reservationsMapper;
    private final TimeSlotMapper     timeSlotMapper;
    private final PaymentMapper      paymentMapper;

    public ReservationHandler(ReservationsMapper reservationsMapper,
                              TimeSlotMapper timeSlotMapper,
                              PaymentMapper paymentMapper) {
        this.reservationsMapper = reservationsMapper;
        this.timeSlotMapper    = timeSlotMapper;
        this.paymentMapper     = paymentMapper;
    }

    @Override
    public String getOrderType() {
        return "RESERVATION";
    }

    /**
     * 결제 성공 후, reservation 상태를 "PAID"로 변경합니다.
     */
    @Override
    @Transactional
    public void handlePaymentSuccess(Long orderNo, JsonNode paymentResponse) {
        ReservationsResponseDTO reservation =
                reservationsMapper.selectReservationByOrderNo(orderNo);
        if (reservation == null) {
            throw new IllegalStateException(
                    "Reservation not found for orderNo=" + orderNo);
        }
        reservationsMapper.updateReservationStatus(
                reservation.getNo(), "REQUESTED");
    }

    /**
     * 환불 완료 시, reservation 상태를 "CANCELED"로 업데이트하고
     * 해당 슬롯을 예약 가능 상태로 복원하며
     * 결제 상태도 "CANCELED"로 변경합니다.
     */
    @Override
    @Transactional
    public void handleRefund(Long orderNo, RefundRequestDTO refundRequest) {
        ReservationsResponseDTO reservation =
                reservationsMapper.selectReservationByOrderNo(orderNo);
        if (reservation == null) {
            throw new IllegalStateException(
                    "Reservation not found for orderNo=" + orderNo);
        }
        // (1) 예약 상태 취소
        reservationsMapper.updateReservationStatus(
                reservation.getNo(), "CANCELED");

        // (2) 슬롯 복원: 기존 객체를 복사하여 새로운 객체 생성
        TimeSlotVO slot = timeSlotMapper.selectBySlotNo(reservation.getSlotNo());
        TimeSlotVO restored = new TimeSlotVO(
                slot.getNo(),
                slot.getUserNo(),
                slot.getSlotDate(),
                slot.getSlotTime(),
                1,
                slot.getAmount());
        timeSlotMapper.updateStatus(restored);

        // (3) 결제 상태 변경
        paymentMapper.updatePaymentStatus(orderNo, "CANCELED");
    }
}

package com.lawnroad.reservation.domain;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
public class TimeSlotVO {
    private final Long no;
    private final Long lawyerNo;
    private final LocalDate slotDate;
    private final LocalTime slotTime;
    private final int status; // 0 : 예약 불가, 1 : 예약 가능
    private final Long amount;

    public TimeSlotVO(Long no, Long lawyerNo, LocalDate slotDate, LocalTime slotTime,
                      int status, Long amount) {
        this.no = no;
        this.lawyerNo = lawyerNo;
        this.slotDate = slotDate;
        this.slotTime = slotTime;
        this.status = status;
        this.amount = amount;
    }

    // 슬롯이 겹치는지 판단
    public boolean isOverlapping(TimeSlotVO other) {
        return this.lawyerNo.equals(other.lawyerNo)
                && this.slotDate.equals(other.slotDate)
                && this.slotTime.equals(other.slotTime);
    }
}

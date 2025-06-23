package com.lawnroad.reservation.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationsVO {
    public enum Status {
        REQUESTED, CANCELED, DONE
    }

    private final Long no;
    private final Long orderNo;
    private final Long slotNo;
    private final Long userNo;
    private final Status status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ReservationsVO(Long no,
                          Long orderNo,
                          Long slotNo,
                          Long userNo,
                          Status status,
                          LocalDateTime createdAt,
                          LocalDateTime updatedAt) {
        this.no = no;
        this.orderNo = orderNo;
        this.slotNo = slotNo;
        this.userNo = userNo;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

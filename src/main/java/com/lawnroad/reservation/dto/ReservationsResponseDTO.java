package com.lawnroad.reservation.dto;

import com.lawnroad.reservation.domain.ReservationsVO.Status;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservationsResponseDTO {

    private Long no;
    private Long orderNo;
    private Long userNo;
    private String userName;     // 화면에 보여줄 사용자 이름 (JOIN)
    private Long slotNo;
    private LocalDate slotDate;     // 화면에 보여줄 예약 날짜 (JOIN)
    private LocalTime slotTime;     // 화면에 보여줄 예약 시간 (JOIN)
    private Status status;
    private Long amount; // 화면에 보여줄 금액 (JOIN)
}


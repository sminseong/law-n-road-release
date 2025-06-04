package com.lawnroad.notification.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 상담 예약 취소
public class LawyerReservationCanceledDto {
  private String to; // 수신자 전화번호 (변호사)
  private String lawyer; // 변호사 이름 (#{lawyer})
  private String client; // 의뢰인 이름 (#{client})
  private String datetime; // 취소된 상담 일시 (#{datetime})
}

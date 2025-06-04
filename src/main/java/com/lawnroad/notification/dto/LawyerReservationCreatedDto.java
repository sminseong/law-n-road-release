package com.lawnroad.notification.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 상담 신청 완료 (변호사용)
public class LawyerReservationCreatedDto {
  private String to; // 수신자 전화번호 (변호사)
  private String lawyer; // 변호사 이름 (#{lawyer})
  private String client; // 신청자 이름 (#{client})
  private String datetime; // 상담 일시 (#{datetime})
  private String summary; // 상담 내용 (#{summary})
}

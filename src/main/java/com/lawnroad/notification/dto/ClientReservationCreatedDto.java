package com.lawnroad.notification.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 상담 신청 완료 (의뢰인용)
public class ClientReservationCreatedDto {
  private String to; // 수신자 (의뢰인) 전화번호
  private String client; // 의뢰인 이름 (#{client})
  private String lawyer; // 상담 대상 변호사 이름 (#{lawyer})
  private String datetime; // 상담 일시 (#{datetime})
  private String summary; // 상담 요약 (#{summary})
}

package com.lawnroad.notification.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 상담 시간 임박 (의뢰인)
public class ClientReservationStartedDto {
  private String to; // 수신자 전화번호 (의뢰인)
  private String client; // 의뢰인 이름 (#{client})
  private String lawyer; // 담당 변호사 이름 (#{lawyer})
  private String datetime; // 상담 일시 (#{datetime})
}

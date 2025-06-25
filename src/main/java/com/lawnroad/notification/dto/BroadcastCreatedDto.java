package com.lawnroad.notification.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 관심 방송 시작 알림
@Getter
@Setter
@ToString
public class BroadcastCreatedDto {
  // 수신자 번호
  private String to;
  // 수신자 명
  private String name;
  // 변호사 명
  private String lawyer;
  // 방송 제목
  private String title;
  // 방송 시작 시간
  private String start;
}

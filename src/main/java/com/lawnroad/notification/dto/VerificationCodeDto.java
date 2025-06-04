package com.lawnroad.notification.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 본인 인증번호 발송
public class VerificationCodeDto {
  // 수신 번호
  private String to;
  // 인증번호
  private String code;
}

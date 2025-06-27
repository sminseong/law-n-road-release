package com.lawnroad.account.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatReportConfirmDto {


    private Long no;                  // 신고 번호
    private Long reportedUserNo;      // 신고자 번호
    private Long userNo;              // 피신고자 번호
    private String nickname;          // 피신고자 닉네임
    private String message;           // 신고 내용
    private Integer reportStatus;     // 처리 상태
    private LocalDateTime createdAt;  // 신고 일시
}

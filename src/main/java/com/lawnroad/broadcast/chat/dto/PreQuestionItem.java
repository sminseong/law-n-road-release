package com.lawnroad.broadcast.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreQuestionItem {
    private Long no;                  // 사전질문 PK
    private Long userNo;              // 작성자 유저번호
    private Long scheduleNo;          // 방송/스케줄 번호
    private String nickname;          // 작성자 닉네임
    private String preQuestionContent;// 질문 내용
}


package com.lawnroad.board.dto;

import lombok.Data;

@Data
public class AnswerRegisterDto {
    private Long boardNo;
    private Long userNo;
    private String content;
}

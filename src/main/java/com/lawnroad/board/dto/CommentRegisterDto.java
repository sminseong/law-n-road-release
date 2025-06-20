package com.lawnroad.board.dto;

import lombok.Data;

@Data
public class CommentRegisterDto {
    private Long boardNo;
    private Long userNo;
    private String content;
}

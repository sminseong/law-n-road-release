package com.lawnroad.board.dto;

import lombok.Data;

@Data
public class MyCommentResponseDto {
    private Long commentId;
    private Long boardNo;
    private String boardTitle;
    private String content;
    private String createdAt;
}

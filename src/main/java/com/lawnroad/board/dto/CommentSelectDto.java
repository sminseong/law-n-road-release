package com.lawnroad.board.dto;

import lombok.Data;

@Data
public class CommentSelectDto {
    private Long commentId;
    private Long boardId;
    private Long userNo;  // 요청자(게시글 작성자)
}

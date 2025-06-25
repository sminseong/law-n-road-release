package com.lawnroad.board.dto;

import lombok.Data;

@Data
public class CommentSelectDto {
    private Long commentNo;
    private Long boardNo;
    private Long userNo;  // 게시글 작성자 번호
}

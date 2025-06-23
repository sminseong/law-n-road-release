package com.lawnroad.board.dto;

import lombok.Data;

@Data
//답변 수정 요청을 처리하기 위한 DTO
public class CommentUpdateDto {
    private Long boardNo; //답변이 작성된 대상 게시글 번호
    private Long userNo; //답변을 작성한 사용자 번호
    private String content; //수정된 답변 내용
}

package com.lawnroad.board.dto;

import lombok.Data;

@Data
//변호사 답변(댓글) 등록을 위한 DTO 클래스
public class CommentRegisterDto {
    private Long boardNo; //답변을 작성할 대상 게시글 번호
    private Long userNo; //답변을 작성한 사용자 번호
    private String content; //실제 작성한 답변 내용
}

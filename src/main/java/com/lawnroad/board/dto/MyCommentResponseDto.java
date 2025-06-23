package com.lawnroad.board.dto;

import lombok.Data;

@Data
//변호사 본인이 작성한 댓글(답변) 목록 조회 시 사용되는 응답 DTO
public class MyCommentResponseDto {
    private Long commentId; //답변의 고유ID
    private Long boardNo; //댓글이 달린 게시글 ID
    private String boardTitle; //댓글이 달린 게시글의 제목
    private String content; //작성한 댓글 내용
    private String createdAt; //댓글 작성일시
}

package com.lawnroad.board.dto;

import lombok.Data;

@Data
//댓글(답변) 상세 조회 응답 DTO
public class CommentDetailDto {
    private Long commentId;           // 댓글 고유 ID
    private Long boardNo;             // 연결된 상담글 ID
    private Long userNo;              // 댓글을 작성한 변호사의 사용자 번호
    private String lawyerName;        // 변호사 이름
    private String lawyerProfileImg;  // 프로필 이미지 URL
    private String content;           // 답변 내용
    private String createdAt;  // 작성일
}

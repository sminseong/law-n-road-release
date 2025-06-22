package com.lawnroad.board.dto;

import lombok.Data;

@Data
//Q&a 게시글의 답변 조회 응답용 DTO
public class CommentResponse {
    private String lawyerName; //답변을 작성한 변호사의 이름
    private String lawyerProfileImage; //답변 작성자의 프로필 이미지 경로
    private String content; //답변 내용
    private String  createdAt; //답변 작성일
    private Long userNo; // ✅ 이거 추가!
}

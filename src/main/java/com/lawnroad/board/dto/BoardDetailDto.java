package com.lawnroad.board.dto;

import lombok.Data;

import java.time.LocalDate;

//게시글 상세조회 시 사용하는 DTO
@Data
public class BoardDetailDto {
    private Long no; //게시글 번호
    private Long categoryNo; //카테고리 번호
    private String categoryName; //카테고리 이름
    private Long userNo; //작성자 번호
    private String title; //제목
    private String content; //내용
    private LocalDate incidentDate; //사건발생일자
    private String createdAt; //생성일자
    private String updatedAt; //수정일자
}

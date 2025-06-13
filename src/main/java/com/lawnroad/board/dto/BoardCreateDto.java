package com.lawnroad.board.dto;

import lombok.Data;

import java.time.LocalDate;

//게시글 등록 요청 DTO
@Data
public class BoardCreateDto {
    private String title;
    private String content;
    private LocalDate incidentDate;
    private Long userNo; // 추후 로그인 사용자로부터 받기
    private Long categoryNo;
}

package com.lawnroad.board.dto;

import lombok.Data;

import java.time.LocalDate;

//게시글 등록 요청 DTO
@Data
public class BoardCreateDto {
    private String title;
    private String content;
    private LocalDate incidentDate;
    private Long userNo;
    private Long categoryNo;
}

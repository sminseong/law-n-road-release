package com.lawnroad.board.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BoardUpdateDto {
    private Long no; // 게시글 번호
    private Long categoryNo;
    private String title;
    private String content;
    private LocalDate incidentDate;
}

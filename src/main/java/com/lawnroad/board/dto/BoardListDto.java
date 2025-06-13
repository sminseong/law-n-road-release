package com.lawnroad.board.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BoardListDto{
    private Long no;
    private Long categoryNo;
    private Long userNo;
    private String title;
    private String summary;
    private LocalDate incidentDate;
    private LocalDateTime createdAt;
}

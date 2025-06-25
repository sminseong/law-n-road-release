package com.lawnroad.board.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BoardSummaryDto {
    private Long boardNo;
    private String title;
    private LocalDate incidentDate;
    private String categoryName;
}

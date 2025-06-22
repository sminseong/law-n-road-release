package com.lawnroad.mainsearch.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BoardDto {
  private Long no;
  private Long categoryNo;
  private String title;
  private String content;
  private LocalDate incidentDate;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
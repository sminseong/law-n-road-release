package com.lawnroad.mainsearch.dto;

import lombok.Data;

@Data
public class SearchBoardDto {
  private Long no;
  private Long categoryNo;
  private Long userNo;
  private String title;
  private String content;
}

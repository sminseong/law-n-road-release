package com.lawnroad.mainsearch.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchResponseDto {
  private List<SearchLawyerDto> lawyers;
  private List<SearchTemplateDto> templates;
  private List<SearchBoardDto> qnas;
}

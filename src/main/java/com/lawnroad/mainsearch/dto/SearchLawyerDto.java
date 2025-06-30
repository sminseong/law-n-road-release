package com.lawnroad.mainsearch.dto;

import com.google.api.client.util.DateTime;
import lombok.Data;

@Data
public class SearchLawyerDto {
  private Long no;
  private String profile;
  private String name;
  private Integer consultPrice;
  private String officeName;
  private String lawyerIntro;
  private Integer score;
  private Integer point;
  private Integer templateCount;
  private Integer qnaAnswerCount;
}

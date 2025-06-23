package com.lawnroad.mainsearch.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BoardHomepageDto {
  private Long no;
  private Long categoryNo;
  private String title;
  private String content;
  private LocalDate incidentDate;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  
  // 선택 필드
  private Long userNo;                // 작성자 번호
  private String userName;            // 작성자 이름
  private Integer viewCount;          // 조회수
  private Integer commentCount;       // 답변 개수
  private Boolean hasSelectedComment; // 채택 답변 여부
}
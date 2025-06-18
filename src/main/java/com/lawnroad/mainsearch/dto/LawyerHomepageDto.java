package com.lawnroad.mainsearch.dto;

import com.lawnroad.board.dto.BoardListDto;
import com.lawnroad.template.dto.TemplateDto;
import lombok.Data;

import java.util.List;

@Data
public class LawyerHomepageDto {
  private Long lawyerNo; // 변호사 번호
  private String name; // 이름
  private String shortIntro; // 자기소개 요약
  private String longIntro; // 자기소개 상세
  private String profileImagePath; // 프로필 이미지
  private int consultPrice; // 상담비용
  
  private String officeName; // 사무실 이름
  private String officeAddress; // 주소
  private String officePhone; // 전화번호
  private String email; // 이메일
  
  // 최근 방송 다시보기 (10개만)
  // TODO: 나중에 추가
  
  // 인기순, 최근 템플릿 (10개만)
  private List<TemplateDto> recentTemplates;
  
  // 최근 상담글 (5개만)
  private List<BoardListDto> recentBoards;
}
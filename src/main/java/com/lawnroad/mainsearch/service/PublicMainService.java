package com.lawnroad.mainsearch.service;

import com.lawnroad.mainsearch.dto.BoardDto;
import com.lawnroad.mainsearch.dto.LawyerAdBannerDto;
import com.lawnroad.template.dto.TemplateDto;

import java.util.List;

public interface PublicMainService {
  // 메인 페이지 메인 베너 조회
  List<LawyerAdBannerDto> getActiveMainBanners();
  
  // 메인 페이지 최신 게시글 5개 조회
  List<BoardDto> getLatestBoards();
  
  // 인기 상품 10개 조회
  List<TemplateDto> getPopularTemplates();
}

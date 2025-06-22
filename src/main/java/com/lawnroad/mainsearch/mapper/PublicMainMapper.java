package com.lawnroad.mainsearch.mapper;

import com.lawnroad.mainsearch.dto.BoardDto;
import com.lawnroad.mainsearch.dto.LawyerAdBannerDto;
import com.lawnroad.template.dto.TemplateDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PublicMainMapper {
  // 메인 페이지 메인 베너 조회
  List<LawyerAdBannerDto> selectActiveMainBanners();
  
  // 메인 페이지 최신 게시글 5개 조회
  List<BoardDto> selectLatestBoards();
  
  // 인기 상품 10개 조회
  List<TemplateDto> selectRecentTemplatesByLawyerNo();
}

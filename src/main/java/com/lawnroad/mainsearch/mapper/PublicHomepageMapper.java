package com.lawnroad.mainsearch.mapper;

import com.lawnroad.mainsearch.dto.BoardHomepageDto;
import com.lawnroad.mainsearch.dto.LawyerHomepageDto;
import com.lawnroad.template.dto.TemplateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PublicHomepageMapper {
  // 변호사 홈페이지에 보여질 메타 데이터
  LawyerHomepageDto selectLawyerHomepageInfo(@Param("lawyerNo") Long lawyerNo);
  
  // 변호사 홈페이지에 보여질, 그 변호사가 작성한 템플릿 (인기/최신순)
  List<TemplateDto> selectRecentTemplatesByLawyerNo(@Param("lawyerNo") Long lawyerNo);
  
  // 변호사 홈페이지에 보여질, 그 변호사가 답변한 상담글 (최신순)
  List<BoardHomepageDto> selectLatestBoardsByLawyer(@Param("lawyerNo") Long lawyerNo);
}

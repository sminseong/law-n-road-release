package com.lawnroad.mainsearch.mapper;

import com.lawnroad.mainsearch.dto.SearchBoardDto;
import com.lawnroad.mainsearch.dto.SearchLawyerDto;
import com.lawnroad.mainsearch.dto.SearchTemplateDto;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PublicSearchMapper {
  
  // 통합검색 - 변호사 검색결과
  List<SearchLawyerDto> searchLawyers(@Param("keywords") List<String> keywords);
  
  // 통합검색 - 템플릿 검색 결과
  List<SearchTemplateDto> searchTemplates(
      @Param("keywords") List<String> keywords,
      @Param("category") Long category
  );
  
  // 통합검색 - qna 검색 결과
  List<SearchBoardDto> searchBoards(
      @Param("keywords") List<String> keywords,
      @Param("category") Long category
  );
  
}

package com.lawnroad.mainsearch.service;

import com.lawnroad.mainsearch.dto.SearchResponseDto;

public interface PublicSearchService {
  
  // 통합검색 (변호사, QNA, 템플릿)
  SearchResponseDto search(String keyword, Long category);
}

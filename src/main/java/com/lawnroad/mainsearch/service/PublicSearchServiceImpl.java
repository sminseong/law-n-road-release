package com.lawnroad.mainsearch.service;

import com.lawnroad.mainsearch.dto.SearchBoardDto;
import com.lawnroad.mainsearch.dto.SearchLawyerDto;
import com.lawnroad.mainsearch.dto.SearchResponseDto;
import com.lawnroad.mainsearch.dto.SearchTemplateDto;
import com.lawnroad.mainsearch.mapper.PublicSearchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicSearchServiceImpl implements PublicSearchService {
  private final PublicSearchMapper mapper;
  
  @Override
  public SearchResponseDto search(String keyword, Long category) {
    SearchResponseDto resp = new SearchResponseDto();
    
    if (!StringUtils.hasText(keyword)) {
      return resp;
    }
    List<String> kws = Arrays.stream(keyword.trim().split("\\s+"))
        .filter(k -> !"변호사".equalsIgnoreCase(k))
        .collect(Collectors.toList());
    
    List<SearchLawyerDto> lawyers = mapper.searchLawyers(kws);
    List<SearchTemplateDto> templates = mapper.searchTemplates(kws, category);
    List<SearchBoardDto> qnas = mapper.searchBoards(kws, category);
    
    resp.setLawyers(lawyers);
    resp.setTemplates(templates);
    resp.setQnas(qnas);
    
    return resp;
  }
}
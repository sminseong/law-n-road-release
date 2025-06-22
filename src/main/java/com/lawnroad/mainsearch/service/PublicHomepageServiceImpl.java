package com.lawnroad.mainsearch.service;

import com.lawnroad.mainsearch.dto.LawyerHomepageDto;
import com.lawnroad.mainsearch.mapper.PublicHomepageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublicHomepageServiceImpl implements PublicHomepageService {
  
  private final PublicHomepageMapper publicHomepageMapper;
  
  @Override
  public LawyerHomepageDto getLawyerHomepage(Long lawyerNo) {
    LawyerHomepageDto dto = publicHomepageMapper.selectLawyerHomepageInfo(lawyerNo);
    dto.setRecentTemplates(publicHomepageMapper.selectRecentTemplatesByLawyerNo(lawyerNo));
    dto.setRecentBoards(publicHomepageMapper.selectLatestBoardsByLawyer(lawyerNo));
    return dto;
  }
}

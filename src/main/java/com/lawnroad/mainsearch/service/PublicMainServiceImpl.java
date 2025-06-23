package com.lawnroad.mainsearch.service;

import com.lawnroad.mainsearch.dto.BoardHomepageDto;
import com.lawnroad.mainsearch.dto.LawyerAdBannerDto;
import com.lawnroad.mainsearch.dto.TopLawyerDto;
import com.lawnroad.mainsearch.mapper.PublicMainMapper;
import com.lawnroad.template.dto.TemplateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicMainServiceImpl implements PublicMainService {
  
  private final PublicMainMapper publicMainMapper;
  
  @Override
  public List<LawyerAdBannerDto> getActiveMainBanners() {
    return publicMainMapper.selectActiveMainBanners();
  }
  
  @Override
  public List<BoardHomepageDto> getLatestBoards() {
    return publicMainMapper.selectLatestBoards();
  }
  
  @Override
  public List<TemplateDto> getPopularTemplates() {
    return publicMainMapper.selectRecentTemplatesByLawyerNo();
  }
  
  @Override
  public List<LawyerAdBannerDto> getActiveSubBanners() {
    return publicMainMapper.selectActiveSubBanners();
  }
  
  @Override
  public List<TopLawyerDto> findTop3LawyersByViews() {
    return publicMainMapper.selectTop3LawyersByViews();
  }
  
}

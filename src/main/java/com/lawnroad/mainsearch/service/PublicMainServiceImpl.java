package com.lawnroad.mainsearch.service;

import com.lawnroad.mainsearch.dto.LawyerAdBannerDto;
import com.lawnroad.mainsearch.mapper.PublicMainMapper;
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
}

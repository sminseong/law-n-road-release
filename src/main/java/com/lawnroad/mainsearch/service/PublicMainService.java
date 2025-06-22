package com.lawnroad.mainsearch.service;

import com.lawnroad.mainsearch.dto.LawyerAdBannerDto;

import java.util.List;

public interface PublicMainService {
  // 메인 페이지 메인 베너 조회
  List<LawyerAdBannerDto> getActiveMainBanners();
}

package com.lawnroad.mainsearch.service;

import com.lawnroad.mainsearch.dto.LawyerBasicInfoDto;

public interface LawyerService {
    // 변호사 기본 정보 조회
  LawyerBasicInfoDto findLawyerBasicInfoByNo(Long lawyerNo);
}

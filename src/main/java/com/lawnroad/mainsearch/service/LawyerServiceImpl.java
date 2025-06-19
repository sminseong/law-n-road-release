package com.lawnroad.mainsearch.service;

import com.lawnroad.mainsearch.mapper.LawyerMainsearchMapper;
import com.lawnroad.mainsearch.dto.LawyerBasicInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LawyerServiceImpl implements LawyerService {
  
  private final LawyerMainsearchMapper lawyerMapper;
  
  // 변호사 기본 정보 조회
  @Override
  public LawyerBasicInfoDto findLawyerBasicInfoByNo(Long lawyerNo) {
    return lawyerMapper.selectLawyerBasicInfoByNo(lawyerNo);
  }
}

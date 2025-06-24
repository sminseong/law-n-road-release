package com.lawnroad.account.service;

import com.lawnroad.account.mapper.LawyerPointMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LawyerPointService {
  
  private final LawyerPointMapper lawyerPointMapper;
  
  // 포인트 지급
  public void addPoint(Long lawyerNo, int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("포인트 지급 금액은 0보다 커야 합니다.");
    }
    
    int result = lawyerPointMapper.updatePoint(lawyerNo, amount);
    if (result == 0) {
      throw new RuntimeException("포인트 지급 실패: 변호사를 찾을 수 없습니다.");
    }
  }
  
  // 포인트 차감 (부족하더라도 음수 허용)
  public void deductPoint(Long lawyerNo, int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("포인트 차감 금액은 0보다 커야 합니다.");
    }
    
    int result = lawyerPointMapper.updatePoint(lawyerNo, -amount);
    if (result == 0) {
      throw new RuntimeException("포인트 차감 실패: 변호사를 찾을 수 없습니다.");
    }
  }
}
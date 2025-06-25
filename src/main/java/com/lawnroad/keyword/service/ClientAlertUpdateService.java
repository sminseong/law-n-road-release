package com.lawnroad.keyword.service;

import com.lawnroad.keyword.dto.ClientAlertUpdateDto;

public interface ClientAlertUpdateService {
  
  /**
   * 클라이언트의 알림 설정을 조회
   */
  ClientAlertUpdateDto getAlertSettings(Long clientNo);
  
  /**
   * 알림 설정을 업데이트
   */
  void updateAlertSettings(ClientAlertUpdateDto dto);
}

package com.lawnroad.keyword.service;

import com.lawnroad.keyword.dto.ClientAlertUpdateDto;
import com.lawnroad.keyword.mapper.ClientAlertUpdateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientAlertUpdateServiceImpl implements ClientAlertUpdateService {
  
  private final ClientAlertUpdateMapper clientAlertUpdateMapper;
  
  /**
   * 클라이언트의 알림 설정 조회
   */
  @Override
  public ClientAlertUpdateDto getAlertSettings(Long clientNo) {
    ClientAlertUpdateDto alertSettings = clientAlertUpdateMapper.getAlertSettings(clientNo);
    
    // 클라이언트가 존재하지 않는 경우 기본값 반환
    if (alertSettings == null) {
      alertSettings = new ClientAlertUpdateDto();
      alertSettings.setClientNo(clientNo);
      alertSettings.setIsConsultAlert(true);  // 기본값: 상담 알림 활성화
      alertSettings.setIsKeywordAlert(true);  // 기본값: 키워드 알림 활성화
    }
    
    return alertSettings;
  }
  
  /**
   * 알림 설정 업데이트 요청 처리
   */
  @Override
  public void updateAlertSettings(ClientAlertUpdateDto dto) {
    clientAlertUpdateMapper.updateAlertSettings(dto);
  }
}

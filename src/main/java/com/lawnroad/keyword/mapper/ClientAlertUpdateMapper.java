package com.lawnroad.keyword.mapper;

import com.lawnroad.keyword.dto.ClientAlertUpdateDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClientAlertUpdateMapper {
  
  /**
   * 클라이언트의 알림 설정을 조회
   */
  ClientAlertUpdateDto getAlertSettings(Long clientNo);
  
  /**
   * 상담 알림 및 키워드 알림 설정을 업데이트
   */
  void updateAlertSettings(ClientAlertUpdateDto dto);
}

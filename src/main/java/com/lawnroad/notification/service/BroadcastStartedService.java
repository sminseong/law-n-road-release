package com.lawnroad.notification.service;

import com.lawnroad.common.config.SolapiConfig;
import com.lawnroad.notification.dto.BroadcastStartedDto;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.KakaoOption;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BroadcastStartedService {
  
  private final SolapiConfig solapiConfig;
  
  public void sendStartAlimtalk(BroadcastStartedDto dto, String templateId) {
    // SDK 초기화
    DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(
        solapiConfig.getApiKey(),
        solapiConfig.getApiSecret(),
        solapiConfig.getApiUrl()
    );
    
    // 알림톡 옵션 구성
    KakaoOption kakaoOption = new KakaoOption();
    kakaoOption.setPfId(solapiConfig.getPfId());
    kakaoOption.setTemplateId(templateId);  // 템플릿 ID 직접 지정
    
    Map<String, String> variables = new HashMap<>();
    variables.put("#{name}", dto.getName());
    variables.put("#{title}", dto.getTitle());
    variables.put("#{start}", dto.getStart());
    kakaoOption.setVariables(variables);
    
    // 메시지 구성
    Message message = new Message();
    message.setFrom(solapiConfig.getFrom());
    message.setTo(dto.getTo());
    message.setKakaoOptions(kakaoOption);
    
    // 발송 시도
    try {
      messageService.send(message);
    } catch (NurigoMessageNotReceivedException e) {
      System.out.println("❌ 발송 실패 - 실패한 메시지 목록:");
      System.out.println(e.getFailedMessageList());
    } catch (Exception e) {
      System.out.println("❌ 알림톡 발송 중 에러: " + e.getMessage());
    }
  }
}

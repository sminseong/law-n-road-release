package com.lawnroad.notification.service;

import com.lawnroad.common.config.SolapiConfig;
import com.lawnroad.notification.dto.LawyerReservationCreatedDto;
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
public class LawyerReservationCreatedService {
  
  private final SolapiConfig solapiConfig;
  
  public void send(LawyerReservationCreatedDto dto) {
    // Solapi SDK 초기화
    DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(
        solapiConfig.getApiKey(),
        solapiConfig.getApiSecret(),
        solapiConfig.getApiUrl()
    );
    
    // 알림톡 옵션 구성
    KakaoOption kakaoOption = new KakaoOption();
    kakaoOption.setPfId(solapiConfig.getPfId());
    kakaoOption.setTemplateId("KA01TP250604015047509fikX0YaLAvQ");
    
    Map<String, String> variables = new HashMap<>();
    variables.put("#{lawyer}", dto.getLawyer());
    variables.put("#{client}", dto.getClient());
    variables.put("#{datetime}", dto.getDatetime());
    variables.put("#{summary}", dto.getSummary());
    kakaoOption.setVariables(variables);
    
    // 메시지 생성
    Message message = new Message();
    message.setFrom(solapiConfig.getFrom());
    message.setTo(dto.getTo());
    message.setKakaoOptions(kakaoOption);
    
    // 메시지 전송
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

package com.lawnroad.notification.service;

import com.lawnroad.common.config.SolapiConfig;
import com.lawnroad.notification.dto.ClientReservationCreatedDto;
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
public class ClientReservationCreatedService {
  
  private final SolapiConfig solapiConfig;
  
  /**
   * #{client}님, 상담 신청이 정상적으로 접수되었습니다.
   * 변호사님이 상담 일시에 직접 연락드릴 예정입니다.
   *
   * 🧑 상담 대상: #{lawyer} 변호사
   * 🗓 상담 일시: #{datetime}
   */
  
  public void send(ClientReservationCreatedDto dto) {
    // Solapi SDK 초기화
    DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(
        solapiConfig.getApiKey(),
        solapiConfig.getApiSecret(),
        solapiConfig.getApiUrl()
    );
    
    // 알림톡 옵션 구성
    KakaoOption kakaoOption = new KakaoOption();
    kakaoOption.setPfId(solapiConfig.getPfId());
    kakaoOption.setTemplateId("KA01TP250624082622083hinD2IC9XTv");
    
    Map<String, String> variables = new HashMap<>();
    variables.put("#{client}", dto.getClient());
    variables.put("#{lawyer}", dto.getLawyer());
    variables.put("#{datetime}", dto.getDatetime());
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

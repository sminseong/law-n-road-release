package com.lawnroad.notification.service;

import com.lawnroad.common.config.SolapiConfig;
import com.lawnroad.notification.dto.BroadcastCreatedDto;
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
public class BroadcastCreatedService {
  
  private final SolapiConfig solapiConfig;
  
  /**
   * #{lawyer} 변호사님 방송 등록!
   *
   * #{name}님, 알림 받기 신청하신 변호사님의 새로운 방송이 등록되었습니다.
   *
   * 📺 방송 제목: #{title}
   * 🕒 방송 예정 시간: #{start}
   *
   * 방송 전에 궁금한 점을 미리 남겨주세요.
   * 변호사님이 방송 중 직접 답변해 드립니다!
   */
  
  public void send(BroadcastCreatedDto dto) {
    // SDK 초기화
    DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(
        solapiConfig.getApiKey(),
        solapiConfig.getApiSecret(),
        solapiConfig.getApiUrl()
    );
    
    // 알림톡 옵션 구성
    KakaoOption kakaoOption = new KakaoOption();
    kakaoOption.setPfId(solapiConfig.getPfId());
    kakaoOption.setTemplateId("KA01TP250624110434737cvBn4FFuvbQ");  // 템플릿 ID 직접 지정
    
    Map<String, String> variables = new HashMap<>();
    variables.put("#{lawyer}", dto.getLawyer());
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

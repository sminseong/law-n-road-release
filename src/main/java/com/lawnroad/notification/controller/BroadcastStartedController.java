package com.lawnroad.notification.controller;

import com.lawnroad.notification.dto.BroadcastStartedDto;
import com.lawnroad.notification.service.BroadcastStartedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification/broadcast")
@RequiredArgsConstructor
public class BroadcastStartedController {
  
  private final BroadcastStartedService broadcastStartedService;
  
  /**
   * 관심 방송 시작 알림톡 발송 API
   * POST /api/notification/broadcast/start
   */
  @PostMapping("/start")
  public ResponseEntity<String> sendBroadcastStartNotification(@RequestBody BroadcastStartedDto dto) {
    System.out.println("📥 알림톡 요청 도착! DTO: " + dto.toString());
    broadcastStartedService.sendStartAlimtalk(dto, "KA01TP250604081040134aHQgx8X77lf");
    return ResponseEntity.ok("알림톡 발송 완료");
  }
}

package com.lawnroad.keyword.controller;

import com.lawnroad.keyword.dto.ClientAlertUpdateDto;
import com.lawnroad.keyword.service.ClientAlertUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientAlertUpdateController {
  
  private final ClientAlertUpdateService clientAlertUpdateService;
  
  /**
   * 클라이언트의 현재 알림 설정을 조회합니다.
   */
  @GetMapping("/client/alert-settings/{clientNo}")
  public ResponseEntity<ClientAlertUpdateDto> getAlertSettings(@PathVariable Long clientNo) {
    ClientAlertUpdateDto alertSettings = clientAlertUpdateService.getAlertSettings(clientNo);
    return ResponseEntity.ok(alertSettings);
  }
  
  /**
   * 상담 알림 및 키워드 알림 설정을 업데이트합니다.
   */
  @PostMapping("/client/update-alerts")
  public ResponseEntity<Void> updateAlertSettings(@RequestBody ClientAlertUpdateDto dto) {
    clientAlertUpdateService.updateAlertSettings(dto);
    return ResponseEntity.ok().build();
  }
}
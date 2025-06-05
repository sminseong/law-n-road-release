package com.lawnroad.notification.controller;

import com.lawnroad.notification.dto.*;
import com.lawnroad.notification.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
  
  private final BroadcastStartedService broadcastStartedService;
  private final VerificationCodeService verificationCodeService;
  private final ClientReservationStartedService clientReservationStartedService;
  private final LawyerReservationStartedService lawyerReservationStartedService;
  private final ClientReservationCreatedService clientReservationCreatedService;
  private final LawyerReservationCreatedService lawyerReservationCreatedService;
  private final LawyerReservationCanceledService lawyerReservationCanceledService;
  
  /**
   * 방송 시작 알림톡 발송 API
   * POST /api/notification/broadcast/start
   */
  @PostMapping("/broadcast/start")
  public ResponseEntity<String> sendBroadcastStartNotification(@RequestBody BroadcastStartedDto dto) {
    broadcastStartedService.send(dto);
    return ResponseEntity.ok("방송 시작 알림톡 발송 완료");
  }
  
  /**
   * 본인 인증번호 알림톡 발송 API
   * POST /api/notification/verify-code
   */
  @PostMapping("/verify-code")
  public ResponseEntity<String> sendVerificationCode(@RequestBody VerificationCodeDto dto) {
    verificationCodeService.send(dto);
    return ResponseEntity.ok("인증번호 알림톡 발송 완료");
  }
  
  /**
   * 상담 임박 (의뢰인) 알림톡 발송 API
   * POST /api/notification/client/reservation/started
   */
  @PostMapping("/client/reservation/started")
  public ResponseEntity<String> sendClientReservationStarted(@RequestBody ClientReservationStartedDto dto) {
    clientReservationStartedService.send(dto);
    return ResponseEntity.ok("상담 임박 (의뢰인) 알림톡 발송 완료");
  }
  
  /**
   * 상담 임박 (변호사) 알림톡 발송 API
   * POST /api/notification/lawyer/reservation/started
   */
  @PostMapping("/lawyer/reservation/started")
  public ResponseEntity<String> sendLawyerReservationStarted(@RequestBody LawyerReservationStartedDto dto) {
    lawyerReservationStartedService.send(dto);
    return ResponseEntity.ok("상담 임박 (변호사) 알림톡 발송 완료");
  }
  
  /**
   * 신규 상담 (의뢰인) 알림톡 발송 API
   * POST /api/notification/client/reservation/created
   */
  @PostMapping("/client/reservation/created")
  public ResponseEntity<String> sendClientReservationCreated(@RequestBody ClientReservationCreatedDto dto) {
    clientReservationCreatedService.send(dto);
    return ResponseEntity.ok("상담 신청 완료 (의뢰인) 알림톡 발송 완료");
  }
  
  /**
   * 신규 상담 (변호사) 알림톡 발송 API
   * POST /api/notification/lawyer/reservation/created
   */
  @PostMapping("/lawyer/reservation/created")
  public ResponseEntity<String> sendLawyerReservationCreated(@RequestBody LawyerReservationCreatedDto dto) {
    lawyerReservationCreatedService.send(dto);
    return ResponseEntity.ok("상담 신청 완료 (변호사) 알림톡 발송 완료");
  }
  
  /**
   * 상담 취소 (변호사) 알림톡 발송 API
   * POST /api/notification/lawyer/reservation/canceled
   */
  @PostMapping("/lawyer/reservation/canceled")
  public ResponseEntity<String> sendLawyerReservationCanceled(@RequestBody LawyerReservationCanceledDto dto) {
    lawyerReservationCanceledService.send(dto);
    return ResponseEntity.ok("상담 취소 (변호사) 알림톡 발송 완료");
  }
}

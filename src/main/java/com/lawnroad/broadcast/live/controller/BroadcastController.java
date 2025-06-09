package com.lawnroad.broadcast.live.controller;

import com.lawnroad.broadcast.live.service.OpenViduService;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/broadcast")
@RequiredArgsConstructor
public class BroadcastController {

    private final OpenViduService openViduService;

    /** 방송 시작: Session 생성 */
    @PostMapping("/start")
    public ResponseEntity<String> startBroadcast() {
        try {
            String sessionId = openViduService.createSession();
            return ResponseEntity.ok(sessionId); // Vue에서 .data 로 받음
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return ResponseEntity.internalServerError().body("세션 생성 실패");
        }
    }

    /**
     * 방송 입장: Token 발급 (방송자/시청자 공용)
     * 요청 JSON 예시: { "sessionId": "ses_abc123", "role": "PUBLISHER" 또는 "SUBSCRIBER" }
     */
    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody Map<String, String> requestBody) {
        try {
            String sessionId = requestBody.get("sessionId");
            String roleStr = requestBody.getOrDefault("role", "SUBSCRIBER"); // 기본값은 시청자
            OpenViduRole role = OpenViduRole.valueOf(roleStr.toUpperCase());

            String token = openViduService.generateToken(sessionId, role);
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("잘못된 role 값입니다. (예: PUBLISHER 또는 SUBSCRIBER)");
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return ResponseEntity.internalServerError().body("토큰 발급 실패");
        }
    }
}

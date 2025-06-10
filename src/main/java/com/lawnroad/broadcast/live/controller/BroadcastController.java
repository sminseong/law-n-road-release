package com.lawnroad.broadcast.live.controller;

import com.lawnroad.broadcast.live.service.OpenViduServiceImpl;
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

    private final OpenViduServiceImpl openViduServiceImpl;

    /** 방송 시작: 세션 생성 */
    @PostMapping("/start")
    public ResponseEntity<String> startBroadcast() {
        try {
            String sessionId = openViduServiceImpl.createSession();
            return ResponseEntity.ok(sessionId);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("⚠ 세션 생성 실패: " + e.getMessage());
        }
    }

    /** 토큰 발급: 방송자/시청자 공용 */
    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody Map<String, String> requestBody) {
        try {
            String sessionId = requestBody.get("sessionId");
            if (sessionId == null || sessionId.isBlank()) {
                return ResponseEntity.badRequest().body("❌ sessionId가 누락되었습니다.");
            }

            String roleStr = requestBody.getOrDefault("role", "SUBSCRIBER");
            OpenViduRole role = OpenViduRole.valueOf(roleStr.toUpperCase());

            String token = openViduServiceImpl.generateToken(sessionId, role);
            return ResponseEntity.ok(token);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ 유효하지 않은 role입니다. (PUBLISHER | SUBSCRIBER)");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ 세션이 존재하지 않습니다. sessionId: " + requestBody.get("sessionId"));
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("⚠ 토큰 발급 실패: " + e.getMessage());
        }
    }

    /** 방송 종료 (선택사항) */
    @DeleteMapping("/end/{sessionId}")
    public ResponseEntity<String> endBroadcast(@PathVariable String sessionId) {
        openViduServiceImpl.removeSession(sessionId);
        return ResponseEntity.ok("✅ 세션 종료 및 제거 완료: " + sessionId);
    }
}

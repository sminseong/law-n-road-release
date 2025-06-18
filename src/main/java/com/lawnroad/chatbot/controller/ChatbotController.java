package com.lawnroad.chatbot.controller;


import com.lawnroad.common.config.ChatbotConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public/chatbot")
@RequiredArgsConstructor
@Slf4j
public class ChatbotController {

    private final ChatbotConfig chatbotConfig;
    private final RestTemplate restTemplate = new RestTemplate(); //

    // 1. open 이벤트
    @PostMapping("/open")
    public ResponseEntity<String> openChatbot() {
        log.info("open chatbot 호출");
        return transmitMessage("", true);
    }

    // 2. send 이벤트
    @PostMapping("/send")
    public ResponseEntity<String> sendChatbot(@RequestBody Map<String, String> body) {
        log.info("send chatbot 호출");
        String question = body.get("question");
        return transmitMessage(question, false);
    }

    // 클로바로 메시지 전달
    private ResponseEntity<String> transmitMessage(String question, boolean isOpen) {
        try {
            log.info("💬 전송할 질문 내용: {}", question);
            log.info("📌 SecretKey: {}", chatbotConfig.getSecretKey());
            log.info("📌 Invoke URL: {}", chatbotConfig.getInvokeUrl());
            // 시간
            long timestamp = LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond();

            // 요청 바디 생성
            Map<String, Object> body = new HashMap<>();
            body.put("version", "v1");
            body.put("userId", "lawroad-user-001");
            body.put("timestamp", timestamp);
            body.put("event", isOpen ? "open" : "send");

            Map<String, Object> content = new HashMap<>();
            content.put("type", "text");

            Map<String, Object> data = new HashMap<>();
            data.put("details", question);
            content.put("data", data);

            body.put("content", new Object[]{content});

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-NCP-CHATBOT_SIGNATURE", chatbotConfig.getSecretKey());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response =
                    restTemplate.postForEntity(chatbotConfig.getInvokeUrl(), request, String.class);

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

        } catch (Exception e) {
            log.error("챗봇 전송 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("챗봇 오류");
        }
    }
}
package com.lawnroad.broadcast.chat.controller;

import com.lawnroad.broadcast.chat.dto.ChatDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 클라이언트가 "/app/chat.sendMessage" 로 전송한 ChatDTO를 받아서 처리
     *  1) createdAt 필드에 서버 현재 시각 채워 넣기
     *  2) "/topic/chat/{broadcastNo}" 로 브로드캐스트
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatDTO chatDto) {
        // 1) 서버에서 현재 시각을 createdAt에 세팅
        chatDto.setCreatedAt(LocalDateTime.now());

        // 2) 실제 브로드캐스트 대상 경로: "/topic/chat/{broadcastNo}"
        String destination = "/topic/chat/" + chatDto.getBroadcastNo();
        messagingTemplate.convertAndSend(destination, chatDto);
    }

    /**
     * 클라이언트가 "/app/chat.addUser" 로 전송한 JOIN 알림을 받아서 처리
     *  1) createdAt 필드에 서버 현재 시각 채워 넣기
     *  2) "/topic/chat/{broadcastNo}" 로 브로드캐스트
     */
    @MessageMapping("/chat.addUser")
    public void addUser(ChatDTO chatDto) {
        // 1) 서버에서 현재 시각을 createdAt에 세팅
        chatDto.setCreatedAt(LocalDateTime.now());

        // 2) 브로드캐스트 경로 동일하게 "/topic/chat/{broadcastNo}"
        String destination = "/topic/chat/" + chatDto.getBroadcastNo();
        messagingTemplate.convertAndSend(destination, chatDto);
    }
}

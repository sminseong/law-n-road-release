package com.lawnroad.broadcast.chat.controller;

import com.lawnroad.broadcast.chat.dto.ChatDTO;
import com.lawnroad.broadcast.chat.service.ChatRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatStompController {

    private final ChatRedisService chatRedisService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatStompController(SimpMessagingTemplate messagingTemplate, ChatRedisService chatRedisService) {
        this.messagingTemplate = messagingTemplate;
        this.chatRedisService = chatRedisService;
    }

    // 사용자가 입장했을 때
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatDTO chatDTO) {
        String notice = "　　　　　　　　" + chatDTO.getNickname() + " 님이 접속했습니다.";
        messagingTemplate.convertAndSend("/topic/" + chatDTO.getBroadcastNo(), notice);
    }

    // 실제 채팅 메시지 전송
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatDTO chatDTO) {
        chatDTO.setCreatedAt(LocalDateTime.now());
        chatRedisService.saveChatMessage(chatDTO);

        String fullMessage = "[" + chatDTO.getNickname() + "]　" + chatDTO.getMessage();
        messagingTemplate.convertAndSend("/topic/" + chatDTO.getBroadcastNo(), fullMessage);
    }
}

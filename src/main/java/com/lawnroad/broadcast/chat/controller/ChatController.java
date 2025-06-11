package com.lawnroad.broadcast.chat.controller;

import com.lawnroad.broadcast.chat.dto.ChatDTO;
import com.lawnroad.broadcast.chat.service.ChatRedisSaveServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final ChatRedisSaveServiceImpl chatRedisSaveService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate, ChatRedisSaveServiceImpl chatRedisSaveService) {
        this.messagingTemplate = messagingTemplate;
        this.chatRedisSaveService = chatRedisSaveService;
    }

    // 사용자가 입장했을 때
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatDTO chatDTO) {
        chatDTO.setType("ENTER");
        chatDTO.setMessage(chatDTO.getNickname() + " 님이 접속했습니다.");
        chatDTO.setCreatedAt(LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/" + chatDTO.getBroadcastNo(), chatDTO);
    }

    // 실제 채팅 메시지 전송
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatDTO chatDTO) {
        chatDTO.setCreatedAt(LocalDateTime.now());
        chatDTO.setType("CHAT");
        chatRedisSaveService.saveChatMessage(chatDTO);

        messagingTemplate.convertAndSend("/topic/" + chatDTO.getBroadcastNo(), chatDTO);
    }


}

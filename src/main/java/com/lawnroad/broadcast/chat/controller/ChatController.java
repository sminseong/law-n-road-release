package com.lawnroad.broadcast.chat.controller;

import com.lawnroad.account.dto.LoginRequest;
import com.lawnroad.account.entity.ClientEntity;
import com.lawnroad.account.entity.UserEntity;
import com.lawnroad.account.mapper.UserMapper;
import com.lawnroad.account.service.ClientService;
import com.lawnroad.broadcast.chat.dto.ChatDTO;
import com.lawnroad.broadcast.chat.service.ChatRedisSaveServiceImpl;
import com.lawnroad.common.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


import java.time.LocalDateTime;
import java.util.Objects;

@Controller
public class ChatController {

    private final ChatRedisSaveServiceImpl chatRedisSaveService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate, ChatRedisSaveServiceImpl chatRedisSaveService, JwtTokenUtil jwtTokenUtil, UserMapper userMapper) {
        this.messagingTemplate = messagingTemplate;
        this.chatRedisSaveService = chatRedisSaveService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatDTO chatDTO, @Header("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenUtil.parseToken(token);

        String nickname = claims.get("nickname", String.class);
        chatDTO.setNickname(nickname);
        chatDTO.setType("ENTER");
        chatDTO.setMessage(nickname + " 님이 접속했습니다.");
        chatDTO.setCreatedAt(LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/" + chatDTO.getBroadcastNo(), chatDTO);
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatDTO chatDTO, @Header("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenUtil.parseToken(token);

        String nickname = claims.get("nickname", String.class);
        Long no = claims.get("no", Long.class);
        chatDTO.setNickname(nickname);
        chatDTO.setCreatedAt(LocalDateTime.now());
        chatDTO.setNo(no);
        chatDTO.setType("CHAT");
        chatDTO.setReportStatus(0);

        chatRedisSaveService.saveChatMessage(chatDTO);
        messagingTemplate.convertAndSend("/topic/" + chatDTO.getBroadcastNo(), chatDTO);
    }


}

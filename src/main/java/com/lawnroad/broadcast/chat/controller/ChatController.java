package com.lawnroad.broadcast.chat.controller;

import com.lawnroad.account.dto.LoginRequest;
import com.lawnroad.account.entity.ClientEntity;
import com.lawnroad.account.entity.UserEntity;
import com.lawnroad.account.mapper.UserMapper;
import com.lawnroad.account.service.ClientService;
import com.lawnroad.broadcast.chat.dto.ChatDTO;
import com.lawnroad.broadcast.chat.service.ChatRedisSaveServiceImpl;
import com.lawnroad.common.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final ChatRedisSaveServiceImpl chatRedisSaveService;
    private final SimpMessagingTemplate messagingTemplate;

    private ClientService clientService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserMapper userMapper;

    public static String nickname;
    public static Long no;


    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate, ChatRedisSaveServiceImpl chatRedisSaveService, JwtTokenUtil jwtTokenUtil, UserMapper userMapper) {
        this.messagingTemplate = messagingTemplate;
        this.chatRedisSaveService = chatRedisSaveService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userMapper = userMapper;
    }

    // 사용자가 입장했을 때
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatDTO chatDTO) {
        chatDTO.setType("ENTER");
        chatDTO.setMessage(chatDTO.getNickname() + " 님이 접속했습니다.");
        chatDTO.setCreatedAt(LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/" + chatDTO.getBroadcastNo(), chatDTO);
    }

    @PostMapping("/test")
    public void test(@RequestBody LoginRequest request){

        ClientEntity client = clientService.login(request.getClientId(), request.getPassword());

        UserEntity user = userMapper.findByNo(client.getNo());
        String accessToken = jwtTokenUtil.generateAccessToken(client.getClientId(),client.getNo(),user.getType(),client.getNickname());
        jwtTokenUtil.printPayload(accessToken);
         no = jwtTokenUtil.getUserNoFromToken(accessToken);
         nickname = jwtTokenUtil.getNicknameFromToken(accessToken);

    }

    // 실제 채팅 메시지 전송
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatDTO chatDTO) {
        chatDTO.setCreatedAt(LocalDateTime.now());
        chatDTO.setType("CHAT");
        chatDTO.setReportStatus(0);

        chatRedisSaveService.saveChatMessage(chatDTO);
        messagingTemplate.convertAndSend("/topic/" + chatDTO.getBroadcastNo(), chatDTO);
    }


}

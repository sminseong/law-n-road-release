package com.lawnroad.broadcast.chat.controller;


import com.lawnroad.broadcast.chat.dto.ChatDTO;
import com.lawnroad.broadcast.chat.service.AutoReplyService;
import com.lawnroad.broadcast.chat.service.ChatRedisSaveServiceImpl;
import com.lawnroad.common.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatRedisSaveServiceImpl chatRedisSaveService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtTokenUtil jwtTokenUtil;
    private final AutoReplyService autoReplyService;


    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatDTO chatDTO, @Header("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenUtil.parseToken(token);

        String nickname = claims.get("nickname", String.class);
        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = chatDTO.getName()+" 변호사";
        }
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
        if(chatDTO.getType() == null) {
            chatDTO.setType("CHAT"); // 일반 채팅이면 기본값
        }
        chatDTO.setReportStatus(0);

        chatRedisSaveService.saveChatMessage(chatDTO);
        messagingTemplate.convertAndSend("/topic/" + chatDTO.getBroadcastNo(), chatDTO);


        // ------- 자동응답 처리 -------
        String msg = chatDTO.getMessage();
        if (msg != null && msg.startsWith("!")) {
            String keyword = msg.substring("!".length()).trim();

            String autoReplyMsg = autoReplyService.findReplyMessage(chatDTO.getBroadcastNo(), keyword);

            if (autoReplyMsg != null) {
                ChatDTO reply = ChatDTO.builder()
                        .broadcastNo(chatDTO.getBroadcastNo())
                        .nickname("AutoReply") // 또는 "나이트봇" 등
                        .message(autoReplyMsg)
                        .type("AUTO_REPLY")
                        .createdAt(LocalDateTime.now())
                        .build();
                messagingTemplate.convertAndSend("/topic/" + chatDTO.getBroadcastNo(), reply);
            }
        }
    }

    @GetMapping("/api/client/my-no")
    public ResponseEntity<Long> getMyNo(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenUtil.parseToken(token);
        Long no = claims.get("no", Long.class);
        return ResponseEntity.ok(no);
    }

    @GetMapping("/api/Lawyer/my-no")
    public ResponseEntity<Long> getLawyerMyNo(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenUtil.parseToken(token);
        Long no = claims.get("no", Long.class);
        return ResponseEntity.ok(no);
    }

}



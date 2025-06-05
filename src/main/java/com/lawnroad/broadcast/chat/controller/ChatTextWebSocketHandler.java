package com.lawnroad.broadcast.chat.controller;

import com.lawnroad.broadcast.chat.dto.ChatDTO;
import com.lawnroad.broadcast.chat.service.ChatRedisService;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatTextWebSocketHandler extends TextWebSocketHandler {

    private final ChatRedisService chatRedisService;

    // 현재 접속 중인 모든 세션
    private final Set<WebSocketSession> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // 각 세션에 대응되는 닉네임 저장
    private final Map<WebSocketSession, String> nicknames = new ConcurrentHashMap<>();

    public ChatTextWebSocketHandler(ChatRedisService chatRedisService) {
        this.chatRedisService = chatRedisService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String nickname = extractNicknameFromUri(session.getUri());
        nicknames.put(session, nickname);
        sessions.add(session);

        String message = "[" + nickname + "]　님이 접속했습니다.";
        broadcast(message);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String nickname = nicknames.get(session);
        String payload = message.getPayload();
        String fullMessage = "[" + nickname + "]　" + payload;

        // 1. 브로드캐스트
        broadcast(fullMessage);

        // 2. Redis에 채팅 저장
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setNo(null);
        chatDTO.setUserNo(null);
        chatDTO.setBroadcastNo(extractBroadcastNoFromUri(session.getUri()));
        chatDTO.setNickname(nickname);
        chatDTO.setMessage(payload);
        chatDTO.setReportCount(0);
        chatDTO.setCreatedAt(LocalDateTime.now());

        chatRedisService.saveChatMessage(chatDTO);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        nicknames.remove(session);
    }

    private void broadcast(String message) {
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                try {
                    s.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String extractNicknameFromUri(URI uri) {
        if (uri == null || uri.getQuery() == null) return "익명";
        String[] params = uri.getQuery().split("&");
        for (String param : params) {
            if (param.startsWith("nickname=")) {
                return URLDecoder.decode(param.substring("nickname=".length()), StandardCharsets.UTF_8);
            }
        }
        return "익명";
    }

    private Long extractBroadcastNoFromUri(URI uri) {
        if (uri == null || uri.getQuery() == null) return null;
        String[] params = uri.getQuery().split("&");
        for (String param : params) {
            if (param.startsWith("broadcastNo=")) {
                try {
                    return Long.parseLong(param.substring("broadcastNo=".length()));
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }
}

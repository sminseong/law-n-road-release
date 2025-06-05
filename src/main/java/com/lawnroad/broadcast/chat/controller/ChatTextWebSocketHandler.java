package com.lawnroad.broadcast.chat.controller;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatTextWebSocketHandler extends TextWebSocketHandler {

    // 현재 접속 중인 모든 클라이언트 세션을 저장하는 집합
    // 동시성 문제가 없도록 ConcurrentHashMap 기반으로 구성
    private final Set<WebSocketSession> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // 동시성 문제가 없도록 ConcurrentHashMap 기반으로 구성
    // 각 세션에 대응되는 닉네임 정보를 저장
    private final Map<WebSocketSession, String> nicknames = new ConcurrentHashMap<>();


    // 사용자가 WebSocket에 연결했을 때 실행
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String nickname = extractNicknameFromUri(session.getUri());
        nicknames.put(session, nickname);
        sessions.add(session);

        String message = "[" + nickname + "] 님이 접속했습니다.";
        broadcast(message);
    }

    // 클라이언트가 메시지를 보내면 호출
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String nickname = nicknames.get(session);
        String payload = message.getPayload();
        String fullMessage = "[" + nickname + "] " + payload;
        broadcast(fullMessage);
    }

    // 연결이 끊긴 세션을 관리 목록에서 제거
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        nicknames.remove(session);
    }

    // 현재 열려 있는 세션에 대해 메시지를 전송
    private void broadcast(String message) {
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                try {
                    s.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    e.printStackTrace(); // 실패한 경우 로그 남기기
                }
            }
        }
    }

    // 쿼리 파라미터에서 닉네임 추출
    // 쿼리 파라미터가 없으면 기본값은 "익명"
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
}

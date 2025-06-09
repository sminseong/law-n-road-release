package com.lawnroad.broadcast.live.service;

import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OpenViduService {

    private final OpenVidu openVidu;

    // 세션 저장소 추가
    private static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    public String createSession() throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = openVidu.createSession();
        sessionMap.put(session.getSessionId(), session);
        return session.getSessionId();
    }

    public String generateToken(String sessionId, OpenViduRole role) throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = sessionMap.get(sessionId);
        if (session == null) {
            throw new RuntimeException("No session found in map for sessionId: " + sessionId);
        }

        ConnectionProperties properties = new ConnectionProperties.Builder()
                .type(ConnectionType.WEBRTC)
                .role(role)
                .build();

        return session.createConnection(properties).getToken();
    }

    public void removeSession(String sessionId) {
        sessionMap.remove(sessionId);
    }
}

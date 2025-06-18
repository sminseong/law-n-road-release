package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.mapper.BroadcastMapper;
import io.openvidu.java.client.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Service
@RequiredArgsConstructor
public class OpenViduServiceImpl implements OpenViduService {

    @Value("${OPENVIDU_URL}")
    private String openviduUrl;

    @Value("${OPENVIDU_SECRET}")
    private String openviduSecret;

    private OpenVidu openVidu;
    private final BroadcastMapper broadcastMapper;
    private final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        this.openVidu = new OpenVidu(openviduUrl, openviduSecret);
    }

    @Override
    public String createSessionAndToken(String customSessionId) {
        try {
            SessionProperties sessionProps = new SessionProperties.Builder()
                    .customSessionId(customSessionId)
                    .build();

            Session session = openVidu.createSession(sessionProps);
            sessionMap.put(customSessionId, session);

            ConnectionProperties connectionProps = new ConnectionProperties.Builder()
                    .type(ConnectionType.WEBRTC)
                    .build();

            Connection connection = session.createConnection(connectionProps);
            return connection.getToken();

        } catch (Exception e) {
            throw new RuntimeException("OpenVidu 세션 생성 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public String createTokenForExistingSession(String sessionId) {
        try {
            Session session = sessionMap.get(sessionId);
            if (session == null) {
                session = openVidu.getActiveSession(sessionId);
                if (session == null) {
                    throw new RuntimeException("세션이 존재하지 않음: " + sessionId);
                }
                sessionMap.put(sessionId, session);
            }

            ConnectionProperties connectionProps = new ConnectionProperties.Builder()
                    .type(ConnectionType.WEBRTC)
                    .build();

            Connection connection = session.createConnection(connectionProps);
            return connection.getToken();

        } catch (Exception e) {
            throw new RuntimeException("OpenVidu 토큰 발급 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isSessionActive(String sessionId) {
        try {
            Session session = openVidu.getActiveSession(sessionId);
            return session != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String createTokenForClient(String sessionId) {
        try {
            // 기존 세션을 가져옴 (존재하지 않으면 예외 발생)
            Session session = openVidu.getActiveSession(sessionId);
            if (session == null) {
                throw new RuntimeException("OpenVidu 세션이 존재하지 않음: " + sessionId);
            }

            // 시청자용 연결 설정
            ConnectionProperties properties = new ConnectionProperties.Builder()
                    .type(ConnectionType.WEBRTC)
                    .role(OpenViduRole.SUBSCRIBER)
                    .build();

            // 토큰 생성
            return session.createConnection(properties).getToken();

        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            throw new RuntimeException("OpenVidu 토큰 발급 실패 (시청자용)", e);
        }
    }

    @Override
    public int getViewerCountByBroadcastNo(Long broadcastNo) {
        try {
            // 방송 번호로 세션 ID 조회
            String sessionId = broadcastMapper.findSessionIdByBroadcastNo(broadcastNo);

            if (sessionId == null || sessionId.isEmpty()) {
                System.out.println("❌ 세션 ID가 존재하지 않음 (broadcastNo: " + broadcastNo + ")");
                return 0;
            }
            System.out.println("조회 대상 세션 ID: " + sessionId);
            Session session = openVidu.getActiveSession(sessionId);
            System.out.println("세션 존재 여부: " + (session != null));
            if (session != null) {
                return session.getActiveConnections().size();
            }
        } catch (Exception e) {
            System.out.println("🔴 시청자 수 조회 실패 (broadcastNo: " + broadcastNo + "): " + e.getMessage());
        }
        return 0;
    }
}

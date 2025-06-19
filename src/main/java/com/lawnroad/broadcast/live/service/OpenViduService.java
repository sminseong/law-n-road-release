package com.lawnroad.broadcast.live.service;

public interface OpenViduService {
    String createSessionAndToken(String customSessionId);
    String createTokenForExistingSession(String sessionId);
    boolean isSessionActive(String sessionId);
    String createTokenForClient(String sessionId);
    // 시청자 수
    int getViewerCountByBroadcastNo(Long broadcastNo);
}

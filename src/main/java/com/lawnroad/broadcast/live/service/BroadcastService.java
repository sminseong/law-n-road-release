package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.dto.BroadcastStartDto;
import com.lawnroad.broadcast.live.dto.BroadcastStartResponseDto;

import java.util.Map;

public interface BroadcastService {
    BroadcastStartResponseDto startBroadcast(Long userNo, BroadcastStartDto dto);
    BroadcastStartResponseDto getClientToken(Long broadcastNo);
    BroadcastStartResponseDto reconnectBroadcast(String sessionId);
}

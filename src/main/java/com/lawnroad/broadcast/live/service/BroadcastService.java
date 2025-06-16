package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.dto.BroadcastStartDto;
import com.lawnroad.broadcast.live.dto.BroadcastStartResponseDto;
import com.lawnroad.broadcast.live.dto.BroadcastViewDetailDto;

import java.util.Map;

public interface BroadcastService {
    BroadcastStartResponseDto startBroadcast(Long userNo, BroadcastStartDto dto);
    BroadcastStartResponseDto getClientToken(Long broadcastNo);
    BroadcastStartResponseDto reconnectBroadcast(String sessionId);

    // 방송에 정보 불러오기
    BroadcastViewDetailDto getDetailByScheduleNo(Long scheduleNo);
    BroadcastViewDetailDto getDetailByBroadcastNo(Long broadcastNo);

    //방송 종료
    void endBroadcast(Long broadcastNo);
}

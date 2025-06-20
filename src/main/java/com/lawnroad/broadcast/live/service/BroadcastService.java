package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.dto.*;
import com.lawnroad.broadcast.live.model.BroadcastVo;

import java.util.List;

public interface BroadcastService {
    BroadcastStartResponseDto startBroadcast(Long userNo, BroadcastStartDto dto);
    BroadcastStartResponseDto getClientToken(Long broadcastNo);
    BroadcastStartResponseDto reconnectBroadcast(String sessionId);
    // 방송에 정보 불러오기
    BroadcastViewDetailDto getDetailByScheduleNo(Long scheduleNo);
    BroadcastViewDetailDto getDetailByBroadcastNo(Long broadcastNo);
    //방송 종료
    void endBroadcast(Long broadcastNo);
    //방송 신고
    void reportBroadcast(BroadcastReportRequestDto dto);
    // 방송 리스트
    List<BroadcastListDto> getLiveBroadcasts();
    // 해당 스케줄이 방송중인지
    Long findLiveBroadcastNoByScheduleNo(Long scheduleNo);
    // 방송종료시간 30분 지난방송 자동 종료처리
    void expireOverdueBroadcasts();
}

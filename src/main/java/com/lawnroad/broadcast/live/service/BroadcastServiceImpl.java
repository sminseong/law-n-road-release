package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.dto.BroadcastSessionDto;
import com.lawnroad.broadcast.live.dto.BroadcastStartDto;
import com.lawnroad.broadcast.live.dto.BroadcastStartResponseDto;
import com.lawnroad.broadcast.live.mapper.BroadcastMapper;
import com.lawnroad.broadcast.live.model.BroadcastVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BroadcastServiceImpl implements BroadcastService {

    private final BroadcastMapper broadcastMapper;
    private final OpenViduService openViduService;

    /**
     * 방송자 - 방송 시작
     */
    @Override
    public BroadcastStartResponseDto startBroadcast(Long userNo, BroadcastStartDto dto) {
        BroadcastVo existing = broadcastMapper.findByScheduleNo(dto.getScheduleNo());

        // 기존 방송이 있고 세션이 살아있으면 토큰만 새로 생성해서 반환
        if (existing != null && openViduService.isSessionActive(existing.getSessionId())) {
            String token = openViduService.createTokenForExistingSession(existing.getSessionId());
            System.out.println("♻️ 방송자 기존 세션 재사용: " + existing.getSessionId());
            return new BroadcastStartResponseDto(existing.getSessionId(), token);
        }

        // 신규 세션 생성
        String sessionId = "ses_" + UUID.randomUUID();
        dto.setSessionId(sessionId);

        BroadcastVo vo = BroadcastVo.builder()
                .userNo(userNo)
                .scheduleNo(dto.getScheduleNo())
                .sessionId(sessionId)
                .startTime(LocalDateTime.now())
                .status("RECORD")
                .build();

        broadcastMapper.insertBroadcast(vo);
        System.out.println("🎥 방송자 신규 sessionId 생성됨: " + sessionId);

        String token = openViduService.createSessionAndToken(sessionId);
        return new BroadcastStartResponseDto(sessionId, token);
    }

    /**
     * 시청자 - 방송 입장 토큰 요청
     */
    @Override
    public BroadcastStartResponseDto getClientToken(Long broadcastNo) {
        BroadcastSessionDto dto = broadcastMapper.findByNo(broadcastNo);

        if (dto == null || dto.getSessionId() == null) {
            throw new RuntimeException("방송 정보 또는 세션 ID가 존재하지 않음");
        }

        System.out.println("🔍 시청자용 sessionId: " + dto.getSessionId());

        String token = openViduService.createTokenForClient(dto.getSessionId());
        return new BroadcastStartResponseDto(dto.getSessionId(), token);
    }

    /**
     * 방송자 - 새로고침 시 재접속을 위한 세션 재사용
     */
    @Override
    public BroadcastStartResponseDto reconnectBroadcast(String sessionId) {
        BroadcastVo vo = broadcastMapper.findBySessionId(sessionId);
        if (vo == null) {
            throw new RuntimeException("해당 세션의 방송이 존재하지 않습니다.");
        }

        if (!openViduService.isSessionActive(sessionId)) {
            throw new RuntimeException("세션이 만료되었거나 존재하지 않습니다.");
        }

        System.out.println("♻️ 방송자 세션 재연결: " + sessionId);
        String token = openViduService.createTokenForExistingSession(sessionId);
        return new BroadcastStartResponseDto(sessionId, token);
    }
}

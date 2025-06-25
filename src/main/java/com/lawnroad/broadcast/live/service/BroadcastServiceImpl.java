package com.lawnroad.broadcast.live.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.lawnroad.broadcast.live.dto.*;
import com.lawnroad.broadcast.live.mapper.BroadcastMapper;
import com.lawnroad.broadcast.live.mapper.ScheduleMapper;
import com.lawnroad.broadcast.live.model.BroadcastVo;
import com.lawnroad.common.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BroadcastServiceImpl implements BroadcastService {

    private final BroadcastMapper broadcastMapper;
    private final OpenViduService openViduService;
    private final ScheduleMapper scheduleMapper;

    /**
     * 방송자 - 방송 시작
     */
    @Override
    public BroadcastStartResponseDto startBroadcast(Long userNo, BroadcastStartDto dto) {
        // schedule 소유자 확인
        ScheduleDetailDto schedule = scheduleMapper.findByScheduleNo(dto.getScheduleNo());
        if (schedule == null) {
            throw new NotFoundException("존재하지 않는 스케줄입니다.");
        }
        if (!schedule.getUserNo().equals(userNo)) {
            throw new AccessDeniedException("자신의 스케줄만 방송 시작이 가능합니다.");
        }
        BroadcastVo existing = broadcastMapper.findByScheduleNo(dto.getScheduleNo());

        // 기존 방송이 있고 세션이 살아있으면 토큰만 새로 생성해서 반환
        if (existing != null) {
            if ("DONE".equals(existing.getStatus())) {
                throw new RuntimeException("이미 종료된 방송입니다.");
            }

            if (openViduService.isSessionActive(existing.getSessionId())) {
                String token = openViduService.createTokenForExistingSession(existing.getSessionId());
                System.out.println("♻️ 방송자 기존 세션 재사용: " + existing.getSessionId());
                return new BroadcastStartResponseDto(existing.getSessionId(), token, existing.getNo(), existing.getStartTime());
            }

            // 세션이 만료된 경우, 방송 불가
            throw new RuntimeException("세션이 만료되어 더 이상 방송을 시작할 수 없습니다.");
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
        return new BroadcastStartResponseDto(sessionId, token, vo.getNo(), vo.getStartTime());
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
            throw new RuntimeException("세션이 만료되어 접속할 수 없습니다.");
        }

        System.out.println("♻️ 방송자 세션 재연결: " + sessionId);
        String token = openViduService.createTokenForExistingSession(sessionId);
        return new BroadcastStartResponseDto(sessionId, token, vo.getNo(), vo.getStartTime());
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
        return new BroadcastStartResponseDto(dto.getSessionId(), token, broadcastNo, dto.getStartTime());
    }

    @Override
    public BroadcastViewDetailDto getDetailByScheduleNo(Long scheduleNo) {
        BroadcastViewDetailDto dto = broadcastMapper.findDetailByScheduleNo(scheduleNo);
        dto.setKeywords(broadcastMapper.findKeywordsByScheduleNo(scheduleNo));
        return dto;
    }

    @Override
    public BroadcastViewDetailDto getDetailByBroadcastNo(Long broadcastNo) {
        BroadcastViewDetailDto dto = broadcastMapper.findDetailByBroadcastNo(broadcastNo);

        if (dto == null) {
            throw new RuntimeException("❌ 방송 상세 정보를 찾을 수 없습니다. broadcastNo = " + broadcastNo);
        }

        Long scheduleNo = broadcastMapper.findScheduleNoByBroadcastNo(broadcastNo);
        dto.setScheduleNo(scheduleNo);
        dto.setKeywords(broadcastMapper.findKeywordsByScheduleNo(scheduleNo));

        return dto;
    }

    @Override
    @Transactional
    public void endBroadcast(Long broadcastNo) {
        broadcastMapper.endBroadcast(broadcastNo);
    }

    @Override
    @Transactional
    public void reportBroadcast(BroadcastReportRequestDto dto) {
        int result = broadcastMapper.insertReport(dto);
        if (result != 1) {
            throw new RuntimeException("방송 신고 등록 실패");
        }
    }

    @Override
    public BroadcastListResponseDto getLiveBroadcasts(BroadcastListRequestDto requestDto) {
        List<BroadcastListDto> list = broadcastMapper.selectLiveBroadcastsPaged(
                requestDto.getOffset(),
                requestDto.getSize()
        );

        // 시청자 수 계산
        for (BroadcastListDto dto : list) {
            int viewerCount = openViduService.getViewerCountByBroadcastNo(dto.getBroadcastNo());
            dto.setViewerCount(viewerCount);
        }

        // 총 개수 조회 (이거 따로 만들어야 함 → 다음 단계)
        long totalCount = broadcastMapper.countLiveBroadcasts();

        int totalPages = (int) Math.ceil((double) totalCount / requestDto.getSize());

        BroadcastListResponseDto response = new BroadcastListResponseDto();
        response.setContent(list);
        response.setTotalElements(totalCount);
        response.setTotalPages(totalPages);
        response.setCurrentPage(requestDto.getPage());

        return response;
    }

    @Override
    public Long findLiveBroadcastNoByScheduleNo(Long scheduleNo) {
        return broadcastMapper.findLiveBroadcastNoByScheduleNo(scheduleNo);
    }

    @Override
    @Transactional
    public void expireOverdueBroadcasts() {
        broadcastMapper.expireOverdueBroadcasts();
    }
}

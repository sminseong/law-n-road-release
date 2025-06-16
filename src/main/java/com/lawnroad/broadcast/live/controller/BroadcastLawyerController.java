package com.lawnroad.broadcast.live.controller;

import com.lawnroad.broadcast.live.dto.BroadcastStartDto;
import com.lawnroad.broadcast.live.dto.BroadcastStartResponseDto;
import com.lawnroad.broadcast.live.dto.ScheduleDetailDto;
import com.lawnroad.broadcast.live.service.BroadcastService;
import com.lawnroad.broadcast.live.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lawyer/broadcast")
@RequiredArgsConstructor
public class BroadcastLawyerController {

    private final BroadcastService broadcastService;
    private final ScheduleService scheduleService;

    @PostMapping("/start")
    public BroadcastStartResponseDto startBroadcast(
            @RequestBody BroadcastStartDto dto,
            @SessionAttribute(name = "userNo", required = false) Long userNo
    ) {
        // 세션이 아직 구현되지 않았으면 임시 하드코딩
        if (userNo == null) {
            userNo = 1L;
        }

        return broadcastService.startBroadcast(userNo, dto);
    }

    @GetMapping("/reconnect/{sessionId}")
    public ResponseEntity<BroadcastStartResponseDto> reconnect(
            @PathVariable String sessionId) {

        BroadcastStartResponseDto response = broadcastService.reconnectBroadcast(sessionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/schedule/{scheduleNo}")
    public ResponseEntity<ScheduleDetailDto> getScheduleDetail(@PathVariable Long scheduleNo) {
        ScheduleDetailDto dto = scheduleService.findDetailByScheduleNo(scheduleNo);
        return ResponseEntity.ok(dto);
    }
}

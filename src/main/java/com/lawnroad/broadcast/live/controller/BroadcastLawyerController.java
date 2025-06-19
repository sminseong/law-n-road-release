package com.lawnroad.broadcast.live.controller;

import com.lawnroad.broadcast.live.dto.BroadcastStartDto;
import com.lawnroad.broadcast.live.dto.BroadcastStartResponseDto;
import com.lawnroad.broadcast.live.dto.BroadcastViewDetailDto;
import com.lawnroad.broadcast.live.dto.ScheduleDetailDto;
import com.lawnroad.broadcast.live.service.BroadcastService;
import com.lawnroad.broadcast.live.service.ScheduleService;
import com.lawnroad.common.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lawyer/broadcast")
@RequiredArgsConstructor
public class BroadcastLawyerController {

    private final JwtTokenUtil jwtTokenUtil;
    private final BroadcastService broadcastService;
    private final ScheduleService scheduleService;

    @PostMapping("/start")
    public ResponseEntity<BroadcastStartResponseDto> startBroadcast(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody BroadcastStartDto dto
    ) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenUtil.parseToken(token);
        Long userNo = claims.get("no", Long.class);

        BroadcastStartResponseDto response = broadcastService.startBroadcast(userNo, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reconnect/{sessionId}")
    public ResponseEntity<BroadcastStartResponseDto> reconnect(
            @PathVariable String sessionId
    ) {
        BroadcastStartResponseDto response = broadcastService.reconnectBroadcast(sessionId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/schedule/{scheduleNo}")
    public ResponseEntity<ScheduleDetailDto> getScheduleDetail(@PathVariable Long scheduleNo) {
        ScheduleDetailDto dto = scheduleService.findDetailByScheduleNo(scheduleNo);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/view-detail/{scheduleNo}")
    public ResponseEntity<BroadcastViewDetailDto> getDetailByScheduleNo(@PathVariable Long scheduleNo) {
        return ResponseEntity.ok(broadcastService.getDetailByScheduleNo(scheduleNo));
    }

    @PostMapping("/end/{broadcastNo}")
    public ResponseEntity<?> endBroadcast(@PathVariable Long broadcastNo) {
        broadcastService.endBroadcast(broadcastNo);
        return ResponseEntity.ok("방송 종료 완료");
    }
}

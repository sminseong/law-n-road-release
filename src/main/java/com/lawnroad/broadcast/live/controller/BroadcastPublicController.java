package com.lawnroad.broadcast.live.controller;

import com.lawnroad.broadcast.live.mapper.BroadcastMapper;
import com.lawnroad.broadcast.live.service.BroadcastService;
import com.lawnroad.common.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public/broadcast")
@RequiredArgsConstructor
public class BroadcastPublicController {

    private final BroadcastService broadcastService;

    // 라이브 중인지 확인
    @GetMapping("/live-check/{scheduleNo}")
    public ResponseEntity<Map<String, Object>> checkLiveBroadcast(@PathVariable Long scheduleNo) {
        Long broadcastNo = broadcastService.findLiveBroadcastNoByScheduleNo(scheduleNo);

        if (broadcastNo != null) {
            return ResponseEntity.ok(Map.of(
                    "live", true,
                    "broadcastNo", broadcastNo
            ));
        } else {
            return ResponseEntity.ok(Map.of("live", false));
        }
    }

    @GetMapping("/expire-overdue")
    public void expireOverdueBroadcasts() {
        broadcastService.expireOverdueBroadcasts();
    }
}

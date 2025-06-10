package com.lawnroad.broadcast.live.controller;

import com.lawnroad.broadcast.live.dto.ScheduleDto;
import com.lawnroad.broadcast.live.dto.ScheduleRequestDto;
import com.lawnroad.broadcast.live.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/register")
    public ResponseEntity<String> registerSchedule(@RequestBody ScheduleRequestDto ScheduleRequestDto) {
        scheduleService.registerSchedule(ScheduleRequestDto);
        return ResponseEntity.ok("방송 스케줄이 성공적으로 등록되었습니다.");
    }
}

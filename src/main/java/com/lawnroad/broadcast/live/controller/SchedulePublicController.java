package com.lawnroad.broadcast.live.controller;

import com.lawnroad.broadcast.live.dto.ScheduleCalendarDto;
import com.lawnroad.broadcast.live.dto.ScheduleDateDto;
import com.lawnroad.broadcast.live.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/public/schedule")
@RequiredArgsConstructor
public class SchedulePublicController {

    private final ScheduleService scheduleService;


    @GetMapping("/month")
    public ResponseEntity<List<ScheduleCalendarDto>> getMonthlySchedule(@RequestParam String month) {
        List<ScheduleCalendarDto> result = scheduleService.getSchedulesByMonth(month);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{date}")
    public ResponseEntity<List<ScheduleDateDto>> getScheduleByDate(@PathVariable String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        List<ScheduleDateDto> schedules = scheduleService.getSchedulesByDate(parsedDate);
        return ResponseEntity.ok(schedules);
    }
}

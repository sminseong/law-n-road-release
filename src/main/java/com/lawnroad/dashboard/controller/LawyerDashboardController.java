package com.lawnroad.dashboard.controller;

import com.lawnroad.common.util.JwtTokenUtil;
import com.lawnroad.dashboard.dto.TodayScheduleDto;
import com.lawnroad.dashboard.dto.TomorrowBroadcastDto;
import com.lawnroad.dashboard.dto.TomorrowConsultationRequestDto;
import com.lawnroad.dashboard.dto.DailyCountDto;
import com.lawnroad.dashboard.service.LawyerDashboardService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lawyer/dashboard")
@RequiredArgsConstructor
public class LawyerDashboardController {
    private final LawyerDashboardService lawyerDashboardService;
    private final JwtTokenUtil jwtUtil;

    /**
     * 오늘 일정만 조회
     * GET /api/lawyer/dashboard/{lawyerNo}/schedule
     */
    @GetMapping("/schedule")
    public ResponseEntity<List<TodayScheduleDto>> getTodaySchedule(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.parseToken(token);
        Long userNo  = claims.get("no", Long.class);

        try {
            List<TodayScheduleDto> schedule = lawyerDashboardService.getTodaySchedule(userNo);

//            log.info("오늘 일정 조회 성공 - lawyerNo: {}, 일정 수: {}", lawyerNo, schedule.size());

            return ResponseEntity.ok(schedule);

        } catch (Exception e) {
//            log.error("오늘 일정 조회 실패 - lawyerNo: {}", lawyerNo, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 내일 상담 신청 목록 조회
     * GET /api/lawyer/dashboard/consultation-requests/tomorrow
     */
    @GetMapping("/consultation-requests/tomorrow")
    public ResponseEntity<List<TomorrowConsultationRequestDto>> getTomorrowConsultationRequests(@RequestHeader("Authorization") String authHeader) {

//        log.info("=== 내일 상담신청 API 호출됨 ===");

        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.parseToken(token);
        Long userNo  = claims.get("no", Long.class);

//        log.info("추출된 lawyerNo: {}", lawyerNo);

        try {
            List<TomorrowConsultationRequestDto> requests = lawyerDashboardService.getTomorrowConsultationRequests(userNo);

//            log.info("내일 상담 신청 목록 조회 성공 - lawyerNo: {}, 신청 수: {}", lawyerNo, requests.size());

            return ResponseEntity.ok(requests);

        } catch (Exception e) {
//            log.error("내일 상담 신청 목록 조회 실패 - lawyerNo: {}", lawyerNo, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 내일 방송 조회
     * GET /api/lawyer/dashboard/broadcasts/tomorrow
     */
    @GetMapping("/broadcasts/tomorrow")
    public ResponseEntity<List<TomorrowBroadcastDto>> getTomorrowBroadcasts(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.parseToken(token);
        Long userNo = claims.get("no", Long.class);

        try {
            List<TomorrowBroadcastDto> broadcasts = lawyerDashboardService.getTomorrowBroadcasts(userNo);
//            log.info("내일 방송 조회 성공 - lawyerNo: {}, 건수: {}", userNo, broadcasts.size());
            return ResponseEntity.ok(broadcasts);
        } catch (Exception e) {
//            log.error("내일 방송 조회 실패 - lawyerNo: {}", userNo, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 주간 상담 건수 조회
     * GET /api/lawyer/dashboard/weekly-consultations
     */
    @GetMapping("/weekly-consultations")
    public ResponseEntity<List<DailyCountDto>> getWeeklyConsultations(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.parseToken(token);
        Long userNo = claims.get("no", Long.class);

        try {
            List<DailyCountDto> list =
                    lawyerDashboardService.getWeeklyConsultations(userNo);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 주간 방송 건수 조회
     * GET /api/lawyer/dashboard/weekly-broadcasts
     */
    @GetMapping("/weekly-broadcasts")
    public ResponseEntity<List<DailyCountDto>> getWeeklyBroadcasts(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.parseToken(token);
        Long userNo = claims.get("no", Long.class);

        try {
            List<DailyCountDto> list =
                    lawyerDashboardService.getWeeklyBroadcasts(userNo);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}

package com.lawnroad.dashboard.controller;

import com.lawnroad.common.util.JwtTokenUtil;
import com.lawnroad.dashboard.dto.TodayScheduleDto;
import com.lawnroad.dashboard.dto.TomorrowBroadcastDto;
import com.lawnroad.dashboard.dto.TomorrowConsultationRequestDto;
import com.lawnroad.dashboard.service.LawyerDashboardService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @GetMapping("/{lawyerNo}/schedule")
    public ResponseEntity<List<TodayScheduleDto>> getTodaySchedule(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.parseToken(token);
        Long lawyerNo = claims.get("no", Long.class);

        try {
            List<TodayScheduleDto> schedule = lawyerDashboardService.getTodaySchedule(lawyerNo);

            log.info("오늘 일정 조회 성공 - lawyerNo: {}, 일정 수: {}", lawyerNo, schedule.size());

            return ResponseEntity.ok(schedule);

        } catch (Exception e) {
            log.error("오늘 일정 조회 실패 - lawyerNo: {}", lawyerNo, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 내일 상담 신청 목록 조회
     * GET /api/lawyer/dashboard/consultation-requests/tomorrow
     */
    @GetMapping("/consultation-requests/tomorrow")
    public ResponseEntity<List<TomorrowConsultationRequestDto>> getTomorrowConsultationRequests(@RequestHeader("Authorization") String authHeader) {

        log.info("=== 내일 상담신청 API 호출됨 ==="); // 🔥 추가

        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.parseToken(token);
        Long lawyerNo = claims.get("no", Long.class);

        log.info("추출된 lawyerNo: {}", lawyerNo); // 🔥 추가

        try {
            List<TomorrowConsultationRequestDto> requests = lawyerDashboardService.getTomorrowConsultationRequests();

            log.info("내일 상담 신청 목록 조회 성공 - lawyerNo: {}, 신청 수: {}", lawyerNo, requests.size());

            return ResponseEntity.ok(requests);

        } catch (Exception e) {
            log.error("내일 상담 신청 목록 조회 실패 - lawyerNo: {}", lawyerNo, e);
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
            log.info("내일 방송 조회 성공 - lawyerNo: {}, 건수: {}", userNo, broadcasts.size());
            return ResponseEntity.ok(broadcasts);
        } catch (Exception e) {
            log.error("내일 방송 조회 실패 - lawyerNo: {}", userNo, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 주간 상담 & 방송 통계 조회
     * GET /api/lawyer/dashboard/weekly-stats
     */
    @GetMapping("/weekly-stats")
    public ResponseEntity<Map<String, Object>> getWeeklyStats(@RequestHeader("Authorization") String authHeader) {
        log.info("주간 통계 조회 요청");

        try {
            // JWT 토큰에서 변호사 번호 추출
            String token = authHeader.replace("Bearer ", "");
            Claims claims = jwtUtil.parseToken(token);
            Long lawyerNo = claims.get("no", Long.class);

            log.info("주간 통계 조회 - 변호사 번호: {}", lawyerNo);

            Map<String, int[]> weeklyStats = lawyerDashboardService.getWeeklyStats(lawyerNo);

            log.info("🔥 서비스에서 반환된 데이터: {}", weeklyStats);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "주간 통계 조회 성공");
            response.put("data", weeklyStats);

            log.info("🔥 최종 응답 데이터: {}", response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("주간 통계 조회 실패: ", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "주간 통계 조회 실패: " + e.getMessage());
            errorResponse.put("data", Map.of(
                    "consultations", new int[]{0, 0, 0, 0, 0, 0, 0},
                    "broadcasts", new int[]{0, 0, 0, 0, 0, 0, 0}
            ));

            return ResponseEntity.ok(errorResponse);
        }
    }
}

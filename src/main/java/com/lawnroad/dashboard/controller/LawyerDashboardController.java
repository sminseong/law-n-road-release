package com.lawnroad.dashboard.controller;

import com.lawnroad.common.util.JwtTokenUtil;
import com.lawnroad.dashboard.dto.*;
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

        Long userNo = extractUserNo(authHeader);
        try {
            List<TodayScheduleDto> schedule = lawyerDashboardService.getTodaySchedule(userNo);
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            log.error("오늘 일정 조회 중 오류 발생 - userNo={}", userNo, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 내일 상담 신청 목록 조회
     * GET /api/lawyer/dashboard/consultation-requests/tomorrow
     */
    @GetMapping("/consultation-requests/tomorrow")
    public ResponseEntity<List<TomorrowConsultationRequestDto>> getTomorrowConsultationRequests(@RequestHeader("Authorization") String authHeader) {
        Long userNo = extractUserNo(authHeader);
        try {
            List<TomorrowConsultationRequestDto> requests =
                    lawyerDashboardService.getTomorrowConsultationRequests(userNo);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            log.error("내일 상담 신청 목록 조회 중 오류 발생 - userNo={}", userNo, e);
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
        Long userNo = extractUserNo(authHeader);
        try {
            List<TomorrowBroadcastDto> broadcasts =
                    lawyerDashboardService.getTomorrowBroadcasts(userNo);
            return ResponseEntity.ok(broadcasts);
        } catch (Exception e) {
            log.error("내일 방송 조회 중 오류 발생 - userNo={}", userNo, e);
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
        Long userNo = extractUserNo(authHeader);
        try {
            List<DailyCountDto> consultations =
                    lawyerDashboardService.getWeeklyConsultations(userNo);
            return ResponseEntity.ok(consultations);
        } catch (Exception e) {
            log.error("주간 상담 건수 조회 중 오류 발생 - userNo={}", userNo, e);
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

        Long userNo = extractUserNo(authHeader);
        try {
            List<DailyCountDto> broadcasts =
                    lawyerDashboardService.getWeeklyBroadcasts(userNo);
            return ResponseEntity.ok(broadcasts);
        } catch (Exception e) {
            log.error("주간 방송 건수 조회 중 오류 발생 - userNo={}", userNo, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/monthly-revenue")
    public ResponseEntity<MonthlyRevenueDto> getMonthlyRevenue(
        @RequestHeader("Authorization") String authHeader) {
        Long userNo = extractUserNo(authHeader);
        try {
            MonthlyRevenueDto revenue = lawyerDashboardService.getMonthlyRevenue(userNo);
            return ResponseEntity.ok(revenue);
        } catch (Exception e) {
            log.error("이달의 수익 조회 중 오류 발생 - userNo={}", userNo, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 이달의 템플릿 판매 건수 조회
     * GET /api/lawyer/dashboard/monthly-template-sales
     */
    @GetMapping("/monthly-template-sales")
    public ResponseEntity<MonthlyTemplateSalesDto> getMonthlyTemplateSales(
            @RequestHeader("Authorization") String authHeader) {

        Long userNo = extractUserNo(authHeader);
        //log.info("이달의 템플릿 판매 건수 조회 시작 - userNo={}", userNo);

        try {
            MonthlyTemplateSalesDto sales = lawyerDashboardService.getMonthlyTemplateSales(userNo);
            //log.info("이달의 템플릿 판매 건수 조회 완료 - userNo={}, 이달 판매: {}건",
            //        userNo, sales.getMonthlySalesCount());
            return ResponseEntity.ok(sales);
        } catch (Exception e) {
            log.error("이달의 템플릿 판매 건수 조회 실패 - userNo: {}", userNo, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    //------------------ 공통 로직 ------------------
    private Long extractUserNo(String authHeader) {
        String token = authHeader.replaceFirst("^Bearer ", "");
        Claims claims = jwtUtil.parseToken(token);
        return claims.get("no", Long.class);
    }

}

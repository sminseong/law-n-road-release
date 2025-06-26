package com.lawnroad.account.controller;


import com.lawnroad.account.dto.BroadcastReportDTO;
import com.lawnroad.account.dto.ChatReportDto;
import com.lawnroad.account.service.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/report")
@RequiredArgsConstructor
public class AdminReportController {

    private final AdminReportService adminReportService;


    @GetMapping("/unpenalized")
    public List<BroadcastReportDTO> getUnpenalizedReports() {
        return adminReportService.getUnpenalizedReports();
    }


    @PostMapping("/penalty")
    public ResponseEntity<Void> applyPenalty(@RequestBody Map<String, Long> body) {
        Long broadcastNo = body.get("broadcastNo");
        adminReportService.applyPenalty(broadcastNo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/client/penalized")
    public List<ChatReportDto> getPenalizedChatReports() {
        return adminReportService.getPenalizedChatReports();
    }

    @PostMapping("/penalty/client")
    public ResponseEntity<Void> penalizeClient(@RequestBody Map<String, Long> request) {
        Long userNo = request.get("userNo");
        adminReportService.penalizeClientByUserNo(userNo); // 기존 서비스와 분리
        return ResponseEntity.ok().build();
    }

    @PostMapping("/penalty_chatNo")
    public ResponseEntity<Void> applyPenalty1(@RequestBody Map<String, Long> body) {
        Long reportNo = body.get("reportNo");
        Long userNo = body.get("userNo");
        adminReportService.penalizeReport(reportNo);
        adminReportService.applyPenaltyClient(userNo);
        return ResponseEntity.ok().build();
    }







}

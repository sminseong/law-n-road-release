package com.lawnroad.broadcast.chat.controller;

import com.lawnroad.broadcast.chat.dto.ChatReportDTO;
import com.lawnroad.broadcast.chat.service.ChatReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ChatReportController {

    @Autowired
    private ChatReportService chatReportService;


    @PostMapping("/api/client/chat/report")
    public void reportChat(@RequestBody ChatReportDTO req) {
        chatReportService.saveChatReport(req);
    }

    @PostMapping("/api/Lawyer/chat/report")
    public void lawyerReportChat(@RequestBody ChatReportDTO req) {
        chatReportService.saveChatReport(req);
    }

    @GetMapping("/api/client/is-stopped/{userNo}")
    public Map<String, Integer> getIsStopped(@PathVariable Long userNo) {
        int isStopped = chatReportService.getIsStopped(userNo);
        return Map.of("is_stopped", isStopped);
    }
}

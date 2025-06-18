package com.lawnroad.broadcast.chat.controller;

import com.lawnroad.broadcast.chat.dto.ChatReportDTO;
import com.lawnroad.broadcast.chat.service.ChatReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}

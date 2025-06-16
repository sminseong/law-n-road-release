package com.lawnroad.broadcast.chat.controller;

import com.lawnroad.broadcast.chat.dto.AutoReplyDTO;
import com.lawnroad.broadcast.chat.service.AutoReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AutoReplyController {

    @Autowired
    private final AutoReplyService autoReplyService;


    @PostMapping("/api/Lawyer/nightBot")
    public void addAutoReply(@RequestBody AutoReplyDTO autoReplyDTO) {
        autoReplyService.insertAutoReply(autoReplyDTO);
    }

    // 전체 리스트
    @GetMapping("/api/Lawyer/nightBot")
    public List<AutoReplyDTO> getAllAutoReplies(@RequestParam int scheduleNo) {
        return autoReplyService.findByAutoReply(scheduleNo);
    }

    // 삭제
//    @DeleteMapping("/api/nightBot/{id}")
//    public void deleteAutoReply(@PathVariable Long id) {
//        autoReplyService.deleteAutoReply(id);
//    }



}

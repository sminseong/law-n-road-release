package com.lawnroad.broadcast.chat.controller;

import com.lawnroad.broadcast.chat.dto.PreQuestionDTO;
import com.lawnroad.broadcast.chat.service.PreQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PreQuestionController {
    @Autowired
    private PreQuestionService preQuestionService;

    @GetMapping("/client/broadcasts/schedule/{scheduleNo}/preQuestion")
    public List<PreQuestionDTO> reportChat(@PathVariable int scheduleNo) {
       return preQuestionService.findBySchedule(scheduleNo);
    }



}

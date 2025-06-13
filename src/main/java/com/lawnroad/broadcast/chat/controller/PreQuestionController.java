package com.lawnroad.broadcast.chat.controller;

import com.lawnroad.broadcast.chat.dto.LawyerPreQuestion;
import com.lawnroad.broadcast.chat.dto.PreQuestionDTO;
import com.lawnroad.broadcast.chat.service.PreQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PreQuestionController {
    @Autowired
    private PreQuestionService preQuestionService;

    @GetMapping("/api/broadcasts/schedule/{scheduleNo}/preQuestion")
    public PreQuestionDTO PreQuestion(@PathVariable int scheduleNo) {
       return preQuestionService.findByPreQuestion(scheduleNo);
    }

    @GetMapping("/api/Lawyer/broadcasts/schedule/{scheduleNo}/preQuestion")
    public List<LawyerPreQuestion> findByPreQuestionLawyer(@PathVariable int scheduleNo) {
       return preQuestionService.findByPreQuestionLawyer(scheduleNo);
    }

    @PostMapping("/api/preQuestions/save")
    public void deletePreQuestion(@RequestBody List<Long> preQuestionNo) {
         preQuestionService.deletePreQuestion(preQuestionNo);
    }

}

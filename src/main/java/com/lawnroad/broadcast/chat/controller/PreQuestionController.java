package com.lawnroad.broadcast.chat.controller;

import com.lawnroad.broadcast.chat.dto.LawyerPreQuestion;
import com.lawnroad.broadcast.chat.dto.PreQuestionDTO;
import com.lawnroad.broadcast.chat.dto.PreQuestionItem;
import com.lawnroad.broadcast.chat.service.PreQuestionService;
import com.lawnroad.common.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PreQuestionController {
    @Autowired
    private PreQuestionService preQuestionService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/api/client/broadcasts/schedule/{scheduleNo}/preQuestion")
    public void insertPreQuestion(
            @RequestBody PreQuestionItem preQuestionItem,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenUtil.parseToken(token);
        String nickname = claims.get("nickname", String.class);
        Long userNo = claims.get("no", Long.class); // userNo도 추출!

        preQuestionItem.setNickname(nickname);
        preQuestionItem.setUserNo(userNo);
        preQuestionService.insertPreQuestion(preQuestionItem);
    }


    @GetMapping("/api/client/broadcasts/schedule/{scheduleNo}/preQuestion")
    public PreQuestionDTO PreQuestion(@PathVariable int scheduleNo) {
       return preQuestionService.findByPreQuestion(scheduleNo);
    }

    @GetMapping("/api/Lawyer/broadcasts/schedule/{scheduleNo}/preQuestion")
    public List<LawyerPreQuestion> findByPreQuestionLawyer(@PathVariable int scheduleNo) {
       return preQuestionService.findByPreQuestionLawyer(scheduleNo);
    }

    @PostMapping("/api/client/preQuestions/save")
    public void deleteLawyerPreQuestion(@RequestBody List<Long> preQuestionNo) {
        preQuestionService.deleteLawyerPreQuestion(preQuestionNo);
    }


    @DeleteMapping("/api/client/broadcasts/schedule/{scheduleNo}/preQuestion/{questionNo}")
    public void deletePreQuestion(
            @PathVariable Long scheduleNo,
            @PathVariable Long questionNo,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenUtil.parseToken(token);
        Long userNo = claims.get("no", Long.class); // userNo 추출

        preQuestionService.deletePreQuestionByUser(scheduleNo, questionNo, userNo);
    }

    @GetMapping("/api/client/broadcasts/schedule/{broadcastNo}")
    public List<LawyerPreQuestion> broadcastPreQuestion(@PathVariable Long broadcastNo) {
        return preQuestionService.findByBroadcastPreQuestion(broadcastNo);
    }

    @GetMapping("/api/Lawyer/broadcasts/schedule/{broadcastNo}")
    public List<LawyerPreQuestion> broadcastPreQuestionLawyer(@PathVariable Long broadcastNo) {
        return preQuestionService.findByBroadcastPreQuestion(broadcastNo);
    }



}

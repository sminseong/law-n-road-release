package com.lawnroad.board.controller;

import com.lawnroad.board.dto.AnswerRegisterDto;
import com.lawnroad.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/lawyer/comment")
@RequiredArgsConstructor
public class LawyerCommentController {

    private final CommentService commentService;

    // 답변 등록
    @PostMapping
    public ResponseEntity<Void> registerAnswer(@RequestBody AnswerRegisterDto dto) {
        System.out.println("🔥 DTO: " + dto);
        log.info("답변 등록 요청: {}", dto);
        commentService.registerAnswer(dto);
        return ResponseEntity.status(201).build(); // 201 Created
    }

}

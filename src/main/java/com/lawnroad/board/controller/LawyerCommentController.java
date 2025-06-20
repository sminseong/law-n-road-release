package com.lawnroad.board.controller;

import com.lawnroad.board.dto.CommentRegisterDto;
import com.lawnroad.board.dto.CommentResponse;
import com.lawnroad.board.dto.MyCommentResponseDto;
import com.lawnroad.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lawyer/comment")
@RequiredArgsConstructor
public class LawyerCommentController {

    private final CommentService commentService;

    // 답변 등록
    @PostMapping
    public ResponseEntity<Void> registerComment(@RequestBody CommentRegisterDto dto) {
        System.out.println("🔥 DTO: " + dto);
        log.info("답변 등록 요청: {}", dto);
        commentService.registerComment(dto);
        return ResponseEntity.status(201).build(); // 201 Created
    }
    @GetMapping("/{boardNo}")
    public ResponseEntity<List<CommentResponse>> getComment(@PathVariable Long boardNo) {
        log.info("📥 댓글 목록 요청 boardNo: {}", boardNo);
        List<CommentResponse> comments = commentService.getCommentsByBoardNo(boardNo);
        log.info("📤 응답 데이터 수: {}, 첫 번째 댓글: {}", comments.size(), comments.isEmpty() ? "없음" : comments.get(0));
        return ResponseEntity.ok(comments);
    }
    @GetMapping("/answers")
    public ResponseEntity<Page<MyCommentResponseDto>> getMyAnswers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userNo = 24L; //TODO 나중 하드코딩 바꿔야됨
        System.out.println(page);
        System.out.println(size);
        Page<MyCommentResponseDto> result = commentService.getMyComments(userNo, page, size);
        return ResponseEntity.ok(result);
    }

}

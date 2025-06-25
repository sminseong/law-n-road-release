package com.lawnroad.board.controller;

import com.lawnroad.board.dto.*;
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
        commentService.registerComment(dto);
        return ResponseEntity.status(201).build();
    }
    // 특정 게시글의 전체 댓글 목록
    @GetMapping("/board/{boardNo}")
    public ResponseEntity<List<CommentResponse>> getComment(@PathVariable Long boardNo) {
        List<CommentResponse> comments = commentService.getCommentsByBoardNo(boardNo);
        return ResponseEntity.ok(comments);
    }
    //내가 쓴 답변 목록
    @GetMapping("/answers")
    public ResponseEntity<Page<MyCommentResponseDto>> getMyAnswers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userNo = 32L; //TODO 나중 하드코딩 바꿔야됨
//        System.out.println(page);
//        System.out.println(size);
        Page<MyCommentResponseDto> result = commentService.getMyComments(userNo, page, size);
        return ResponseEntity.ok(result);
    }
    // 특정 댓글 상세 조회
    @GetMapping("/detail/{commentId}")
    public ResponseEntity<CommentDetailDto> getCommentDetail(@PathVariable Long commentId) {
        Long userNo = 32L; // 임시로 하드코딩
        return ResponseEntity.ok(commentService.findById(commentId, userNo));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(@PathVariable Long commentId, @RequestBody CommentUpdateDto dto) {
        commentService.updateComment(commentId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

}

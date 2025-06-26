package com.lawnroad.board.controller;

import com.lawnroad.board.dto.*;
import com.lawnroad.board.service.CommentService;
import com.lawnroad.common.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
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
    private final JwtTokenUtil jwtUtil;

    // 답변 등록
    @PostMapping
    public ResponseEntity<Void> registerComment(@RequestBody CommentRegisterDto dto,
                                                @RequestHeader("Authorization") String authHeader) {
        // JWT에서 userNo 추출
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.parseToken(token);
        Long userNo = claims.get("no", Long.class);

        dto.setUserNo(userNo);

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
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.parseToken(token);
        Long userNo = claims.get("no", Long.class);

        Page<MyCommentResponseDto> result = commentService.getMyComments(userNo, page, size);
        return ResponseEntity.ok(result);
    }
    // 특정 댓글 상세 조회
    @GetMapping("/detail/{commentId}")
    public ResponseEntity<CommentDetailDto> getCommentDetail(@PathVariable Long commentId) {

        return ResponseEntity.ok(commentService.findById(commentId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(@PathVariable Long commentId, @RequestBody CommentUpdateDto dto, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.parseToken(token);
        Long userNo = claims.get("no", Long.class);

        // 검증 로직 (작성자 본인 확인)
        CommentDetailDto comment = commentService.findById(commentId);
        if (!comment.getUserNo().equals(userNo)) {
            return ResponseEntity.status(403).build();
        }

        dto.setUserNo(userNo); // 보안상 다시 주입
        commentService.updateComment(commentId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.parseToken(token);
        Long userNo = claims.get("no", Long.class);

        CommentDetailDto comment = commentService.findById(commentId);
        if (!comment.getUserNo().equals(userNo)) {
            return ResponseEntity.status(403).build(); // 권한 없음
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

}

package com.lawnroad.board.controller;

import com.lawnroad.board.dto.*;
import com.lawnroad.board.service.BoardService;
import com.lawnroad.board.service.CommentService;
import com.lawnroad.common.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client/qna") // 로그인 사용자 전용 API 경로

public class ClientBoardController {
    private final BoardService boardService;
    private final CommentService commentService;
    private final JwtTokenUtil jwtUtil;

    //게시글 등록
    @PostMapping
    public ResponseEntity<String> register(@RequestBody BoardCreateDto dto, @RequestHeader("Authorization") String authHeader) {
        System.out.println(dto);
        System.out.println(authHeader);

        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.parseToken(token);
        Long userNo = claims.get("no", Long.class);

        dto.setUserNo(userNo); // 여기서 주입

        System.out.println(dto);
        boardService.register(dto);
        return ResponseEntity.ok("게시글이 성공적으로 등록되었습니다.");
    }

    //게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBoard( @PathVariable("id") Long id, @RequestBody BoardUpdateDto dto) {
        dto.setNo(id);
        boardService.updateBoard(dto);
        return ResponseEntity.ok().build();
    }

    //게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }

    //마이페이지 내 답변
    @GetMapping("/my")
    public ResponseEntity<List<BoardSummaryDto>> getMyQna(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.parseToken(token);
        Long userNo = claims.get("no", Long.class);

        List<BoardSummaryDto> boards = boardService.getBoardsByUserNo(userNo);
        return ResponseEntity.ok(boards);
    }

    //채택 기능
    @PostMapping("/select")
    public ResponseEntity<?> selectAnswer(@RequestBody CommentSelectDto dto) {
        commentService.selectAnswer(dto);
        return ResponseEntity.ok().build();
    }
}

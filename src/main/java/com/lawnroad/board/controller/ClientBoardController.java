package com.lawnroad.board.controller;

import com.lawnroad.board.dto.BoardCreateDto;
import com.lawnroad.board.dto.BoardDetailDto;
import com.lawnroad.board.dto.BoardListDto;
import com.lawnroad.board.dto.BoardUpdateDto;
import com.lawnroad.board.service.BoardService;
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

    //게시글 등록
    @PostMapping
    public ResponseEntity<String> register(@RequestBody BoardCreateDto dto) {
//        System.out.println(dto.toString());
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
}

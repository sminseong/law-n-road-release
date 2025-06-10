package com.lawnroad.board.controller;

import com.lawnroad.board.dto.BoardListDto;
import com.lawnroad.board.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client/qna")
public class ClientBoardController {
    private final BoardService boardService;

    public ClientBoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 페이지, 사이즈 유효성 검증
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        // 조회
        List<BoardListDto> content = boardService.getBoardList(page, size);

        // 응답 구조: Map 또는 PageResponseDto
        Map<String, Object> resp = new HashMap<>();
        resp.put("content", content);
        resp.put("page", page);
        resp.put("size", size);

        // total 관련은 나중에 추가

        return ResponseEntity.ok(resp);
    }
}

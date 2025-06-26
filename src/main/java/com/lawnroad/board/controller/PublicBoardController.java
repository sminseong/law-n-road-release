package com.lawnroad.board.controller;

import com.lawnroad.board.dto.BoardCommentResponseDto;
import com.lawnroad.board.dto.BoardDetailDto;
import com.lawnroad.board.dto.BoardListDto;
import com.lawnroad.board.service.BoardService;
import com.lawnroad.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/qna") // 비회원 접근 허용 경로
public class PublicBoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    //상담 게시글 목록 조회 (페이징 포함)
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "1") int page, // 쿼리 파라미터 page, 기본 1
            @RequestParam(defaultValue = "10") int size // 쿼리 파라미터 size, 기본 10
    ) {
        // 페이지, 사이즈 유효성 검증
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        // 게시글 목록 조회 (offset 계산 포함)
        List<BoardListDto> content = boardService.getBoardList(page, size);
        int totalElements = boardService.getBoardCount(); // 전체 게시글 수 조회
        int totalPages = (int) Math.ceil((double) totalElements / size); // 전체 페이지 수 계산

        // Vue에서 활용할 응답 구조를 Map으로 구성
        Map<String, Object> resp = new HashMap<>();
        resp.put("content", content);
        resp.put("page", page);
        resp.put("size", size);
        resp.put("totalElements", totalElements);
        resp.put("totalPages", totalPages);

        return ResponseEntity.ok(resp); // 클라이언트에게 200 OK와 함께 응답 반환
    }
    //전체 게시글 수 조회
    @GetMapping("/count")
    public ResponseEntity<Integer> getBoardCount() {
        // 단순 count 조회 결과 반환
        return ResponseEntity.ok(boardService.getBoardCount());
    }
    //게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardDetailDto> getDetail(@PathVariable("id") Long id) {
        //System.out.println(id);
        BoardDetailDto dto = boardService.getBoardDetail(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{boardId}/comments")
    public ResponseEntity<List<BoardCommentResponseDto>> getBoardComments(@PathVariable Long boardId) {
        List<BoardCommentResponseDto> comments = commentService.findBoardCommentsByBoardId(boardId);
        return ResponseEntity.ok(comments);
    }
}

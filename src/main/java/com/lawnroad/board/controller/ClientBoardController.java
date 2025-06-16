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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client/qna") // 이 컨트롤러의 모든 API는 /api/client/qna로 시작함
public class ClientBoardController {
    private final BoardService boardService;

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
 //   전체 게시글 수 조회
    @GetMapping("/count") // GET /api/client/qna/count
    public ResponseEntity<Integer> getBoardCount() {
        // 단순 count 조회 결과 반환
        return ResponseEntity.ok(boardService.getBoardCount());
    }

    //게시글 등록
    @PostMapping
    //@PreAuthorize("hasRole('CLIENT')")테스트 할 수 있어서 추가함
    public ResponseEntity<String> register(@RequestBody BoardCreateDto dto) {
        boardService.register(dto);
        return ResponseEntity.ok("게시글이 성공적으로 등록되었습니다.");
    }

    //게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardDetailDto> getDetail(@PathVariable("id") Long id) {
        //System.out.println(id);
        BoardDetailDto dto = boardService.getBoardDetail(id);
        return ResponseEntity.ok(dto);
    }

    //게시글 수정하기
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBoard(
            @PathVariable("id") Long id,
            @RequestBody BoardUpdateDto dto
    ) {
        log.info("수정 요청 수신: {}", dto);
        dto.setNo(id); // ID 직접 주입
        boardService.updateBoard(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        System.out.println(id);
        boardService.deleteBoard(id); // 내부적으로 boardMapper.deleteBoard(id) 호출
        return ResponseEntity.noContent().build(); // HTTP 204
    }
}

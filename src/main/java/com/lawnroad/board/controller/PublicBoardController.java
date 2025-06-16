//package com.lawnroad.board.controller;
//
//
//import com.lawnroad.board.service.BoardService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/auth/qna")
//public class PublicBoardController {
//
//
//    private final BoardService boardService;
//    //전체 게시글 수 조회
//    @GetMapping("/count") // GET /api/client/qna/count
//    public ResponseEntity<Integer> getBoardCount() {
//        // 단순 count 조회 결과 반환
//        return ResponseEntity.ok(boardService.getBoardCount());
//    }
//
//
//
//}

package com.lawnroad.board.service;

import com.lawnroad.board.dto.BoardListDto;

import java.util.List;

public interface BoardService {
    /**
     * 게시글 목록 조회
     * @param page 1부터 시작하는 페이지 번호
     * @param size 페이지당 게시글 수
     * @return 조회된 게시글 목록 (BoardListDto 리스트)
     */
    List<BoardListDto> getBoardList(int page, int size);
    /**
     * 전체 게시글 수 조회
     * @return 게시글 총 개수 (페이징 계산용)
     */
    int getBoardCount();

}

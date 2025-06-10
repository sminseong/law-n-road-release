package com.lawnroad.board.service;

import com.lawnroad.board.dto.BoardListDto;

import java.util.List;

public interface BoardService {
    /**
     * 게시글 목록 조회
     * @param page 1부터 시작하는 페이지 번호
     * @param size 페이지당 항목 수
     * @return 조회된 BoardListDto 목록
     */
    List<BoardListDto> getBoardList(int page, int size);

}

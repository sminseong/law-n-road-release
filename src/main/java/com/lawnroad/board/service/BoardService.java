package com.lawnroad.board.service;

import com.lawnroad.board.dto.BoardCreateDto;
import com.lawnroad.board.dto.BoardDetailDto;
import com.lawnroad.board.dto.BoardListDto;
import com.lawnroad.board.dto.BoardUpdateDto;

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

    /**
     * 게시글 등록
     * @param dto 등록할 게시글 데이터 (제목, 내용, 사건발생일자, 작성자 ID 포함)
     */
    void register(BoardCreateDto dto);

    /**
     * 게시글 상세 조회
     * @param id 게시글 번호 (PK)
     * @return 게시글 상세 정보 DTO
     */
    BoardDetailDto getBoardDetail(Long id);

    void updateBoard(BoardUpdateDto dto);

    void deleteBoard(Long id);

}

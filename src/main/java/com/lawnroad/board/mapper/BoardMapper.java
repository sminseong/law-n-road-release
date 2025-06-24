package com.lawnroad.board.mapper;

import com.lawnroad.board.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {
    // 페이징 목록 조회 (offset & limit)
    List<BoardListDto> selectBoardList(@Param("offset") int offset, @Param("limit") int limit);
    // 게시글 총 개수 조회
    int getBoardCount();

    void insertBoard(BoardCreateDto dto);

    BoardDetailDto selectBoardById(Long id);

    int updateBoard(BoardUpdateDto dto);

    int deleteBoard(Long id);

    List<BoardSummaryDto> findBoardsByUserNo(@Param("userNo") Long userNo);
}

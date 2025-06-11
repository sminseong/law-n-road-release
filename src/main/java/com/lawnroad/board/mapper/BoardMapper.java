package com.lawnroad.board.mapper;

import com.lawnroad.board.dto.BoardCreateDto;
import com.lawnroad.board.dto.BoardListDto;
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
}

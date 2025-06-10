package com.lawnroad.board.mapper;

import com.lawnroad.board.dto.BoardListDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {
    // 목록 조회: offset, limit 파라미터를 @Param으로 지정
    List<BoardListDto> selectBoardList(@Param("offset") int offset, @Param("limit") int limit);
}

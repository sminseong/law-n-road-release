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

    //[공개] 게시글 상세 정보 조회
    BoardDetailDto selectBoardById(Long id);

    int updateBoard(BoardUpdateDto dto);

    int deleteBoard(Long id);

    //[회원] 특정 회원이 작성한 게시글 목록 조회 - 마이페이지용
    List<BoardSummaryDto> findBoardsByUserNo(@Param("userNo") Long userNo);

    // 게시글 단건 조회 (채택 시 작성자 검증용)
    BoardDetailDto findById(@Param("id") Long id);
}

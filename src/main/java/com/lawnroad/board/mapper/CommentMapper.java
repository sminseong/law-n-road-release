package com.lawnroad.board.mapper;

import com.lawnroad.board.dto.CommentRegisterDto;
import com.lawnroad.board.dto.CommentResponse;
import com.lawnroad.board.dto.MyCommentResponseDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper {
    void insertComment(CommentRegisterDto dto);
    List<CommentResponse> findByBoardNo(Long boardNo);

    //  내 답변 목록 페이징 조회
    List<MyCommentResponseDto> findMyComments(@Param("userNo") Long userNo,
                                              @Param("offset") int offset,
                                              @Param("limit") int limit);

    // 총 개수 (페이지 계산용)
    int countMyComments(@Param("userNo") Long userNo);

}

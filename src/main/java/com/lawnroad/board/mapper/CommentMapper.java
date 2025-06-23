package com.lawnroad.board.mapper;

import com.lawnroad.board.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper {
    // 댓글(답변) 등록
    void insertComment(CommentRegisterDto dto);
    // 특정 게시글에 달린 전체 댓글 목록을 조회
    List<CommentResponse> findByBoardNo(Long boardNo);

    // 로그인한 변호사가 작성한 답변 목록을 페이징하여 조회
    List<MyCommentResponseDto> findMyComments(@Param("userNo") Long userNo,
                                              @Param("offset") int offset,
                                              @Param("limit") int limit);

    // 로그인한 변호사가 작성한 전체 답변 개수를 반환 (페이징 계산용
    int countMyComments(@Param("userNo") Long userNo);

    // 댓글 ID를 기반으로 댓글 상세정보(변호사 이름, 이미지, 내용 등)를 조회
    CommentDetailDto findById(@Param("commentId") Long commentId);

    //댓글(답변) 수정
    void updateComment(@Param("commentId") Long commentId, @Param("dto") CommentUpdateDto dto);

    //댓글(답변) 삭제
    void deleteComment(Long commentId);

    boolean existsByBoardNoAndUserNo(@Param("boardNo") Long boardNo, @Param("userNo") Long userNo);

    List<BoardCommentResponseDto> findBoardCommentsByBoardId(@Param("boardId") Long boardId);
}

package com.lawnroad.board.service;

import com.lawnroad.board.dto.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {
    // 새로운 댓글(답변)을 등록
    void registerComment(CommentRegisterDto dto);

    //특정 게시글에 달린 모든 댓글(답변)을 조회
    List<CommentResponse> getCommentsByBoardNo(Long boardNo);

    //로그인한 변호사 본인의 답변 목록을 페이징 조회
    Page<MyCommentResponseDto> getMyComments(Long userNo, int page, int size);

    //[변호사] 특정 답변(commentId)의 상세 내용을 조회
    CommentDetailDto findById(Long commentNo);

    // [변호사] 기존 답변 내용을 수정
    void updateComment(Long commentId, CommentUpdateDto dto);

    //[변호사] 답변 삭제
    void deleteComment(Long commentId);

    //[공개] 게시글 ID를 기반으로 모든 답변 리스트 반환
    List<BoardCommentResponseDto> findBoardCommentsByBoardId(@Param("boardId") Long boardId);

    //[의뢰인] 특정 답변을 채택 처리
    void selectAnswer(CommentSelectDto dto);
}
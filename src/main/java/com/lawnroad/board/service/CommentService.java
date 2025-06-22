package com.lawnroad.board.service;

import com.lawnroad.board.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {
    // 새로운 댓글(답변)을 등록
    void registerComment(CommentRegisterDto dto);

    //특정 게시글에 달린 모든 댓글(답변)을 조회
    List<CommentResponse> getCommentsByBoardNo(Long boardNo);

    //로그인한 변호사 본인의 답변 목록을 페이징 조회
    Page<MyCommentResponseDto> getMyComments(Long userNo, int page, int size);

    //특정 댓글의 상세 내용을 조회
    CommentDetailDto findById(Long commentId, Long userNo);

    //댓글(답변) 내용을 수정
    void updateComment(Long commentId, CommentUpdateDto dto);

    //댓글(답변)을 삭제
    void deleteComment(Long commentId);

}
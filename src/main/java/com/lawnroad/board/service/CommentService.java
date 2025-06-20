package com.lawnroad.board.service;

import com.lawnroad.board.dto.CommentRegisterDto;
import com.lawnroad.board.dto.CommentResponse;
import com.lawnroad.board.dto.MyCommentResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {
    // 답변 등록
    void registerComment(CommentRegisterDto dto);
    List<CommentResponse> getCommentsByBoardNo(Long boardNo);
    Page<MyCommentResponseDto> getMyComments(Long userNo, int page, int size);
}

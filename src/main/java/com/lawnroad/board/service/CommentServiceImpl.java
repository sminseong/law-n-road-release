package com.lawnroad.board.service;

import com.lawnroad.board.dto.*;
import com.lawnroad.board.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    @Override
    public void registerComment(CommentRegisterDto dto) {
        // 🔐 중복 작성 방지: 이미 해당 변호사가 이 게시글에 답변을 작성했는지 확인
        boolean exists = commentMapper.existsByBoardNoAndUserNo(dto.getBoardNo(), dto.getUserNo());
        if (exists) {
            throw new IllegalStateException("이미 해당 게시글에 답변을 작성하셨습니다.");
        }

        // 중복이 아니면 등록 진행
        commentMapper.insertComment(dto);
    }

    @Override
    public List<CommentResponse> getCommentsByBoardNo(Long boardNo) {
        return commentMapper.findByBoardNo(boardNo);
    }

    @Override
    public Page<MyCommentResponseDto> getMyComments(Long userNo, int page, int size) {
        int offset = (page - 1) * size;
        List<MyCommentResponseDto> list = commentMapper.findMyComments(userNo, offset, size);
        int total = commentMapper.countMyComments(userNo);

        return new PageImpl<>(list, PageRequest.of(page - 1, size), total);
    }

    @Override
    public CommentDetailDto findById(Long commentId, Long userNo) {
        CommentDetailDto dto = commentMapper.findById(commentId);
        if (!dto.getUserNo().equals(userNo)) {
            throw new AccessDeniedException("본인의 답변만 수정할 수 있습니다.");
        }
        return dto;
    }

    @Override
    public void updateComment(Long commentId, CommentUpdateDto dto) {
        commentMapper.updateComment(commentId, dto);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentMapper.deleteComment(commentId);
    }


}

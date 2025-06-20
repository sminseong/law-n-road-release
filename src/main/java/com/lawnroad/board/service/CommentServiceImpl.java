package com.lawnroad.board.service;

import com.lawnroad.board.dto.CommentRegisterDto;
import com.lawnroad.board.dto.CommentResponse;
import com.lawnroad.board.dto.MyCommentResponseDto;
import com.lawnroad.board.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    @Override
    public void registerComment(CommentRegisterDto dto) {
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

}

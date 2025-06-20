package com.lawnroad.board.service;

import com.lawnroad.board.dto.AnswerRegisterDto;
import com.lawnroad.board.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    @Override
    public void registerAnswer(AnswerRegisterDto dto) {
        commentMapper.insertAnswer(dto);
    }

}

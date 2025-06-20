package com.lawnroad.board.service;

import com.lawnroad.board.dto.AnswerRegisterDto;
import com.lawnroad.board.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

public interface CommentService {
    // 답변 등록
    void registerAnswer(AnswerRegisterDto dto);
}

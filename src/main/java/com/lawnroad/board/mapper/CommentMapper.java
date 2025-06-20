package com.lawnroad.board.mapper;

import com.lawnroad.board.dto.AnswerRegisterDto;
import com.lawnroad.board.dto.AnswerResponseDto;

import java.util.List;

public interface CommentMapper {
    void insertAnswer(AnswerRegisterDto dto);
//    List<AnswerResponseDto> selectAnswersByBoardNo(Long boardNo);
}

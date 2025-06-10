package com.lawnroad.board.service;

import com.lawnroad.board.dto.BoardListDto;
import com.lawnroad.board.mapper.BoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;

    public BoardServiceImpl(BoardMapper boardMapper) {
        this.boardMapper = boardMapper;
    }

    @Override
    public List<BoardListDto> getBoardList(int page, int size) {
        // page < 1 이 들어오면 1로 기본 처리
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        int offset = (page - 1) * size;
        return boardMapper.selectBoardList(offset, size);
    }
}

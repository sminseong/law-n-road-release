package com.lawnroad.board.service;

import com.lawnroad.board.dto.BoardCreateDto;
import com.lawnroad.board.dto.BoardDetailDto;
import com.lawnroad.board.dto.BoardListDto;
import com.lawnroad.board.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // 스프링 빈으로 등록되는 서비스 계층 클래스임을 명시
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    // BoardMapper 의존성 주입: DB와 직접 연결하는 MyBatis 매퍼
    private final BoardMapper boardMapper;

    //페이징된 게시글 목록을 조회하는 메서드
    @Override
    public List<BoardListDto> getBoardList(int page, int size) {
        // page가 1보다 작을 경우 기본값 1로 보정
        if (page < 1) page = 1;
        // size가 1보다 작을 경우 기본값 10으로 보정
        if (size < 1) size = 10;
        // MyBatis 쿼리에 전달할 offset 계산 (LIMIT offset, size)
        int offset = (page - 1) * size;
        // 매퍼를 통해 DB에서 게시글 목록 조회
        return boardMapper.selectBoardList(offset, size);
    }

    @Override
    public int getBoardCount() {
        // 매퍼를 통해 DB에서 게시글 개수(count)를 조회
        return boardMapper.getBoardCount();
    }

    //게시글 등록
    @Override
    public void register(BoardCreateDto dto) {
        boardMapper.insertBoard(dto);
    }

    @Override
    public BoardDetailDto getBoardDetail(Long id) {
        return boardMapper.selectBoardById(id);
    }
}

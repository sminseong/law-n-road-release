package com.lawnroad.board.service;

import com.lawnroad.board.dto.*;
import com.lawnroad.board.mapper.BoardMapper;
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
    private final BoardMapper boardMapper;

    //[변호사] 게시글에 새로운 답변 등록
    @Override
    public void registerComment(CommentRegisterDto dto) {
        // 댓글(답변 )중복 작성 방지: 이미 해당 변호사가 이 게시글에 답변을 작성했는지 확인
        boolean exists = commentMapper.existsByBoardNoAndUserNo(dto.getBoardNo(), dto.getUserNo());
        if (exists) {
            throw new IllegalStateException("이미 해당 게시글에 답변을 작성하셨습니다.");
        }
        // 중복이 아니면 등록 진행
        commentMapper.insertComment(dto);
    }

    //[공개] 특정 게시글에 달린 전체 답변 목록 조회
    @Override
    public List<CommentResponse> getCommentsByBoardNo(Long boardNo) {
        return commentMapper.findByBoardNo(boardNo);
    }

    //[변호사] 로그인한 변호사의 답변 목록을 페이지 단위로 조회
    @Override
    public Page<MyCommentResponseDto> getMyComments(Long userNo, int page, int size) {
        int offset = (page - 1) * size;
        List<MyCommentResponseDto> list = commentMapper.findMyComments(userNo, offset, size);
        int total = commentMapper.countMyComments(userNo);

        return new PageImpl<>(list, PageRequest.of(page - 1, size), total);
    }

    //[변호사] 특정 답변의 상세 내용 조회
    @Override
    public CommentDetailDto findById(Long commentId) {
        return commentMapper.findById(commentId);
    }

    @Override
    public void updateComment(Long commentId, CommentUpdateDto dto) {
        commentMapper.updateComment(commentId, dto);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentMapper.deleteComment(commentId);
    }

    //[공개] 게시글에 달린 답변 목록을 조회
    @Override
    public List<BoardCommentResponseDto> findBoardCommentsByBoardId(Long boardId) {
        return commentMapper.findBoardCommentsByBoardId(boardId);
    }

    //[사용자] 게시글 작성자가 특정 답변을 채택
    @Override
    public void selectAnswer(CommentSelectDto dto) {
        System.out.println(dto);

        // 1. 게시글 유효성 + 작성자 본인 확인
        BoardDetailDto board = boardMapper.findById(dto.getBoardNo());
        System.out.println(board);

        if (board == null) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }
        // 작성자 본인 확인
        if (!board.getUserNo().equals(dto.getUserNo())) {
            throw new AccessDeniedException("본인만 답변을 채택할 수 있습니다.");
        }
        // 2. 이미 채택된 답변이 있는지 확인
        boolean alreadySelected = commentMapper.existsSelectedAnswer(dto.getBoardNo());
        if (alreadySelected) {
            throw new IllegalStateException("이미 채택된 답변이 존재합니다.");
        }
        // 3. 채택 처리
        commentMapper.selectComment(dto);
    }


}

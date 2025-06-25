package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.mapper.KeywordMapper;
import com.lawnroad.broadcast.live.model.KeywordVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService {

    private final KeywordMapper keywordMapper;

    @Override
    public void insertKeyword(KeywordVo keywordVo) {
        keywordMapper.insertKeyword(keywordVo);
    }

    @Override
    public void registerKeywordAlert(Long userNo, String keyword) {
        boolean exists = keywordMapper.existsByUserNoAndKeyword(userNo, keyword);
        if (exists) {
            throw new IllegalArgumentException("이미 완료된 알림신청입니다.");
        }

        int result = keywordMapper.insertKeywordAlert(userNo, keyword);
        if (result == 0) {
            throw new RuntimeException("알림 신청에 실패했습니다.");
        }
    }
}

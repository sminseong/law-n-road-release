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
}

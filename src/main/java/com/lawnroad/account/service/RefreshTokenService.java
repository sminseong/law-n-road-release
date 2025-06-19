package com.lawnroad.account.service;

import com.lawnroad.account.mapper.RefreshMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshMapper refreshMapper;

    public void deleteByUserNo(Long userNo) {
        refreshMapper.deleteByUserNo(userNo);
    }

    public void save(Long userNo, String token) {
        refreshMapper.save(userNo, token);
    }



}

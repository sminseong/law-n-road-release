package com.lawnroad.broadcast.chat.service;

import com.lawnroad.broadcast.chat.dto.AutoReplyDTO;
import com.lawnroad.broadcast.chat.mapper.AutoReplyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutoReplyServiceImpl implements AutoReplyService {

    @Autowired
    private final AutoReplyMapper autoReplyMapper;

    @Override
    public void insertAutoReply(AutoReplyDTO autoReplyDTO) {
        autoReplyMapper.insertAutoReply(autoReplyDTO);
    }

    @Override
    public List<AutoReplyDTO> findByAutoReply(int scheduleNo) {
        return autoReplyMapper.findByAutoReply(scheduleNo);
    }
}

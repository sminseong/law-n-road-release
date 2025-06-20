package com.lawnroad.broadcast.chat.service;

import com.lawnroad.broadcast.chat.dto.ChatReportDTO;
import com.lawnroad.broadcast.chat.mapper.ChatReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatReportServiceImpl implements ChatReportService{

    @Autowired
    private ChatReportMapper chatReportMapper;

    @Override
    public void saveChatReport(ChatReportDTO dto) {
        // nickname이 null이거나 공백이면 no를 nickname에 세팅
        if (dto.getNickname() == null || dto.getNickname().trim().isEmpty()) {
            dto.setNickname(String.valueOf(dto.getUserNo()));
        }
        chatReportMapper.insertChatReport(dto);
    }

}

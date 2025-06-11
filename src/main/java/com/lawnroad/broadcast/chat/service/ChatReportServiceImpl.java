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
        chatReportMapper.insertChatReport(dto);
    }
}

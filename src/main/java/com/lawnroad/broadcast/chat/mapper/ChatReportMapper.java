package com.lawnroad.broadcast.chat.mapper;

import com.lawnroad.broadcast.chat.dto.ChatReportDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatReportMapper {
    void insertChatReport(ChatReportDTO report);
}

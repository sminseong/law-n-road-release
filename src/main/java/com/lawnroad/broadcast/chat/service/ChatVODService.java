package com.lawnroad.broadcast.chat.service;

import com.lawnroad.broadcast.chat.model.ChatVO;

import java.util.List;

public interface ChatVODService {
    List<ChatVO> getChatsByBroadcastNo(Long broadcastNo);
}

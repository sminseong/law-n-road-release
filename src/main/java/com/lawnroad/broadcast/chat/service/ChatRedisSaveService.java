package com.lawnroad.broadcast.chat.service;

import com.lawnroad.broadcast.chat.dto.ChatDTO;

import java.util.List;

public interface ChatRedisSaveService {
    void saveChatMessage(ChatDTO chatDTO);
    List<ChatDTO> findUncheckedMessages();
    void updateChatMessage(ChatDTO chatDTO);
}

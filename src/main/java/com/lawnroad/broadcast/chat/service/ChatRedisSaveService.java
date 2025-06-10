package com.lawnroad.broadcast.chat.service;

import com.lawnroad.broadcast.chat.dto.ChatDTO;

public interface ChatRedisSaveService {
    void saveChatMessage(ChatDTO chatDTO);
}

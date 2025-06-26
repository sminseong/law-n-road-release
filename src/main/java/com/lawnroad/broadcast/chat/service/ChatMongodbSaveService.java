package com.lawnroad.broadcast.chat.service;

import com.lawnroad.broadcast.chat.dto.ChatDTO;

public interface ChatMongodbSaveService {
    void saveChatMessage(ChatDTO chatDTO);
}

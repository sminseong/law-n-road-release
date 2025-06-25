package com.lawnroad.broadcast.chat.service;

import com.lawnroad.broadcast.chat.dto.ChatDTO;

public interface ChatMongodbSaveService {
    void backupRedisToMongo();
    void saveChatMessage(ChatDTO chatDTO);
}

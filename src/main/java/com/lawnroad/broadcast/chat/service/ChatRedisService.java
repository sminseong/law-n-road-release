package com.lawnroad.broadcast.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lawnroad.broadcast.chat.dto.ChatDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRedisService {

    private final StringRedisTemplate redisTemplate;

    public void saveChatMessage(ChatDTO chatDTO) {
        String key = "chat:" + chatDTO.getBroadcastNo();
        String json = toJson(chatDTO); // Jackson 등으로 직렬화
        redisTemplate.opsForList().rightPush(key, json);
    }

    private String toJson(ChatDTO dto) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 실패", e);
        }
    }

}

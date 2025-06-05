package com.lawnroad.broadcast.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lawnroad.broadcast.chat.dto.ChatDTO;
import com.lawnroad.broadcast.chat.dto.ChatDocument;
import com.lawnroad.broadcast.chat.repository.MongoChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatBackupService {

    private final StringRedisTemplate redisTemplate;
    private final MongoChatRepository mongoChatRepository; // Mongo용 Repository

    @Scheduled(fixedDelay = 10000) // 10초마다 백업
    public void backupRedisToMongo() {
        Set<String> keys = redisTemplate.keys("chat:*");
        if (keys != null) {
            for (String key : keys) {
                List<String> jsonList = redisTemplate.opsForList().range(key, 0, -1);
                if (jsonList != null && !jsonList.isEmpty()) {
                    List<ChatDocument> docs = jsonList.stream()
                            .map(json -> {
                                ChatDTO dto = fromJson(json, ChatDTO.class);
                                return ChatDocument.builder()
                                        .no(dto.getNo())
                                        .userNo(dto.getUserNo())
                                        .broadcastNo(dto.getBroadcastNo())
                                        .nickname(dto.getNickname())
                                        .message(dto.getMessage())
                                        .reportCount(dto.getReportCount())
                                        .createdAt(dto.getCreatedAt())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    mongoChatRepository.saveAll(docs);
                    redisTemplate.delete(key); // 저장 후 Redis 삭제
                }
            }
        }
    }


    private <T> T fromJson(String json, Class<T> type) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // 🧩 LocalDateTime 역직렬화 지원
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: 날짜 포맷을 배열 대신 ISO로
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 역변환 실패", e);
        }
    }
}

package com.lawnroad.broadcast.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lawnroad.broadcast.chat.dto.ChatDTO;
import com.lawnroad.broadcast.chat.model.ChatVO;
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
public class ChatMongodbSaveServiceImpl implements ChatMongodbSaveService {

    private final StringRedisTemplate redisTemplate;
    private final MongoChatRepository mongoChatRepository; // Mongo용 Repository

    @Override
    @Scheduled(fixedDelay = 10000) // 10초마다 백업
    public void backupRedisToMongo() {
        Set<String> keys = redisTemplate.keys("chat:*");
        for (String key : keys) {
            List<String> jsonList = redisTemplate.opsForList().range(key, 0, -1);
            if (jsonList != null && !jsonList.isEmpty()) {
                List<ChatVO> docs = jsonList.stream()
                        .map(json -> {
                            ChatDTO dto = fromJson(json, ChatDTO.class);
                            return ChatVO.builder()
                                    .no(dto.getNo())
                                    .userNo(dto.getUserNo())
                                    .broadcastNo(dto.getBroadcastNo())
                                    .nickname(dto.getNickname())
                                    .message(dto.getMessage())
                                    .reportStatus(dto.getReportStatus())
                                    .createdAt(dto.getCreatedAt())
                                    .build();
                        })
                        .collect(Collectors.toList());

                mongoChatRepository.saveAll(docs);
                redisTemplate.delete(key); // 저장 후 Redis 삭제
            }
        }
    }
    // Redis 장애 시 즉시 저장 (단일 메시지)
    @Override
    public void saveChatMessage(ChatDTO chatDTO) {
        ChatVO doc = ChatVO.builder()
                .no(chatDTO.getNo())
                .userNo(chatDTO.getUserNo())
                .broadcastNo(chatDTO.getBroadcastNo())
                .nickname(chatDTO.getNickname())
                .message(chatDTO.getMessage())
                .reportStatus(chatDTO.getReportStatus())
                .createdAt(chatDTO.getCreatedAt())
                .build();
        mongoChatRepository.save(doc);
    }


    private <T> T fromJson(String json, Class<T> type) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); //  LocalDateTime 역직렬화 지원
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: 날짜 포맷을 배열 대신 ISO로
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 역변환 실패", e);
        }
    }
}

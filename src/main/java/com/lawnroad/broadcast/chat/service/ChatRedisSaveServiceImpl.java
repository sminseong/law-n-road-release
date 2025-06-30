package com.lawnroad.broadcast.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lawnroad.broadcast.chat.dto.ChatDTO;
import com.lawnroad.broadcast.live.mapper.VodMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatRedisSaveServiceImpl implements ChatRedisSaveService {

    private final StringRedisTemplate redisTemplate;
    private final VodMapper vodMapper;

    @Override
    public void saveChatMessage(ChatDTO chatDTO) {
        if (chatDTO.getNickname() == null || chatDTO.getNickname().trim().isEmpty()) {
            String userName = vodMapper.selectNameByUserNo(chatDTO.getUserNo());
            chatDTO.setNickname(userName);
        }
        if (chatDTO.getNo() == null) {
            String seqKey = "chat_seq:" + chatDTO.getBroadcastNo(); // <-- 수정!
            Long nextNo = redisTemplate.opsForValue().increment(seqKey);
            chatDTO.setNo(nextNo);
        }
        String key = "chat:" + chatDTO.getBroadcastNo(); // 메시지 저장용 List
        String json = toJson(chatDTO);
        redisTemplate.opsForList().rightPush(key, json);
    }


    // 2. 미검사 메시지 조회 (모든 채팅방 최근 100개씩)
    @Override
    public List<ChatDTO> findUncheckedMessages() {
        List<ChatDTO> uncheckedList = new ArrayList<>();
        Set<String> keys = redisTemplate.keys("chat:*");
        if (keys == null) return uncheckedList;

        for (String key : keys) {
            List<String> jsonList = redisTemplate.opsForList().range(key, -100, -1); // 최근 100개
            if (jsonList == null) continue;
            for (String json : jsonList) {
                ChatDTO chat = fromJson(json, ChatDTO.class);
                if (chat != null && !chat.isChecked() && !chat.isBlind()) {
                    uncheckedList.add(chat);
                }
            }
        }
        return uncheckedList;
    }

    // 3. 메시지 덮어쓰기 (채팅방 내 메시지 번호로 찾아서 set)
    @Override
    public void updateChatMessage(ChatDTO chatDTO) {
        String key = "chat:" + chatDTO.getBroadcastNo();
        List<String> allChats = redisTemplate.opsForList().range(key, 0, -1);
        if (allChats == null) return;

        for (int i = 0; i < allChats.size(); i++) {
            ChatDTO savedChat = fromJson(allChats.get(i), ChatDTO.class);
            if (savedChat != null && savedChat.getNo().equals(chatDTO.getNo())) {
                String newJson = toJson(chatDTO);
                redisTemplate.opsForList().set(key, i, newJson);
                break;
            }
        }
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
    private <T> T fromJson(String json, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

}

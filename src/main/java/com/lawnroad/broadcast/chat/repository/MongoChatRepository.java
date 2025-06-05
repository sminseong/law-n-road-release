package com.lawnroad.broadcast.chat.repository;

import com.lawnroad.broadcast.chat.dto.ChatDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoChatRepository extends MongoRepository<ChatDocument, String> {
    // 추가 쿼리 메서드 정의 가능
}

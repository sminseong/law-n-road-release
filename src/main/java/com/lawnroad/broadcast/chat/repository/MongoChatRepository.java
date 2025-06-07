package com.lawnroad.broadcast.chat.repository;

import com.lawnroad.broadcast.chat.dto.ChatDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoChatRepository extends MongoRepository<ChatDocument, String> {
}

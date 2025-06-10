package com.lawnroad.broadcast.chat.repository;

import com.lawnroad.broadcast.chat.model.ChatVO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoChatRepository extends MongoRepository<ChatVO, String> {
    List<ChatVO> findByBroadcastNoOrderByCreatedAtAsc(Long broadcastNo);

}

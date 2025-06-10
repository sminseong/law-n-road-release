package com.lawnroad.broadcast.chat.service;

import com.lawnroad.broadcast.chat.model.ChatVO;
import com.lawnroad.broadcast.chat.repository.MongoChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatVODServiceImpl implements ChatVODService {

    private final MongoChatRepository mongoChatRepository;


    @Override
    public List<ChatVO> getChatsByBroadcastNo(Long broadcastNo) {
        return mongoChatRepository.findByBroadcastNoOrderByCreatedAtAsc(broadcastNo);
    }
}

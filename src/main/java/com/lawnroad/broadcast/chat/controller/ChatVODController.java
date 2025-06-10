package com.lawnroad.broadcast.chat.controller;

import com.lawnroad.broadcast.chat.model.ChatVO;
import com.lawnroad.broadcast.chat.service.ChatVODService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/broadcast")
@RequiredArgsConstructor
public class ChatVODController {

    private final ChatVODService chatVodService;

    // VOD 채팅 내역 (방송별 시간순)
    @GetMapping("/{broadcastNo}/chats")
    public List<ChatVO> getBroadcastChats(@PathVariable Long broadcastNo) {
        return chatVodService.getChatsByBroadcastNo(broadcastNo);
    }
}

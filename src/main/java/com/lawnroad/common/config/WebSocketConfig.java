package com.lawnroad.common.config;

import com.lawnroad.broadcast.chat.controller.ChatTextWebSocketHandler;
import com.lawnroad.broadcast.chat.service.ChatRedisService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatRedisService chatRedisService;

    public WebSocketConfig(ChatRedisService chatRedisService) {
        this.chatRedisService = chatRedisService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Redis를 활용하는 WebSocket 핸들러 등록
        registry.addHandler(new ChatTextWebSocketHandler(chatRedisService), "/ws/chat")
                .setAllowedOriginPatterns("*");
    }
}

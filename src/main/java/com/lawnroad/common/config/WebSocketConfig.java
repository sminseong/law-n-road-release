package com.lawnroad.common.config;

import com.lawnroad.broadcast.chat.controller.ChatTextWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 순수 WebSocket(Text) 핸들러
        registry.addHandler(new ChatTextWebSocketHandler(), "/ws/chat")
                .setAllowedOriginPatterns("*");
    }
}

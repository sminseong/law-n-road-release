package com.lawnroad.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * "/ws" 엔드포인트로 SockJS/STOMP 연결을 허용
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // 개발 단계에서는 모든 Origin 허용
                .withSockJS();
    }

    /**
     * 브로커 설정
     *  - enableSimpleBroker("/topic") : "/topic/**" 경로를 SimpleBroker(메모리)로 사용
     *  - setApplicationDestinationPrefixes("/app") : "/app/**" 로 들어오는 메시지를 @MessageMapping 으로 라우팅
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}

//package com.lawnroad.common.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.*;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws-stomp")  // 클라이언트는 이 주소로 연결
//                .setAllowedOriginPatterns("*")
//                .withSockJS(); // SockJS fallback 지원
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableSimpleBroker("/topic"); // 메시지 브로커 주소
//        registry.setApplicationDestinationPrefixes("/app"); // 클라이언트 SEND prefix
//    }
//}

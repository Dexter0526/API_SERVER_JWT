//package com.api.dex.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//// 플랫폼 페이지 소켓 사용할 경우
//// 주석 해제 및 Gradle socket 추가
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry){
//        registry.enableSimpleBroker("/socket"); // SET Socket prefix
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/websocket").setAllowedOrigins("*").addInterceptors(new HttpSessionIdHandshakeInterceptor()); // Connect to Android
//        registry.addEndpoint("/websocket").setAllowedOrigins("*").withSockJS().setInterceptors(new HttpSessionIdHandshakeInterceptor()); // Connect to Web
//    }
//}
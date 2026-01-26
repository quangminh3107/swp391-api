// package com.devmam.taraacademyapi.config;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.messaging.simp.config.MessageBrokerRegistry;
// import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
// import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
// import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// @Configuration
// @EnableWebSocketMessageBroker
// public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//     @Override
//     public void configureMessageBroker(MessageBrokerRegistry registry) {
//         // Enable a simple memory-based message broker
//         registry.enableSimpleBroker("/topic");

//         // Prefix for messages from client
//         registry.setApplicationDestinationPrefixes("/app");
//     }

//     @Override
//     public void registerStompEndpoints(StompEndpointRegistry registry) {
//         // Register STOMP endpoint
//         registry.addEndpoint("/ws")
//                 .setAllowedOriginPatterns("*")
//                 .withSockJS();
//     }
// }
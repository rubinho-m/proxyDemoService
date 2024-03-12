package com.rubinho.vkproxy.config;

import com.rubinho.vkproxy.jwt.UserAuthProvider;
import com.rubinho.vkproxy.mappers.UserMapper;
import com.rubinho.vkproxy.services.AuditService;
import com.rubinho.vkproxy.services.UserService;
import com.rubinho.vkproxy.utils.MessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final AuditService auditService;
    private final UserAuthProvider userAuthProvider;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(wsHandler(), "/ws");
    }

    @Bean
    WebSocketHandler wsHandler() {
        return new MessageHandler(auditService, userAuthProvider, userService, userMapper);
    }

}
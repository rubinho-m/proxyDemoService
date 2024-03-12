package com.rubinho.vkproxy.config;

import com.rubinho.vkproxy.services.AuditService;
import com.rubinho.vkproxy.services.WebSocketService;
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
    private final WebSocketService webSocketService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(wsHandler(), "/ws");
    }

    @Bean
    WebSocketHandler wsHandler() {
        return new MessageHandler(auditService, webSocketService);
    }

}
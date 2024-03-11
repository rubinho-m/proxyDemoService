package com.rubinho.vkproxy.config;

import com.rubinho.vkproxy.utils.MessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(wsHandler(), "/ws");
    }

    @Bean
    WebSocketHandler wsHandler() {
        return new MessageHandler();
    }

}
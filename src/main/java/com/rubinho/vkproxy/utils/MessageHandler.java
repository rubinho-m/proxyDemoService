package com.rubinho.vkproxy.utils;


import com.rubinho.vkproxy.model.User;
import com.rubinho.vkproxy.services.AuditService;
import com.rubinho.vkproxy.services.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class MessageHandler implements WebSocketHandler {
    private final AuditService auditService;
    private final WebSocketService webSocketService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws ExecutionException, InterruptedException {
        String authorizationHeader = session.getHandshakeHeaders().getFirst("Authorization");

        if (authorizationHeader != null) {
            User user = webSocketService.getUserFromHeader(authorizationHeader);
            auditService.doAudit(user, true, "/ws", "CONNECT");
        }

        Sessions.clientSession = session;
        webSocketService.connectToEchoServer();
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String authorizationHeader = session.getHandshakeHeaders().getFirst("Authorization");

        if (authorizationHeader != null) {
            User user = webSocketService.getUserFromHeader(authorizationHeader);
            auditService.doAudit(user, true, "/ws", "MESSAGE");
        }

        Sessions.serverSession.sendMessage(message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String authorizationHeader = session.getHandshakeHeaders().getFirst("Authorization");

        if (authorizationHeader != null) {
            User user = webSocketService.getUserFromHeader(authorizationHeader);
            auditService.doAudit(user, true, "/ws", "DISCONNECT");
        }

        Sessions.serverSession.close();
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


}

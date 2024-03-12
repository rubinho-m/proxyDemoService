package com.rubinho.vkproxy.utils;

import com.rubinho.vkproxy.dto.UserDto;
import com.rubinho.vkproxy.jwt.UserAuthProvider;
import com.rubinho.vkproxy.mappers.UserMapper;
import com.rubinho.vkproxy.model.User;
import com.rubinho.vkproxy.services.AuditService;
import com.rubinho.vkproxy.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

@Component
@RequiredArgsConstructor
public class MessageHandler implements WebSocketHandler {
    private final AuditService auditService;
    private final UserAuthProvider userAuthProvider;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String authorizationHeader = session.getHandshakeHeaders().getFirst("Authorization");

        if (authorizationHeader != null) {
            User user = getUserFromHeader(authorizationHeader);
            auditService.doAudit(user, true, "/ws", "CONNECT");
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String data = (String) message.getPayload();
        String authorizationHeader = session.getHandshakeHeaders().getFirst("Authorization");

        if (authorizationHeader != null) {
            User user = getUserFromHeader(authorizationHeader);
            auditService.doAudit(user, true, "/ws", "MESSAGE");
        }

        session.sendMessage(new TextMessage(data));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String authorizationHeader = session.getHandshakeHeaders().getFirst("Authorization");

        if (authorizationHeader != null) {
            User user = getUserFromHeader(authorizationHeader);
            auditService.doAudit(user, true, "/ws", "DISCONNECT");
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private User getUserFromHeader(String authorizationHeader) {
        String email = userAuthProvider.getUsernameFromJwt(authorizationHeader.split(" ")[1]);
        System.out.println(email);
        return userMapper.dtoToUser(userService.findByEmail(email));
    }
}

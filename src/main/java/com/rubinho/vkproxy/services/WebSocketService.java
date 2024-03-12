package com.rubinho.vkproxy.services;

import com.rubinho.vkproxy.jwt.UserAuthProvider;
import com.rubinho.vkproxy.mappers.UserMapper;
import com.rubinho.vkproxy.model.User;
import com.rubinho.vkproxy.utils.EchoServerHandler;
import com.rubinho.vkproxy.utils.MessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final UserAuthProvider userAuthProvider;
    private final UserService userService;
    private final UserMapper userMapper;
    private final String URI = "wss://echo.websocket.org";


    public void connectToEchoServer() throws ExecutionException, InterruptedException {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        webSocketClient.execute(new EchoServerHandler(), URI).get();


    }

    public User getUserFromHeader(String authorizationHeader) {
        String email = userAuthProvider.getUsernameFromJwt(authorizationHeader.split(" ")[1]);
        return userMapper.dtoToUser(userService.findByEmail(email));
    }

//    public String wsRequest(String message) {
//        WebSocketClient client = new StandardWebSocketClient();
////        client.execute();
//        return "";
//    }


}


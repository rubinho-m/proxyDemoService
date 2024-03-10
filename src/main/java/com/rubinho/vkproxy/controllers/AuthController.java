package com.rubinho.vkproxy.controllers;

import com.rubinho.vkproxy.dto.CredentialsDto;
import com.rubinho.vkproxy.dto.SignUpDto;
import com.rubinho.vkproxy.dto.UserDto;
import com.rubinho.vkproxy.jwt.UserAuthProvider;
import com.rubinho.vkproxy.mappers.UserMapper;
import com.rubinho.vkproxy.services.AuditService;
import com.rubinho.vkproxy.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class AuthController {
    private final UserService userService;
    private final UserAuthProvider userAuthProvider;
    private final AuditService auditService;
    private final UserMapper userMapper;
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(SignUpDto signUpDto) {
        UserDto user = userService.register(signUpDto);
        user.setToken(userAuthProvider.createToken(user.getEmail()));
        auditService.doAudit(userMapper.dtoToUser(user), true, "/register", "POST");
        return new ResponseEntity<>(user, HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(CredentialsDto credentialsDto) {
        UserDto user = userService.login(credentialsDto);
        user.setToken(userAuthProvider.createToken(user.getEmail()));
        auditService.doAudit(userMapper.dtoToUser(user), true, "/login", "POST");
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }
}

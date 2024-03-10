package com.rubinho.vkproxy.controllers;

import com.rubinho.vkproxy.dto.CredentialsDto;
import com.rubinho.vkproxy.dto.SignUpDto;
import com.rubinho.vkproxy.dto.UserDto;
import com.rubinho.vkproxy.jwt.UserAuthProvider;
import com.rubinho.vkproxy.mappers.UserMapper;
import com.rubinho.vkproxy.services.AuditService;
import com.rubinho.vkproxy.services.MailService;
import com.rubinho.vkproxy.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class AuthController {
    private final UserService userService;
    private final UserAuthProvider userAuthProvider;
    private final AuditService auditService;
    private final MailService mailService;
    private final UserMapper userMapper;

    @PostMapping("/activate")
    private ResponseEntity<UserDto> activate(@RequestHeader("Authorization") String authorizationHeader,
                                       String code) {
        String email = userAuthProvider.getUsernameFromJwt(authorizationHeader.split(" ")[1]);
        UserDto userDto = userService.findByEmail(email);

        userDto = userService.activateUser(userMapper.dtoToUser(userDto), code);

        return new ResponseEntity<>(userDto, HttpStatus.ACCEPTED);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(SignUpDto signUpDto) {
        UserDto user = userService.register(signUpDto);
        user.setToken(userAuthProvider.createToken(user.getEmail()));

        auditService.doAudit(userMapper.dtoToUser(user), true, "/register", "POST");

        String code = userService.setCodeForUser(userMapper.dtoToUser(user));

        mailService.sendActivation(user.getEmail(), code);

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

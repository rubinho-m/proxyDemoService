package com.rubinho.vkproxy.controllers;

import com.rubinho.vkproxy.dto.CredentialsDto;
import com.rubinho.vkproxy.dto.SignUpDto;
import com.rubinho.vkproxy.dto.UserDto;
import com.rubinho.vkproxy.jwt.UserAuthProvider;
import com.rubinho.vkproxy.mappers.UserMapper;
import com.rubinho.vkproxy.model.Role;
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
    public ResponseEntity<UserDto> activate(@RequestHeader("Authorization") String authorizationHeader,
                                            String code) {
        String email = userAuthProvider.getUsernameFromJwt(authorizationHeader.split(" ")[1]);
        UserDto userDto = userService.findByEmail(email);

        userDto = userService.activateUser(userMapper.dtoToUser(userDto), code);

        auditService.doAudit(userMapper.dtoToUser(userDto), true, "/activate", "POST");

        return new ResponseEntity<>(userDto, HttpStatus.ACCEPTED);
    }

    @GetMapping("/password/permission")
    public ResponseEntity<String> passwordPermission(@RequestHeader("Authorization") String authorizationHeader) {
        String email = userAuthProvider.getUsernameFromJwt(authorizationHeader.split(" ")[1]);
        UserDto userDto = userService.findByEmail(email);

        String code = userService.setRestorePasswordCodeForUser(userMapper.dtoToUser(userDto));

        mailService.sendRestorePassword(email, code);

        auditService.doAudit(userMapper.dtoToUser(userDto), true, "/password/permission", "GET");

        return new ResponseEntity<>(code, HttpStatus.CREATED);
    }

    @PostMapping("/password/restore")
    public ResponseEntity<UserDto> passwordRestore(@RequestHeader("Authorization") String authorizationHeader,
                                                   String code,
                                                   String password) {
        String email = userAuthProvider.getUsernameFromJwt(authorizationHeader.split(" ")[1]);
        UserDto userDto = userService.findByEmail(email);

        userDto = userService.restorePassword(userMapper.dtoToUser(userDto), code, password);

        auditService.doAudit(userMapper.dtoToUser(userDto), true, "/password/restore", "POST");

        return new ResponseEntity<>(userDto, HttpStatus.ACCEPTED);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(SignUpDto signUpDto) {
        UserDto user = userService.register(signUpDto, Role.ROLE_UNVERIFIED_USER);
        user.setToken(userAuthProvider.createToken(user.getEmail()));

        auditService.doAudit(userMapper.dtoToUser(user), true, "/register", "POST");

        String code = userService.setActivationCodeForUser(userMapper.dtoToUser(user));

        mailService.sendActivation(user.getEmail(), code);

        return new ResponseEntity<>(user, HttpStatus.CREATED);

    }

    @PostMapping("/register/admin")
    public ResponseEntity<UserDto> registerAdmin(SignUpDto signUpDto, String securedWord) {
        userService.checkAdminPermissionsForRegistration(securedWord);

        UserDto user = userService.register(signUpDto, Role.ROLE_ADMIN);
        user.setToken(userAuthProvider.createToken(user.getEmail()));

        auditService.doAudit(userMapper.dtoToUser(user), true, "/registerAdmin", "POST");

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

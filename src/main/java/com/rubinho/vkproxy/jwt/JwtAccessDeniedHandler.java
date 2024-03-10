package com.rubinho.vkproxy.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubinho.vkproxy.dto.ErrorDto;
import com.rubinho.vkproxy.dto.UserDto;
import com.rubinho.vkproxy.mappers.UserMapper;
import com.rubinho.vkproxy.services.AuditService;
import com.rubinho.vkproxy.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final AuditService auditService;
    private final UserService userService;
    private final UserAuthProvider userAuthProvider;
    private final UserMapper userMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        String uri = request.getRequestURI();
        String method = request.getMethod();
        String authorizationHeader = request.getHeader("Authorization");
        String email = userAuthProvider.getUsernameFromJwt(authorizationHeader.split(" ")[1]);
        UserDto userDto = userService.findByEmail(email);

        auditService.doAudit(userMapper.dtoToUser(userDto), false, uri, method);

        OBJECT_MAPPER.writeValue(response.getOutputStream(), new ErrorDto("Forbidden"));
    }
}

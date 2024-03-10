package com.rubinho.vkproxy.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubinho.vkproxy.dto.ErrorDto;
import com.rubinho.vkproxy.services.AuditService;
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

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        String uri = request.getRequestURI();
        String method = request.getMethod();
        String remoteUser = request.getRemoteUser();

        auditService.doAudit(auditService.getUserfromRemoteUser(remoteUser), false, uri, method);

        OBJECT_MAPPER.writeValue(response.getOutputStream(), new ErrorDto("Forbidden"));
    }
}

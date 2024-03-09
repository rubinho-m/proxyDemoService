package com.rubinho.vkproxy.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubinho.vkproxy.services.AuditService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Order(1)
@RequiredArgsConstructor
public class LoggingFilter implements Filter {
    private final AuditService auditService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();
        String method = request.getMethod();
        String remoteUser = request.getRemoteUser();

        auditService.fileAudit(auditService.getMessage(remoteUser, true, uri, method));

        filterChain.doFilter(request, response);

    }
}

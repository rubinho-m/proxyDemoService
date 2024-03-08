package com.rubinho.vkproxy.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/")
public class ProxyController {
    @GetMapping("/test")
    public ResponseEntity<String> test(HttpServletRequest request) {
        System.out.println(request.getRequestURI());
        return ResponseEntity.ok("Hello, World!");
    }
}

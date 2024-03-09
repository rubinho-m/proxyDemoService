package com.rubinho.vkproxy.controllers;

import com.rubinho.vkproxy.services.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api")
public class ProxyController {
    private final ProxyService proxyService;

    @GetMapping(path = "**", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> proxyingGet(HttpServletRequest request) {
        String requestURI = proxyService.formatRequest(request.getRequestURI());

        return proxyService.proxyingGetRequest(requestURI);

    }

    @PostMapping(path = "**", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=application/json")
    public ResponseEntity<String> proxyingPost(HttpServletRequest request,
                                               @RequestParam Map<String, Object> lookupRequestObject) {
        String requestURI = proxyService.formatRequest(request.getRequestURI());
        return proxyService.proxyingPostRequest(requestURI, lookupRequestObject);
    }

    @PutMapping(path = "**", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=application/json")
    public ResponseEntity<String> proxyingPut(HttpServletRequest request,
                                              @RequestParam Map<String, Object> lookupRequestObject) {
        String requestURI = proxyService.formatRequest(request.getRequestURI());
        Long id = proxyService.getIdFromURI(requestURI);

        return proxyService.proxyingPutRequest(requestURI, lookupRequestObject, id);
    }

    @DeleteMapping(path = "**", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=application/json")
    public ResponseEntity<?> proxyingDelete(HttpServletRequest request) {
        String requestURI = proxyService.formatRequest(request.getRequestURI());
        Long id = proxyService.getIdFromURI(requestURI);
        proxyService.proxyingDeleteRequest(requestURI, id);
        return ResponseEntity.noContent().build();

    }


}

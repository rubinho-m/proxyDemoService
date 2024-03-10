package com.rubinho.vkproxy.controllers;

import com.rubinho.vkproxy.dto.UserDto;
import com.rubinho.vkproxy.jwt.UserAuthProvider;
import com.rubinho.vkproxy.mappers.UserMapper;
import com.rubinho.vkproxy.services.AuditService;
import com.rubinho.vkproxy.services.ProxyService;
import com.rubinho.vkproxy.services.UserService;
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
    private final UserService userService;
    private final AuditService auditService;
    private final UserAuthProvider userAuthProvider;
    private final UserMapper userMapper;

    @GetMapping(path = "**", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> proxyingGet(@RequestHeader("Authorization") String authorizationHeader,
                                              HttpServletRequest request) {

        String email = userAuthProvider.getUsernameFromJwt(authorizationHeader.split(" ")[1]);
        UserDto userDto = userService.findByEmail(email);

        String requestURI = proxyService.formatRequest(request.getRequestURI());
        ResponseEntity<String> response = proxyService.proxyingGetRequest(requestURI);

        auditService.doAudit(userMapper.dtoToUser(userDto), true, requestURI, "GET");

        return response;

    }

    @PostMapping(path = "**", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=application/json")
    public ResponseEntity<String> proxyingPost(HttpServletRequest request,
                                               @RequestParam Map<String, Object> lookupRequestObject,
                                               @RequestHeader("Authorization") String authorizationHeader) {
        String email = userAuthProvider.getUsernameFromJwt(authorizationHeader.split(" ")[1]);
        UserDto userDto = userService.findByEmail(email);

        String requestURI = proxyService.formatRequest(request.getRequestURI());
        ResponseEntity<String> response = proxyService.proxyingPostRequest(requestURI, lookupRequestObject);

        auditService.doAudit(userMapper.dtoToUser(userDto), true, requestURI, "POST");

        return response;
    }

    @PutMapping(path = "**", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=application/json")
    public ResponseEntity<String> proxyingPut(HttpServletRequest request,
                                              @RequestParam Map<String, Object> lookupRequestObject,
                                              @RequestHeader("Authorization") String authorizationHeader) {
        String email = userAuthProvider.getUsernameFromJwt(authorizationHeader.split(" ")[1]);
        UserDto userDto = userService.findByEmail(email);

        String requestURI = proxyService.formatRequest(request.getRequestURI());
        Long id = proxyService.getIdFromURI(requestURI);
        ResponseEntity<String> response = proxyService.proxyingPutRequest(requestURI, lookupRequestObject, id);

        auditService.doAudit(userMapper.dtoToUser(userDto), true, requestURI, "PUT");

        return response;
    }

    @DeleteMapping(path = "**", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=application/json")
    public ResponseEntity<?> proxyingDelete(HttpServletRequest request,
                                            @RequestHeader("Authorization") String authorizationHeader) {
        String email = userAuthProvider.getUsernameFromJwt(authorizationHeader.split(" ")[1]);
        UserDto userDto = userService.findByEmail(email);

        String requestURI = proxyService.formatRequest(request.getRequestURI());
        Long id = proxyService.getIdFromURI(requestURI);
        proxyService.proxyingDeleteRequest(requestURI, id);

        auditService.doAudit(userMapper.dtoToUser(userDto), true, requestURI, "DELETE");

        return ResponseEntity.noContent().build();

    }


}

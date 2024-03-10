package com.rubinho.vkproxy.controllers;

import com.rubinho.vkproxy.dto.UserDto;
import com.rubinho.vkproxy.mappers.UserMapper;
import com.rubinho.vkproxy.model.Role;
import com.rubinho.vkproxy.services.AuditService;
import com.rubinho.vkproxy.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminController {
    private final UserService userService;
    private final AuditService auditService;
    private final UserMapper userMapper;

    @GetMapping("/ban/{id}")
    public ResponseEntity<UserDto> banUser(@PathVariable Long id) {
        UserDto userDto = userService.changeRoleForUser(id, Role.ROLE_BANNED);
        auditService.doAudit(userMapper.dtoToUser(userDto), true, "/ban/" + id, "GET");
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/setup/{id}")
    public ResponseEntity<UserDto> setupRole(@PathVariable Long id, Role role) {
        UserDto userDto = userService.changeRoleForUser(id, role);
        auditService.doAudit(userMapper.dtoToUser(userDto), true, "/setup/" + id, "POST");
        return ResponseEntity.ok(userDto);
    }

}

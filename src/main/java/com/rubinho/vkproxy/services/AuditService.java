package com.rubinho.vkproxy.services;

import com.rubinho.vkproxy.model.Ledger;
import com.rubinho.vkproxy.model.Role;
import com.rubinho.vkproxy.model.User;
import com.rubinho.vkproxy.repositories.LedgerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.StringJoiner;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {
    private final LedgerRepository ledgerRepository;

    public Ledger doAudit(User user, boolean hasAccess, String uri, String method) {
        if (user == null) return null;

        String message = getMessage(user.getEmail(), user.getRole().name(), hasAccess, uri, method);

        fileAudit(message);

        return databaseAudit(user.getEmail(), user.getRole(), hasAccess, uri, method);
    }

    private void fileAudit(String message) {
        log.warn(message);
    }

    private Ledger databaseAudit(String username, Role role, boolean hasAccess, String uri, String method) {
        Ledger ledger = Ledger.builder()
                .username(username)
                .method(method)
                .logTime(LocalDateTime.now())
                .hasAccess(hasAccess)
                .uri(uri)
                .role(role)
                .build();

        return ledgerRepository.save(ledger);
    }


    private String getMessage(String username, String role, boolean hasAccess, String uri, String method) {
        StringJoiner logMessage = new StringJoiner("---");
        String access = hasAccess ? "yes" : "no";
        logMessage
                .add("USER: " + username)
                .add("ROLE: " + role)
                .add("HAS ACCESS: " + access)
                .add("URI: " + uri)
                .add("METHOD: " + method);

        return logMessage.toString();
    }


}

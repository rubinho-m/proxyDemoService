package com.rubinho.vkproxy.services;

import com.rubinho.vkproxy.model.Ledger;
import com.rubinho.vkproxy.model.Role;
import com.rubinho.vkproxy.repositories.LedgerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.StringJoiner;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {
    private final LedgerRepository ledgerRepository;

    public void doAudit(String user, boolean hasAccess, String uri, String method) {
        String message = getMessage(user, hasAccess, uri, method);

        fileAudit(message);
        databaseAudit(getEmailFromRemoteUser(user), getRoleFromRemoteUser(user), hasAccess, uri, method);
    }

    private void fileAudit(String message) {
        log.warn(message);
    }

    private void databaseAudit(String username, String role, boolean hasAccess, String uri, String method) {
        Ledger ledger = Ledger.builder()
                .username(username)
                .method(method)
                .hasAccess(hasAccess)
                .uri(uri)
                .role(Role.valueOf(role))
                .build();

        ledgerRepository.save(ledger);
    }

    private String getMessage(String user, boolean hasAccess, String uri, String method) {
        StringJoiner logMessage = new StringJoiner("---");
        String access = hasAccess ? "yes" : "no";
        logMessage
                .add("USER: " + getEmailFromRemoteUser(user))
                .add("ROLE: " + getRoleFromRemoteUser(user))
                .add("HAS ACCESS: " + access)
                .add("URI: " + uri)
                .add("METHOD: " + method);

        return logMessage.toString();
    }

    private String getEmailFromRemoteUser(String remoteUser) {
        remoteUser = remoteUser.replace("UserDto", "").replace("(", "").replace(")", "");
        String[] parts = remoteUser.split(",");
        return parts[1].replace("email=", "").strip();
    }

    private String getRoleFromRemoteUser(String remoteUser) {
        remoteUser = remoteUser.replace("UserDto", "").replace("(", "").replace(")", "");
        String[] parts = remoteUser.split(",");
        return parts[3].replace("role=", "").strip();
    }

}

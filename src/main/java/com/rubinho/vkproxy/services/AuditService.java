package com.rubinho.vkproxy.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.StringJoiner;


@Slf4j
@Service
public class AuditService {
    public void fileAudit(String message) {
        log.warn(message);
    }

    public void databaseAudit() {

    }

    public String getMessage(String user, boolean hasAccess, String uri, String method) {
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

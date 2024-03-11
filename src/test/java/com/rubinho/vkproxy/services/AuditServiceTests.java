package com.rubinho.vkproxy.services;

import com.rubinho.vkproxy.model.Ledger;
import com.rubinho.vkproxy.model.Role;
import com.rubinho.vkproxy.model.User;
import com.rubinho.vkproxy.repositories.LedgerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class AuditServiceTests {
    @Mock
    private LedgerRepository ledgerRepository;

    @InjectMocks
    private AuditService auditService;

    private Ledger getLedger() {
        return Ledger
                .builder()
                .id(1L)
                .logTime(LocalDateTime.now())
                .role(Role.ROLE_UNVERIFIED_USER)
                .uri("/test")
                .hasAccess(true)
                .username("test")
                .method("GET")
                .build();
    }

    private User getUser() {
        return User
                .builder()
                .id(1L)
                .email("test")
                .password("testHashPassword")
                .role(Role.ROLE_UNVERIFIED_USER)
                .build();
    }


    @Test
    public void audit_withValidData_savesLog() {
        Ledger ledger = getLedger();
        User user = getUser();

        Mockito.when(ledgerRepository.save(Mockito.any(Ledger.class))).thenReturn(ledger);


        Ledger savedLedger = auditService.doAudit(user, true, "/test", "GET");

        Assertions.assertThat(savedLedger).isNotNull();

    }
}

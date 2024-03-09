package com.rubinho.vkproxy.repositories;

import com.rubinho.vkproxy.model.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
}

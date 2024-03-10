package com.rubinho.vkproxy.repositories;

import com.rubinho.vkproxy.model.Restores;
import com.rubinho.vkproxy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestoresRepository extends JpaRepository<Restores, Long> {
    Optional<Restores> findByUser(User user);
}

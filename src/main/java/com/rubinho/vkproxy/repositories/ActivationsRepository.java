package com.rubinho.vkproxy.repositories;

import com.rubinho.vkproxy.model.Activations;
import com.rubinho.vkproxy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivationsRepository extends JpaRepository<Activations, Long> {
    Optional<Activations> findByUser(User user);
}

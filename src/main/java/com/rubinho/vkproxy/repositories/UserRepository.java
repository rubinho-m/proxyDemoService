package com.rubinho.vkproxy.repositories;

import com.rubinho.vkproxy.model.Role;
import com.rubinho.vkproxy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("""
            UPDATE User u SET u.role = :role WHERE u.id = :id""")
    void changeRole(@Param("id") Long id, @Param("role") Role role);
}
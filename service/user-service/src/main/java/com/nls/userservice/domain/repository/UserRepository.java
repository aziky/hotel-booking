package com.nls.userservice.domain.repository;

import com.nls.userservice.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Boolean existsUserByEmail(String email);

    Optional<User> findByEmail(String email);
    Long countByRole(String role);
    List<User> findByRole(String role);
}

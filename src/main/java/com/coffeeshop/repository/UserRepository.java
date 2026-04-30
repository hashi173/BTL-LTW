package com.coffeeshop.repository;

import com.coffeeshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, java.util.UUID> {
    Optional<User> findByUsername(String username);
}

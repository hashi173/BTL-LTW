package com.coffeeshop.service;

import com.coffeeshop.entity.User;
import com.coffeeshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User saveUser(@org.springframework.lang.NonNull User user) {
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(@org.springframework.lang.NonNull java.util.UUID id) {
        return userRepository.findById(id);
    }
}

package com.skyhorn.userservice.controller;


import com.skyhorn.userservice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        return List.of(
                new User(1L, "Alice", "alice@example.com"),
                new User(2L, "Bob", "bob@example.com")
        );
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        logger.info("Fetching user by ID: {}", id);
        return new User(id, "User" + id, "user" + id + "@example.com");
    }

    @GetMapping("/status")
    public Map<String, String> getStatus() {
        logger.info("Health check endpoint called");
        return Map.of("status", "UP", "service", "User Service");
    }
}

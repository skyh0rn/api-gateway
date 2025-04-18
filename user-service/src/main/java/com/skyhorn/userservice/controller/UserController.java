package com.skyhorn.userservice.controller;


import com.skyhorn.userservice.model.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @GetMapping("/special-users")
    public ResponseEntity<Map<String, String>> specialUsers(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String header = request.getHeader("X-Request-ID");
        logger.info("[User Service] Request received from IP: " + clientIp);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Special user access");
        response.put("requested_by_ip", clientIp);
        response.put("request ID", header);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/request")
    public Map<String, Object> debugRequest(HttpServletRequest request) {
        Map<String, Object> debugInfo = new LinkedHashMap<>();

        debugInfo.put("method", request.getMethod());
        debugInfo.put("requestURI", request.getRequestURI());
        debugInfo.put("queryString", request.getQueryString());
        debugInfo.put("protocol", request.getProtocol());
        debugInfo.put("scheme", request.getScheme());
        debugInfo.put("serverName", request.getServerName());
        debugInfo.put("serverPort", request.getServerPort());
        debugInfo.put("remoteAddr", request.getRemoteAddr());
        debugInfo.put("remoteHost", request.getRemoteHost());
        debugInfo.put("remotePort", request.getRemotePort());
        debugInfo.put("localAddr", request.getLocalAddr());
        debugInfo.put("localPort", request.getLocalPort());

        // Headers
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }
        debugInfo.put("headers", headers);

        // Parameters
        Map<String, String[]> params = request.getParameterMap();
        debugInfo.put("queryParams", params);

        // Cookies
        Map<String, String> cookies = new HashMap<>();
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                cookies.put(cookie.getName(), cookie.getValue());
            }
        }
        debugInfo.put("cookies", cookies);

        // Session info
        HttpSession session = request.getSession(false);
        if (session != null) {
            debugInfo.put("sessionId", session.getId());
            debugInfo.put("sessionCreationTime", session.getCreationTime());
            debugInfo.put("sessionLastAccessedTime", session.getLastAccessedTime());
        }

        return debugInfo;
    }

}

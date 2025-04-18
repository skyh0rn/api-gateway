package com.skyhorn.userservice.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RequestIdLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Extract request-id from header
        String requestId = httpRequest.getHeader("X-Request-ID");

        if (requestId != null && !requestId.isEmpty()) {
            MDC.put("request-id", requestId);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear(); // Always clear after response
        }
    }
}

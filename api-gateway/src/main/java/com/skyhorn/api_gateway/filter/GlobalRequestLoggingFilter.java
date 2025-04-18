package com.skyhorn.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class GlobalRequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(GlobalRequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        // Put request ID in MDC so it's accessible to all loggers in this thread
        MDC.put("request-id", requestId);

        // Log incoming request
        logger.info("🌐 Incoming request | {} {}", method, path);

        // Add header + mutate request
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-Request-ID", requestId)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .contextWrite(context -> {
                    MDC.put("request-id", requestId);
                    return context.put("request-id", requestId)
                            .put("startTime",startTime);
                })
//                .doOnSuccess(aVoid -> {
//                    logger.info("Request completed | {} {} {}", method, path, System.currentTimeMillis() - startTime);
//                })
                .doFinally(signalType -> {
                    logger.info("Request completed | {} {} execution time: {} seconds", method, path, System.currentTimeMillis() - startTime);
                    MDC.clear(); // Always clear to avoid memory leaks
                });
    }


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
     }
}

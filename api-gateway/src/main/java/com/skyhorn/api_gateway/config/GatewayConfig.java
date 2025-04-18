package com.skyhorn.api_gateway.config;

import com.skyhorn.api_gateway.filter.GlobalRequestLoggingFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // Java microservice routes (user-service)
                .route("user-service", r -> r
                        .path("/api/users", "/api/users/{id}")
                        .filters(f -> f
                                .addRequestHeader("X-Requested-by", "gateway")
                                .stripPrefix(1)
                        )
                        .uri("http://user-service:8081"))

                // Python microservice routes (product-service)
                .route("product-service-products", r -> r
                        .path("/api/products", "/api/products/{id}")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://product-service:8082"))

                .route("special-product-route", r -> r
                        .path("/api/special-products")
                        .and()
                        .method("GET")
                        .filters(f -> f
                                .addRequestHeader("X-Requested-By", "gateway")
                                .stripPrefix(1)
                        )
                        .uri("http://product-service:8082"))

                .route("request-demo", r -> r
                        .path("/api/request")
                        .uri("http://user-service:8081")
                )
                .build();
    }
}
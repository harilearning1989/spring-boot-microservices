package com.web.demo.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@Configuration
public class GatewayRouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // USER SERVICE
                .route("user-service", r -> r
                        .path("/users/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Gateway", "Gateway-Service")
                        )
                        .uri("lb://USER-SERVICE")
                )

                // CUSTOMER SERVICE
                .route("customer-service", r -> r
                        .path("/customers/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://CUSTOMER-SERVICE")
                )

                // PRODUCT SERVICE
                .route("product-service", r -> r
                        .path("/products/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://PRODUCT-SERVICE")
                )

                // EMPLOYEE SERVICE
                .route("employee-service", r -> r
                        .path("/employees/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://EMPLOYEE-SERVICE")
                )

                .build();
    }
}


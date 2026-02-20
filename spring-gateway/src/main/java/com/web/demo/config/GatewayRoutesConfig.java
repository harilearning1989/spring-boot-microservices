package com.web.demo.config;

import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder,
                               RedisRateLimiter rateLimiter) {

        return builder.routes()
                // ================= LOGIN SERVICE =================
                .route("login-service", r -> r
                        .path("/auth/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "Login-Service")
                                .removeRequestHeader("Cookie")
                                //.requestRateLimiter(c -> c.setRateLimiter(rateLimiter))
                                .circuitBreaker(config -> config
                                        .setName("loginServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/auth")
                                )
                        )
                        .uri("lb://LOGIN-SERVICE")
                )
                // ================= PRODUCT SERVICE =================
                /*.route("product-service", r -> r
                        .path("/products/**")
                        .uri("lb://PRODUCT-SERVICE")
                )*/

                .route("product-service", r -> r
                        .path("/products/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "Customer-Service")
                                .removeRequestHeader("Cookie")
                                //.requestRateLimiter(c -> c.setRateLimiter(rateLimiter))
                                .circuitBreaker(config -> config
                                        .setName("productServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/products")
                                )
                        )
                        .uri("lb://PRODUCT-SERVICE")
                )
                // ================= CUSTOMER SERVICE =================
                .route("customer-service", r -> r
                        .path("/customers/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "Customer-Service")
                                .removeRequestHeader("Cookie")
                                //.requestRateLimiter(c -> c.setRateLimiter(rateLimiter))
                                .circuitBreaker(c -> c
                                        .setName("CustomerCB")
                                        .setFallbackUri("forward:/fallback/customers"))
                        )
                        .uri("lb://CUSTOMER-SERVICE")
                )
                // ================= USER SERVICE =================
                .route("user-service", r -> r
                        .path("/users/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "User-Service")
                                .removeRequestHeader("Cookie")
                                .requestRateLimiter(c -> c.setRateLimiter(rateLimiter))
                                .circuitBreaker(c -> c
                                        .setName("userCB")
                                        .setFallbackUri("forward:/fallback/users"))
                        )
                        .metadata("authRequired", false)   // 🔥 changed to false
                        .metadata("roles", Set.of("ROLE_USER"))
                        .uri("lb://USER-SERVICE")
                )
                /*.route("user-service", r -> r
                        .path("/users/**")
                        .filters(f -> f
                                .filter(authFilter)
                                .addRequestHeader("X-Gateway", "User-Service")
                                .removeRequestHeader("Cookie")
                                .requestRateLimiter(c -> c.setRateLimiter(rateLimiter))
                                .circuitBreaker(c -> c
                                        .setName("userCB")
                                        .setFallbackUri("forward:/fallback/users"))
                        )
                        .metadata("authRequired", true)   // ✅ Authentication enabled
                        .metadata("roles", Set.of("ROLE_USER"))  // ✅ Only ROLE_USER allowed
                        .uri("lb://USER-SERVICE")
                )*/

                // ================= EMPLOYEE SERVICE =================
                .route("employee-service", r -> r
                        .path("/employee/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "Employee-Service")
                                .removeRequestHeader("Cookie")
                                .requestRateLimiter(c -> c.setRateLimiter(rateLimiter))
                                .circuitBreaker(c -> c
                                        .setName("employeeCB")
                                        .setFallbackUri("forward:/fallback/employee"))
                        )
                        .metadata("authRequired", true)
                        .metadata("roles", Set.of("ROLE_EMPLOYEE", "ROLE_ADMIN"))
                        .uri("lb://EMPLOYEE-SERVICE")
                )

                // ================= ADMIN SERVICE =================
                .route("admin-service", r -> r
                        .path("/admin/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "Admin-Service")
                                .circuitBreaker(c -> c
                                        .setName("adminCB")
                                        .setFallbackUri("forward:/fallback/admin"))
                        )
                        .metadata("authRequired", true)
                        .metadata("roles", Set.of("ROLE_ADMIN"))
                        .uri("lb://ADMIN-SERVICE")
                )

                // ================= PUBLIC SERVICE =================
                .route("public-service", r -> r
                        .path("/public/**")

                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "Public-Service"))
                        .metadata("authRequired", false) // 🔥 No Authentication
                        .uri("lb://PUBLIC-SERVICE")
                )
                .build();
    }

}



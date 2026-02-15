package com.web.demo.config;

import com.web.demo.filters.AuthenticationFilter;
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
                               AuthenticationFilter authFilter,
                               RedisRateLimiter rateLimiter) {

        return builder.routes()

                // ================= USER SERVICE =================
                .route("user-service", r -> r
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
                                .filter(authFilter)
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
                                .filter(authFilter)
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



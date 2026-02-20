package com.web.demo.filters;

import com.web.demo.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Component
public class AuthenticationFilter implements GatewayFilter {

    private final JwtUtil jwtUtil;

    public AuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        Route route = exchange.getAttribute(
                ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

        if (route == null) {
            return chain.filter(exchange);
        }

        Object authRequiredObj =
                route.getMetadata().getOrDefault("authRequired", Boolean.FALSE);

        boolean authRequired =
                authRequiredObj instanceof Boolean && (Boolean) authRequiredObj;

        if (!authRequired) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);

        Claims claims = jwtUtil.validateAndGetClaimsFromToken(token);
        if (claims == null) {
            return unauthorized(exchange);
        }

        // ✅ Role validation
        Set<String> requiredRoles =
                (Set<String>) route.getMetadata().get("roles");

        if (requiredRoles != null) {
            List<String> userRoles = claims.get("roles", List.class);

            boolean hasRole = userRoles.stream()
                    .anyMatch(requiredRoles::contains);

            if (!hasRole) {
                return forbidden(exchange);
            }
        }

        // ✅ Inject user info into headers
        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header("X-User", claims.getSubject())
                .build();

        return chain.filter(exchange.mutate()
                .request(mutatedRequest)
                .build());
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }
}



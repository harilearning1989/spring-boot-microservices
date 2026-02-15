package com.web.demo.filters;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
public class RoleAuthorizationFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             WebFilterChain chain) {

        Route route = exchange.getAttribute(
                ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

        if (route == null) {
            return chain.filter(exchange);
        }

        boolean authRequired =
                Boolean.TRUE.equals(route.getMetadata().get("authRequired"));

        if (!authRequired) {
            return chain.filter(exchange);
        }

        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new RuntimeException("Unauthorized")))
                .flatMap(context -> {

                    Authentication auth = context.getAuthentication();

                    if (auth == null || !auth.isAuthenticated()) {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }

                    Set<String> requiredRoles =
                            (Set<String>) route.getMetadata().get("roles");

                    if (requiredRoles != null) {
                        boolean hasRole = auth.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .anyMatch(requiredRoles::contains);

                        if (!hasRole) {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        }
                    }

                    return chain.filter(exchange);
                });
    }
}

/*
@Component
public class RoleAuthorizationFilter implements GatewayFilter {

    private final Set<String> allowedRoles;

    public RoleAuthorizationFilter(Set<String> allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String role = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Role");

        if (role == null || !allowedRoles.contains(role)) {
            exchange.getResponse()
                    .setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}*/




package com.web.demo.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        long start = System.currentTimeMillis();

        log.info("Incoming: {} {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI());

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long time = System.currentTimeMillis() - start;
            log.info("Completed in {} ms with status {}",
                    time,
                    exchange.getResponse().getStatusCode());
        }));
    }
}


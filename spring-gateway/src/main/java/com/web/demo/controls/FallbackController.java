package com.web.demo.controls;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @GetMapping("/fallback/users")
    public Mono<String> usersFallback() {
        return Mono.just("User Service Down");
    }

    @GetMapping("/fallback/admin")
    public Mono<String> adminFallback() {
        return Mono.just("Admin Service Down");
    }

    @GetMapping("/fallback/employee")
    public Mono<String> employeeFallback() {
        return Mono.just("Employee Service Down");
    }
}


package com.web.demo.controls;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/fallback/products")
    public ResponseEntity<String> productServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Product Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/fallback/customers")
    public ResponseEntity<String> customerServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Customer Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/fallback/auth")
    public ResponseEntity<String> loginServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Login Service is currently unavailable. Please try again later.");
    }
}


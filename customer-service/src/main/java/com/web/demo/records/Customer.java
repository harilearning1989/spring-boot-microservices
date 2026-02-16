package com.web.demo.records;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record Customer(
        Long id,
        String email,
        String first,
        String last,
        String company,
        @JsonProperty("created_at")
        Instant createdAt,
        String country
) {}

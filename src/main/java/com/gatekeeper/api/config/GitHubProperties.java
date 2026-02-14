package com.gatekeeper.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "gatekeeper.github")
@Validated
public record GitHubProperties(
        @NotBlank String acceptHeader,
        @NotBlank String userAgent,
        String token
) {
}

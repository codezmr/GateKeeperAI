package com.gatekeeper.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@ConfigurationProperties(prefix = "gatekeeper.watsonx")
@Validated
public record WatsonxProperties(
        @NotBlank String baseUrl,
        @NotBlank String projectId,
        @NotBlank String iamToken,
        @NotBlank String modelId,
        @NotBlank String decodingMethod,
        @Positive int maxNewTokens,
        @Positive int minNewTokens,
        @NotBlank String apiVersion
) {
}

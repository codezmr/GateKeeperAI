package com.gatekeeper.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatekeeper.api.config.WatsonxProperties;
import com.gatekeeper.api.exception.AiAnalysisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Client for IBM Watsonx API interactions
 */
@Component
public class WatsonxApiClient {

    private static final Logger log = LoggerFactory.getLogger(WatsonxApiClient.class);

    private final RestClient restClient;
    private final WatsonxProperties properties;
    private final ObjectMapper objectMapper;
    private final String systemPrompt;

    public WatsonxApiClient(
            RestClient restClient,
            WatsonxProperties properties,
            ObjectMapper objectMapper,
            @Value("classpath:prompt.txt") Resource promptResource
    ) {
        this.restClient = restClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.systemPrompt = loadPrompt(promptResource);
    }

    private String loadPrompt(Resource promptResource) {
        try {
            return new String(promptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to load AI prompt from resources", e);
            throw new AiAnalysisException("Could not load AI prompt from resources", e);
        }
    }

    /**
     * Analyzes code using IBM Watsonx AI
     *
     * @param codeDiff the code diff to analyze
     * @return the AI analysis result
     * @throws AiAnalysisException if the analysis fails
     */
    public String analyzeCode(String codeDiff) {
        String url = buildApiUrl();
        log.info("Sending code to IBM Watsonx for analysis...");

        var requestBody = buildRequestBody(codeDiff);

        try {
            String rawJson = restClient.post()
                    .uri(url)
                    .header("Authorization", "Bearer " + properties.iamToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return parseResponse(rawJson);

        } catch (RestClientException e) {
            log.error("Failed to analyze code with Watsonx", e);
            throw new AiAnalysisException("Failed to analyze code: " + e.getMessage(), e);
        }
    }

    private String buildApiUrl() {
        return properties.baseUrl() + "/ml/v1/text/generation?version=" + properties.apiVersion();
    }

    private Map<String, Object> buildRequestBody(String codeDiff) {
        return Map.of(
                "project_id", properties.projectId(),
                "model_id", properties.modelId(),
                "input", systemPrompt + "\n" + codeDiff,
                "parameters", Map.of(
                        "decoding_method", properties.decodingMethod(),
                        "max_new_tokens", properties.maxNewTokens(),
                        "min_new_tokens", properties.minNewTokens()
                )
        );
    }

    private String parseResponse(String rawJson) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode results = root.path("results");

            if (results.isEmpty() || results.get(0) == null) {
                throw new AiAnalysisException("Invalid response from Watsonx: No results found");
            }

            return results.get(0).path("generated_text").asText();

        } catch (IOException e) {
            log.error("Failed to parse Watsonx response", e);
            throw new AiAnalysisException("Failed to parse AI response: " + e.getMessage(), e);
        }
    }
}

package com.gatekeeper.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Map;

@Service
public class ManualWatsonxService {

    @Value("${spring.ai.watsonx.ai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.watsonx.ai.project-id}")
    private String projectId;

    @Value("${spring.ai.watsonx.ai.iam-token}")
    private String iamToken;

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper(); // For parsing JSON

    public String analyzeCode(String codeDiff) {
        // Use the LEGACY endpoint because it is stable
        String url = baseUrl + "/ml/v1/text/generation?version=2023-05-29";

        System.out.println("⚡ GATEKEEPER: Sending code to IBM Granite 3.0...");

        var requestBody = Map.of(
                "project_id", projectId,
                "model_id", "ibm/granite-3-8b-instruct", // The working model
                "input", """
                     You are a strict Security Architect. 
                     Review the following Java code for:
                     1. Hardcoded Secrets
                     2. PII Logging (passwords, tokens)
                     3. SQL Injection
                     
                     If vulnerabilities are found, list them clearly with Severity levels.
                     Then provide the FIXED code block.
                     
                     CODE:
                     """ + codeDiff,
                "parameters", Map.of(
                        "decoding_method", "greedy",
                        "max_new_tokens", 500,
                        "min_new_tokens", 1
                )
        );

        try {
            String rawJson = restClient.post()
                    .uri(url)
                    .header("Authorization", "Bearer " + iamToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            // --- CLEANING THE OUTPUT ---
            // We extract only the "generated_text" field
            JsonNode root = objectMapper.readTree(rawJson);
            String cleanReport = root.path("results").get(0).path("generated_text").asText();

            return "✅ ANALYSIS COMPLETE:\n" + cleanReport;

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Analysis Failed: " + e.getMessage();
        }
    }
}
package com.gatekeeper.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class ManualWatsonxService {

    @Value("${spring.ai.watsonx.ai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.watsonx.ai.project-id}")
    private String projectId;

    @Value("${spring.ai.watsonx.ai.iam-token}")
    private String iamToken;

    // Load the prompt.txt file from resources
    @Value("classpath:prompt.txt")
    private Resource promptResource;

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String analyzeCode(String codeDiff) {
        String url = baseUrl + "/ml/v1/text/generation?version=2023-05-29";

        System.out.println("⚡ GATEKEEPER: Sending code to IBM Granite 3.0...");

        // Read the System Prompt from the file
        String systemPrompt;
        try {
            systemPrompt = new String(promptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "❌ Error: Could not load AI Prompt from resources.";
        }

        var requestBody = Map.of(
                "project_id", projectId,
                "model_id", "ibm/granite-3-8b-instruct",
                // Combine the File Prompt + The Code from GitHub
                "input", systemPrompt + "\n" + codeDiff,
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

            JsonNode root = objectMapper.readTree(rawJson);
            String cleanReport = root.path("results").get(0).path("generated_text").asText();

            return "✅ ANALYSIS COMPLETE:\n" + cleanReport;

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Analysis Failed: " + e.getMessage();
        }
    }
}
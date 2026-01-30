package com.gatekeeper.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Map;
import java.util.List;

@Service
public class ManualWatsonxService {

    @Value("${spring.ai.watsonx.ai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.watsonx.ai.project-id}")
    private String projectId;

    @Value("${spring.ai.watsonx.ai.iam-token}")
    private String iamToken;

    private final RestClient restClient = RestClient.create();

    public String analyzeCode(String codeDiff) {
        String url = baseUrl + "/ml/v1/text/generation?version=2023-05-29";

        System.out.println("⚡ MANUAL OVERRIDE: Sending request to " + url);

        // Construct the exact JSON payload that worked in your Curl
        var requestBody = Map.of(
                "project_id", projectId,
                "model_id", "ibm/granite-3-8b-instruct",
                "input", """
                     You are a Security Architect. Review this code for hardcoded secrets, PII logging, or SQL Injection.
                     If safe, say 'SAFE'. If not, list the vulnerabilities.
                     
                     CODE TO REVIEW:
                     """ + codeDiff,
                "parameters", Map.of(
                        "decoding_method", "greedy",
                        "max_new_tokens", 500,
                        "min_new_tokens", 1
                )
        );

        try {
            // Send the POST request manually
            String response = restClient.post()
                    .uri(url)
                    .header("Authorization", "Bearer " + iamToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return "✅ RAW AI RESPONSE:\n" + response;

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Manual Call Failed: " + e.getMessage();
        }
    }
}
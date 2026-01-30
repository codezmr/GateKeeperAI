package com.gatekeeper.api.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Service
public class GitHubClient {

    private final RestClient restClient = RestClient.builder().build();

    public String fetchDiff(String apiUrl) {
        try {
            // Pretend to be a real browser to bypass 404 blocks
            // Use the "Accept" header to get the diff text
            ResponseEntity<String> response = restClient.get()
                    .uri(URI.create(apiUrl))
                    .header("Accept", "application/vnd.github.v3.diff")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .retrieve()
                    .toEntity(String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            // We return null so the Service knows it failed
            return null;
        }
        return null;
    }
}
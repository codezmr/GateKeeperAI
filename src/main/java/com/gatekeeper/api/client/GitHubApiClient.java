package com.gatekeeper.api.client;

import com.gatekeeper.api.config.GitHubProperties;
import com.gatekeeper.api.exception.GitHubApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;

/**
 * Client for GitHub API interactions
 */
@Component
public class GitHubApiClient {

    private static final Logger log = LoggerFactory.getLogger(GitHubApiClient.class);

    private final RestClient restClient;
    private final GitHubProperties properties;

    public GitHubApiClient(RestClient restClient, GitHubProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    /**
     * Fetches the diff content from a GitHub pull request URL
     *
     * @param apiUrl the GitHub API URL for the pull request
     * @return the diff content as a string
     * @throws GitHubApiException if the diff cannot be fetched
     */
    public String fetchDiff(String apiUrl) {
        log.info("Fetching diff from GitHub: {}", apiUrl);

        try {
            var requestSpec = restClient.get()
                    .uri(URI.create(apiUrl))
                    .header("Accept", properties.acceptHeader())
                    .header("User-Agent", properties.userAgent());

            // Add authorization token if configured
            if (properties.token() != null && !properties.token().isBlank()) {
                requestSpec.header("Authorization", "Bearer " + properties.token());
            }

            ResponseEntity<String> response = requestSpec
                    .retrieve()
                    .toEntity(String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully fetched diff ({} characters)", response.getBody().length());
                return response.getBody();
            }

            throw new GitHubApiException("Failed to fetch diff: Received status " + response.getStatusCode());

        } catch (RestClientException e) {
            log.error("Failed to fetch diff from GitHub", e);
            throw new GitHubApiException("Failed to fetch diff from GitHub: " + e.getMessage(), e);
        }
    }
}

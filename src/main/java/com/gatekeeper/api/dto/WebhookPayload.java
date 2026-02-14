package com.gatekeeper.api.dto;

import java.util.Map;

/**
 * DTO for GitHub webhook payload
 */
public record WebhookPayload(
        String action,
        Map<String, Object> pullRequest,
        Map<String, Object> repository
) {
    public static WebhookPayload fromRawPayload(Map<String, Object> raw) {
        String action = (String) raw.get("action");
        @SuppressWarnings("unchecked")
        Map<String, Object> pullRequest = (Map<String, Object>) raw.get("pull_request");
        @SuppressWarnings("unchecked")
        Map<String, Object> repository = (Map<String, Object>) raw.get("repository");
        return new WebhookPayload(action, pullRequest, repository);
    }

    public String getPrUrl() {
        return pullRequest != null ? (String) pullRequest.get("url") : null;
    }

    public String getPrNumber() {
        if (pullRequest == null) return "0";
        Object number = pullRequest.get("number");
        return number != null ? String.valueOf(number) : "0";
    }

    public String getRepositoryFullName() {
        if (repository != null && repository.get("full_name") != null) {
            return repository.get("full_name").toString();
        }
        return "GateKeeper/Unknown-Repo";
    }
}

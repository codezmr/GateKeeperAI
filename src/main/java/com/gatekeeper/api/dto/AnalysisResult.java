package com.gatekeeper.api.dto;

/**
 * DTO for AI analysis result
 */
public record AnalysisResult(
        String status,
        String analysis,
        String rawDiff
) {
    public static final String STATUS_SAFE = "SAFE";
    public static final String STATUS_VULNERABLE = "VULNERABLE";

    public boolean isVulnerable() {
        return STATUS_VULNERABLE.equals(status);
    }
}

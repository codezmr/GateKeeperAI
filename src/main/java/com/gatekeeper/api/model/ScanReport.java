package com.gatekeeper.api.model;

import java.time.LocalDateTime;

public record ScanReport(
        String id,
        String repository,
        String prNumber,
        String status, // "SAFE" or "VULNERABLE"
        String aiAnalysis,
        String rawDiff, // <--- NEW FIELD for the raw code changes
        LocalDateTime timestamp
) {}
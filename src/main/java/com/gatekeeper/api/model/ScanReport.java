package com.gatekeeper.api.model;

import java.time.LocalDateTime;

// A simple record to hold our scan data
public record ScanReport(
        String id,
        String repository,
        String prNumber,
        String status, // "SAFE" or "VULNERABLE"
        String aiAnalysis,
        LocalDateTime timestamp
) {}
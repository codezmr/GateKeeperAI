package com.gatekeeper.api.controller;

import com.gatekeeper.api.dto.ApiResponse;
import com.gatekeeper.api.model.ScanReport;
import com.gatekeeper.api.service.GateKeeperService;
import com.gatekeeper.api.service.SseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

/**
 * REST controller for webhook and API endpoints
 */
@RestController
@RequestMapping("/api")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);
    private static final String EVENT_PULL_REQUEST = "pull_request";
    private static final String ACTION_OPENED = "opened";
    private static final String ACTION_SYNCHRONIZE = "synchronize";

    private final GateKeeperService gateKeeperService;
    private final SseService sseService;

    public WebhookController(GateKeeperService gateKeeperService, SseService sseService) {
        this.gateKeeperService = gateKeeperService;
        this.sseService = sseService;
    }

    /**
     * Retrieves scan history
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ScanReport>>> getHistory() {
        List<ScanReport> history = gateKeeperService.getHistory();
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * SSE endpoint for live log streaming
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLogs() {
        return sseService.createEmitter();
    }

    /**
     * Handles GitHub webhook events
     */
    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<String>> handleGitHubEvent(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "X-GitHub-Event", defaultValue = "unknown") String eventType
    ) {
        log.info("Received webhook event: {}", eventType);
        sseService.broadcast("\n🔔 Webhook Received: " + eventType);

        if (!isEligiblePullRequestEvent(eventType, payload)) {
            log.debug("Ignoring event: {} with action: {}", eventType, payload.get("action"));
            return ResponseEntity.ok(ApiResponse.success("Event Ignored", null));
        }

        String result = gateKeeperService.processPullRequest(payload);
        return ResponseEntity.ok(ApiResponse.success("Analysis Complete", result));
    }

    private boolean isEligiblePullRequestEvent(String eventType, Map<String, Object> payload) {
        if (!EVENT_PULL_REQUEST.equals(eventType)) {
            return false;
        }

        String action = (String) payload.get("action");
        return ACTION_OPENED.equals(action) || ACTION_SYNCHRONIZE.equals(action);
    }
}
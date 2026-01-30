package com.gatekeeper.api.controller;

import com.gatekeeper.api.model.ScanReport;
import com.gatekeeper.api.service.GateKeeperService;
import com.gatekeeper.api.service.SseService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WebhookController {

    private final GateKeeperService gateKeeperService;
    private final SseService sseService;

    public WebhookController(GateKeeperService gateKeeperService, SseService sseService) {
        this.gateKeeperService = gateKeeperService;
        this.sseService = sseService;
    }

    @GetMapping("/history")
    public List<ScanReport> getHistory() {
        return gateKeeperService.getHistory();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLogs() {
        return sseService.createEmitter();
    }

    @PostMapping("/webhook")
    public String handleGitHubEvent(@RequestBody Map<String, Object> payload,
                                    @RequestHeader(value = "X-GitHub-Event", defaultValue = "unknown") String eventType) {

        sseService.broadcast("\n🔔 Webhook Received: " + eventType);

        if ("pull_request".equals(eventType)) {
            String action = (String) payload.get("action");
            if ("opened".equals(action) || "synchronize".equals(action)) {
                return gateKeeperService.processPullRequest(payload);
            }
        }
        return "Event Ignored";
    }
}
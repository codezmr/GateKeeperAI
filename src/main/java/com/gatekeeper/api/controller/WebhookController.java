package com.gatekeeper.api.controller;

import com.gatekeeper.api.service.ManualWatsonxService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    private final ManualWatsonxService manualService;
    private final RestClient restClient = RestClient.create(); // Client to fetch code from GitHub

    public WebhookController(ManualWatsonxService manualService) {
        this.manualService = manualService;
    }

    @PostMapping
    public String handleGitHubEvent(@RequestBody Map<String, Object> payload,
                                    @RequestHeader(value = "X-GitHub-Event", defaultValue = "unknown") String eventType) {

        System.out.println("\n🔔 Webhook Received: " + eventType);

        if ("pull_request".equals(eventType)) {
            String action = (String) payload.get("action");
            System.out.println("📝 Action: " + action);

            // Trigger on 'opened' or 'synchronize' (when they push new commits)
            if ("opened".equals(action) || "synchronize".equals(action)) {

                try {
                    // 1. Extract the Diff URL from the payload
                    Map<String, Object> pr = (Map<String, Object>) payload.get("pull_request");
                    String diffUrl = (String) pr.get("diff_url");
                    String htmlUrl = (String) pr.get("html_url"); // Link to the PR UI

                    System.out.println("🔗 Fetching code changes from: " + diffUrl);

                    // 2. Fetch the actual Diff text from GitHub
                    // (This works nicely for Public repos. Private repos need a GitHub Token header)
                    String codeDiff = restClient.get()
                            .uri(diffUrl)
                            .retrieve()
                            .body(String.class);

                    // 3. Send the REAL code to Watsonx
                    System.out.println("🔍 Code fetched (" + codeDiff.length() + " chars). Running AI Scan...");
                    String aiAnalysis = manualService.analyzeCode(codeDiff);

                    System.out.println("✅ Analysis sent to GitHub (Console)");
                    return aiAnalysis;

                } catch (Exception e) {
                    e.printStackTrace();
                    return "❌ Failed to process PR: " + e.getMessage();
                }
            }
        }
        return "Event Ignored";
    }
}
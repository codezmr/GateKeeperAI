package com.gatekeeper.api.controller;

// import com.gatekeeper.api.service.AIService; // <-- Comment this out
import com.gatekeeper.api.service.ManualWatsonxService; // <-- Add this
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    // private final AIService aiService;
    private final ManualWatsonxService manualService; // <-- Switch to Manual

    public WebhookController(ManualWatsonxService manualService) {
        this.manualService = manualService;
    }

    @PostMapping
    public String handleGitHubEvent(@RequestBody Map<String, Object> payload,
                                    @RequestHeader(value = "X-GitHub-Event", defaultValue = "unknown") String eventType) {

        System.out.println("\n🔔 Webhook Received: " + eventType);

        if ("pull_request".equals(eventType)) {
            String action = (String) payload.get("action");
            if ("opened".equals(action)) {

                String mockVulnerableCode = """
                        public void login(String user, String pass) {
                             System.out.println("Password is: " + pass); // VULNERABILITY!
                             String sql = "SELECT * FROM users WHERE name = " + user; // VULNERABILITY!
                        }
                        """;

                System.out.println("🔍 Running Manual Watsonx Scan...");

                // Call the Manual Service
                return manualService.analyzeCode(mockVulnerableCode);
            }
        }
        return "Event Ignored";
    }
}
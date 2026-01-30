package com.gatekeeper.api.service;

import com.gatekeeper.api.model.ScanReport;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class GateKeeperService {

    private final GitHubClient gitHubClient;
    private final ManualWatsonxService watsonxService;
    private final SseService sseService;

    // In-Memory Database
    private final List<ScanReport> scanHistory = new CopyOnWriteArrayList<>();

    public GateKeeperService(GitHubClient gitHubClient, ManualWatsonxService watsonxService, SseService sseService) {
        this.gitHubClient = gitHubClient;
        this.watsonxService = watsonxService;
        this.sseService = sseService;
    }

    public List<ScanReport> getHistory() {
        return scanHistory;
    }

    public String processPullRequest(Map<String, Object> payload) {
        try {
            // 1. Extract Data
            Map<String, Object> pr = (Map<String, Object>) payload.get("pull_request");
            String apiUrl = (String) pr.get("url");

            String repoName = "GateKeeper/Demo-Repo";
            if (payload.containsKey("repository") && payload.get("repository") != null) {
                Map<String, Object> repo = (Map<String, Object>) payload.get("repository");
                repoName = (String) repo.getOrDefault("full_name", repoName);
            }
            String prNum = String.valueOf(pr.getOrDefault("number", "1"));

            sseService.broadcast("🔗 Fetching PR #" + prNum + " via API: " + apiUrl);

            // 2. Fetch Code
            String codeToAnalyze = gitHubClient.fetchDiff(apiUrl);

            if (codeToAnalyze == null || codeToAnalyze.isEmpty()) {
                sseService.broadcast("❌ API Download Failed or Empty. Aborting scan.");
                return "Error: Could not fetch diff from GitHub.";
            }

            sseService.broadcast("📦 Successfully fetched Real Code (" + codeToAnalyze.length() + " chars)");

            // 3. AI Analysis
            sseService.broadcast("🤖 Sending to IBM Watsonx (Granite 3.0)...");
            String analysis = watsonxService.analyzeCode(codeToAnalyze);

            // 4. Save Report
            String status = analysis.contains("SAFE") ? "SAFE" : "VULNERABLE";
            String cleanAnalysis = analysis.replace("✅ ANALYSIS COMPLETE:\n", "");

            ScanReport report = new ScanReport(
                    UUID.randomUUID().toString(),
                    repoName,
                    prNum,
                    status,
                    cleanAnalysis,
                    LocalDateTime.now()
            );

            scanHistory.add(0, report);
            sseService.broadcast("✅ Report Generated for " + repoName);

            return analysis;

        } catch (Exception e) {
            e.printStackTrace();
            sseService.broadcast("❌ Processing Error: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }
}
package com.gatekeeper.api.service;

import com.gatekeeper.api.client.GitHubApiClient;
import com.gatekeeper.api.client.WatsonxApiClient;
import com.gatekeeper.api.dto.AnalysisResult;
import com.gatekeeper.api.dto.WebhookPayload;
import com.gatekeeper.api.exception.GitHubApiException;
import com.gatekeeper.api.model.ScanReport;
import com.gatekeeper.api.repository.ScanReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Core business logic service for GateKeeper operations
 */
@Service
public class GateKeeperService {

    private static final Logger log = LoggerFactory.getLogger(GateKeeperService.class);

    private final GitHubApiClient gitHubApiClient;
    private final WatsonxApiClient watsonxApiClient;
    private final SseService sseService;
    private final ScanReportRepository reportRepository;

    public GateKeeperService(
            GitHubApiClient gitHubApiClient,
            WatsonxApiClient watsonxApiClient,
            SseService sseService,
            ScanReportRepository reportRepository
    ) {
        this.gitHubApiClient = gitHubApiClient;
        this.watsonxApiClient = watsonxApiClient;
        this.sseService = sseService;
        this.reportRepository = reportRepository;
    }

    /**
     * Retrieves all scan history
     */
    public List<ScanReport> getHistory() {
        return reportRepository.findAll();
    }

    /**
     * Processes a pull request webhook event
     *
     * @param payload the raw webhook payload
     * @return the analysis result
     */
    public String processPullRequest(Map<String, Object> payload) {
        WebhookPayload webhookPayload = WebhookPayload.fromRawPayload(payload);

        validatePayload(webhookPayload);

        String prUrl = webhookPayload.getPrUrl();
        String prNumber = webhookPayload.getPrNumber();
        String repoName = webhookPayload.getRepositoryFullName();

        sseService.broadcast("🔗 Fetching PR #" + prNumber + " via API: " + prUrl);

        // Fetch code diff from GitHub
        String codeToAnalyze = fetchCodeDiff(prUrl);
        sseService.broadcast("📦 Successfully fetched Real Code (" + codeToAnalyze.length() + " chars)");

        // Perform AI analysis
        sseService.broadcast("🤖 Sending to IBM Watsonx (Granite 3.0)...");
        AnalysisResult analysisResult = analyzeCode(codeToAnalyze);

        // Save the report
        ScanReport report = createAndSaveReport(repoName, prNumber, analysisResult);
        sseService.broadcast("✅ Report Generated for " + repoName + " [" + report.status() + "]");

        return formatAnalysisOutput(analysisResult);
    }

    private void validatePayload(WebhookPayload payload) {
        if (payload.pullRequest() == null) {
            throw new IllegalArgumentException("Payload missing 'pull_request' object");
        }
        if (payload.getPrUrl() == null || payload.getPrUrl().isBlank()) {
            throw new IllegalArgumentException("Pull request URL is missing");
        }
    }

    private String fetchCodeDiff(String prUrl) {
        String codeDiff = gitHubApiClient.fetchDiff(prUrl);

        if (codeDiff == null || codeDiff.isEmpty()) {
            sseService.broadcast("❌ API Download Failed or Empty. Aborting scan.");
            throw new GitHubApiException("Could not fetch diff from GitHub - empty response");
        }

        return codeDiff;
    }

    private AnalysisResult analyzeCode(String codeDiff) {
        String analysis = watsonxApiClient.analyzeCode(codeDiff);
        String cleanAnalysis = analysis.replace("✅ ANALYSIS COMPLETE:\n", "");

        String status = analysis.toUpperCase().contains("VULNERABILITY REPORT")
                ? AnalysisResult.STATUS_VULNERABLE
                : AnalysisResult.STATUS_SAFE;

        return new AnalysisResult(status, cleanAnalysis, codeDiff);
    }

    private ScanReport createAndSaveReport(String repoName, String prNumber, AnalysisResult result) {
        ScanReport report = new ScanReport(
                UUID.randomUUID().toString(),
                repoName,
                prNumber,
                result.status(),
                result.analysis(),
                result.rawDiff(),
                LocalDateTime.now()
        );

        reportRepository.save(report);
        log.info("Saved scan report for {} PR#{} - Status: {}", repoName, prNumber, result.status());

        return report;
    }

    private String formatAnalysisOutput(AnalysisResult result) {
        return "✅ ANALYSIS COMPLETE:\n" + result.analysis();
    }
}
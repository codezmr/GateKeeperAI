package com.gatekeeper.api.constants;

/**
 * Application-wide constants
 */
public final class GateKeeperConstants {

    private GateKeeperConstants() {
        // Prevent instantiation
    }

    // GitHub Events
    public static final String EVENT_PULL_REQUEST = "pull_request";
    public static final String ACTION_OPENED = "opened";
    public static final String ACTION_SYNCHRONIZE = "synchronize";

    // Status
    public static final String STATUS_SAFE = "SAFE";
    public static final String STATUS_VULNERABLE = "VULNERABLE";

    // Messages
    public static final String MSG_ANALYSIS_COMPLETE = "✅ ANALYSIS COMPLETE:\n";
    public static final String MSG_WEBHOOK_RECEIVED = "\n🔔 Webhook Received: ";
    public static final String MSG_FETCHING_PR = "🔗 Fetching PR #%s via API: %s";
    public static final String MSG_FETCH_SUCCESS = "📦 Successfully fetched Real Code (%d chars)";
    public static final String MSG_SENDING_TO_AI = "🤖 Sending to IBM Watsonx (Granite 3.0)...";
    public static final String MSG_REPORT_GENERATED = "✅ Report Generated for %s [%s]";
    public static final String MSG_FETCH_FAILED = "❌ API Download Failed or Empty. Aborting scan.";
}

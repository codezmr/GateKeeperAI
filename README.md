# GateKeeper AI

Automated security code review for regulated industries. GateKeeper AI integrates with your CI/CD pipeline to analyze pull requests in real time using IBM Watsonx, providing actionable security feedback and remediation suggestions.

## Features
- Context-aware vulnerability detection (e.g., log injection, SQLi, weak crypto)
- Real-time security dashboard
- Auto-remediation suggestions
- GitHub webhook integration

## Tech Stack
- Java 21, Spring Boot 3.3
- IBM Watsonx.ai (Granite 3-8b-instruct)
- Maven

## Project Structure

```
src/main/java/com/gatekeeper/api/
├── GateKeeperApplication.java    # Main application entry point
├── client/                       # External API clients
│   ├── GitHubApiClient.java      # GitHub API integration
│   └── WatsonxApiClient.java     # IBM Watsonx AI integration
├── config/                       # Configuration classes
│   ├── AppConfig.java            # Application configuration
│   ├── GitHubProperties.java     # GitHub configuration properties
│   └── WatsonxProperties.java    # Watsonx configuration properties
├── constants/                    # Application constants
│   └── GateKeeperConstants.java
├── controller/                   # REST controllers
│   └── WebhookController.java    # Webhook and API endpoints
├── dto/                          # Data Transfer Objects
│   ├── AnalysisResult.java
│   ├── ApiResponse.java
│   └── WebhookPayload.java
├── exception/                    # Exception handling
│   ├── AiAnalysisException.java
│   ├── GitHubApiException.java
│   └── GlobalExceptionHandler.java
├── model/                        # Domain models
│   └── ScanReport.java
├── repository/                   # Data access layer
│   ├── InMemoryScanReportRepository.java
│   └── ScanReportRepository.java
└── service/                      # Business logic services
    ├── AIService.java
    ├── GateKeeperService.java
    └── SseService.java
```

## Quick Start

### 1. Configure Environment Variables (Recommended)

Set the following environment variables for production use:

```bash
export WATSONX_BASE_URL=https://us-south.ml.cloud.ibm.com
export WATSONX_PROJECT_ID=your-project-id
export WATSONX_IAM_TOKEN=your-iam-token
export WATSONX_MODEL_ID=ibm/granite-3-8b-instruct
export GITHUB_TOKEN=your-github-token  # Optional, for private repos
```

### 2. Or Configure via application.properties

Edit `src/main/resources/application.properties`:

```properties
# IBM Watsonx Configuration
gatekeeper.watsonx.base-url=https://us-south.ml.cloud.ibm.com
gatekeeper.watsonx.project-id=YOUR_PROJECT_ID
gatekeeper.watsonx.iam-token=YOUR_IAM_TOKEN
gatekeeper.watsonx.model-id=ibm/granite-3-8b-instruct

# GitHub Configuration (optional)
gatekeeper.github.token=YOUR_GITHUB_TOKEN
```

### 3. Run the Application

**Using Maven:**
```bash
mvn spring-boot:run
```

**Using specific profile:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**In IntelliJ:** Open `GateKeeperApplication.java` and click Run.

Wait for: `🚀 GateKeeper AI is running and ready to analyze code!`

### 4. Expose Localhost (for GitHub Webhooks)

```bash
ngrok http 8080
```
Copy the HTTPS URL provided by ngrok.

### 5. Configure GitHub Webhook

1. In your GitHub repo: **Settings → Webhooks → Add webhook**
2. Payload URL: `https://<ngrok-id>.ngrok-free.app/api/webhook`
3. Content type: `application/json`
4. Events: Enable **"Pull requests"**

### 6. View Dashboard

Open [http://localhost:8080/index.html](http://localhost:8080/index.html) in your browser.

### 7. Test

Create or update a pull request in your repo. Watch the dashboard for live analysis and results.

## API Endpoints

| Method | Endpoint       | Description                    |
|--------|----------------|--------------------------------|
| GET    | `/api/history` | Get all scan history           |
| GET    | `/api/stream`  | SSE endpoint for live logs     |
| POST   | `/api/webhook` | GitHub webhook handler         |

## Configuration Reference

### Environment Variables

| Variable              | Description                          | Default                                    |
|-----------------------|--------------------------------------|--------------------------------------------|
| `SERVER_PORT`         | Application server port              | 8080                                       |
| `WATSONX_BASE_URL`    | IBM Watsonx API base URL             | https://us-south.ml.cloud.ibm.com          |
| `WATSONX_PROJECT_ID`  | IBM Watsonx project ID               | -                                          |
| `WATSONX_IAM_TOKEN`   | IBM Watsonx IAM token                | -                                          |
| `WATSONX_MODEL_ID`    | AI model to use                      | ibm/granite-3-8b-instruct                  |
| `WATSONX_MAX_TOKENS`  | Maximum tokens for AI response       | 500                                        |
| `GITHUB_TOKEN`        | GitHub token for private repos       | -                                          |

### Spring Profiles

- `dev` - Development profile with verbose logging
- `prod` - Production profile with minimal logging

---

*Built for secure, efficient code review automation.*

package com.gatekeeper.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for AI-powered code analysis using Spring AI
 */
@Service
public class AIService {

    private static final Logger log = LoggerFactory.getLogger(AIService.class);

    private final ChatClient chatClient;
    private final String systemInstruction;

    public AIService(
            ChatClient.Builder builder,
            @Value("${gatekeeper.ai.system-instruction:You are GateKeeper AI, a Senior Security Architect for a Fintech Bank.}") String baseInstruction
    ) {
        this.systemInstruction = buildSystemInstruction(baseInstruction);
        this.chatClient = builder
                .defaultSystem(systemInstruction)
                .build();
        log.info("AIService initialized with Spring AI ChatClient");
    }

    private String buildSystemInstruction(String baseInstruction) {
        return """
            %s
            Your job is to review Java code changes for security vulnerabilities.
            
            STRICTLY ANALYZE THE CODE FOR THESE 3 CRITICAL ISSUES:
            
            1. 🚨 SENSITIVE LOGGING (Highest Priority):
               - Look for 'System.out.println', 'logger.info', or 'logger.debug' statements.
               - Check if they print variables named: 'password', 'key', 'token', 'secret', 'auth', 'credential', 'pan', 'cvv'.
               - Example of BAD Code: 'log.info("Password: " + userPassword);'
            
            2. 🔑 HARDCODED SECRETS:
               - Look for string literals that look like API Keys, Passwords, or AWS Credentials.
            
            3. 💉 SQL INJECTION:
               - Look for string concatenation inside SQL queries (e.g., "SELECT * FROM users WHERE name = " + input).
            
            RESPONSE FORMAT:
            If the code is safe, reply strictly with: "SAFE".
            If issues are found, reply with a bulleted list:
            - [SEVERITY: HIGH/MEDIUM] : [ISSUE TYPE] : [LINE NUMBER] : [BRIEF EXPLANATION]
            """.formatted(baseInstruction);
    }

    /**
     * Analyzes code for security vulnerabilities
     *
     * @param codeDiff the code diff to analyze
     * @return the analysis result
     */
    public String analyzeCode(String codeDiff) {
        try {
            log.info("Sending code to AI for analysis...");
            String result = chatClient.prompt()
                    .user("Review this code change:\n" + codeDiff)
                    .call()
                    .content();
            log.info("AI analysis completed successfully");
            return result;
        } catch (Exception e) {
            log.error("AI Analysis failed", e);
            return "❌ AI Analysis Failed: " + e.getMessage();
        }
    }
}
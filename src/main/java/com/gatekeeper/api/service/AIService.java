package com.gatekeeper.api.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AIService {

    private final ChatClient chatClient;

    // The "Personality" of the AI. We instruct it to be a Security Auditor.
    private static final String SYSTEM_INSTRUCTION = """
            You are GateKeeper AI, a Senior Security Architect for a Fintech Bank.
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
            """;

    // Constructor Injection automatically wires up the IBM Watsonx connection
    public AIService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem(SYSTEM_INSTRUCTION)
                .build();
    }

    public String analyzeCode(String codeDiff) {
        try {
            System.out.println("🤖 Sending code to IBM Watsonx for analysis...");
            return chatClient.prompt()
                    .user("Review this code change:\n" + codeDiff)
                    .call()
                    .content();
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ AI Analysis Failed: " + e.getMessage();
        }
    }
}
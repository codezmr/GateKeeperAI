package com.gatekeeper.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ConfigCheck implements CommandLineRunner {

    @Value("${spring.ai.watsonx.ai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.watsonx.ai.project-id}")
    private String projectId;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n🔍 --- CONFIGURATION DIAGNOSTIC ---");
        System.out.println("🌍 Target URL:  " + baseUrl);
        System.out.println("🆔 Project ID:  " + projectId);
        System.out.println("----------------------------------\n");
    }
}
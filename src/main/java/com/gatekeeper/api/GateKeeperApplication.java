package com.gatekeeper.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for GateKeeper AI
 */
@SpringBootApplication
public class GateKeeperApplication {

    private static final Logger log = LoggerFactory.getLogger(GateKeeperApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(GateKeeperApplication.class, args);
        log.info("🚀 GateKeeper AI is running and ready to analyze code!");
    }
}
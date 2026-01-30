// src/main/java/com/gatekeeper/api/demo/Vulnerable.java
package com.gatekeeper.api.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vulnerable {
    private static final Logger logger = LoggerFactory.getLogger(Vulnerable.class);

    public void login(String username, String password) {
        // Insecure: Logging credentials
        logger.info("Attempting login with 2username: {} and password: {}", username, password);
        // Simulate authentication logic
        if ("admin".equals(username) && "password123".equals(password)) {
            logger.info("Login successful for 2 user: {}", username);
        } else {
            logger.warn("Login failed for user: {}", username);
            logger.warn("Login failed for test only password: {}", password);
        }
    }
}

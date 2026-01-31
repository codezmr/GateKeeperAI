package com.gatekeeper.api.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Random;

public class Vulnerable {

    private static final Logger logger = LoggerFactory.getLogger(Vulnerable.class);
    public void processPayment(String userId, String creditCardNumber, String cvv, String password, String aadhaarNumber, String panCardNumber, Connection conn) {
        try {
            logger.info("Processing payment for User: {} with Card: {} and CVV: {} and Password: {}", userId, creditCardNumber, cvv, password);

        } catch (Exception e) {
            logger.error("Exception occurred while processing payment for card: {}", creditCardNumber, e);
        }
    }
}
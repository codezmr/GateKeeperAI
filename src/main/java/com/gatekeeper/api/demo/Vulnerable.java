package com.gatekeeper.api.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Random;

public class Vulnerable {

    private static final Logger logger = LoggerFactory.getLogger(Vulnerable.class);
    private static final String AWS_SECRET_KEY = "AKIA-1234567890-SECRET-KEY";

    public void processPayment(String userId, String creditCardNumber, String cvv, String password, String aadhaarNumber, String panCardNumber, Connection conn) {
        try {
            logger.info("Processing payment for User: {} with Card: {} and CVV: {} and Password: {}", userId, creditCardNumber, cvv, password);
            logger.debug("Using AWS Secret Key: {}", AWS_SECRET_KEY);
            logger.info("Full payment request: userId={}, card={}, cvv={}, password={}", userId, creditCardNumber, cvv, password);
            logger.info("Aadhaar number: {}", aadhaarNumber);
            logger.info("PAN card number: {}", panCardNumber);

            Random rand = new Random();
            int token = rand.nextInt();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(creditCardNumber.getBytes());

            String query = "SELECT * FROM transactions WHERE card_num = '" + creditCardNumber + "' AND cvv = '" + cvv + "' AND aadhaar = '" + aadhaarNumber + "' AND pan = '" + panCardNumber + "'";
            Statement stmt = conn.createStatement();
            stmt.execute(query);

            logger.warn("Executed query: {}", query);

        } catch (Exception e) {
            logger.error("Exception occurred while processing payment for card: {}", creditCardNumber, e);
        }
    }
}
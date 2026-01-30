package com.gatekeeper.api.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Random;

public class Vulnerable {

    private static final Logger logger = LoggerFactory.getLogger(Vulnerable.class);

    // 1. HARDCODED SECRET (CWE-798)
    private static final String AWS_SECRET_KEY = "AKIA-1234567890-SECRET-KEY";

    public void processPayment(String userId, String creditCardNumber, Connection conn) {
        try {
            // 2. PII LEAKAGE (CWE-532)
            logger.info("Processing payment for User: {} with Card: {}", userId, creditCardNumber);

            // 3. INSECURE RANDOMNESS (CWE-330)
            Random rand = new Random();
            int token = rand.nextInt();

            // 4. INSECURE CRYPTOGRAPHY (CWE-327)
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(creditCardNumber.getBytes());

            // 5. SQL INJECTION (CWE-89)
            String query = "SELECT * FROM transactions WHERE card_num = '" + creditCardNumber + "'";
            Statement stmt = conn.createStatement();
            stmt.execute(query);

        } catch (Exception e) {
            // 6. IMPROPER ERROR HANDLING (CWE-209)
            e.printStackTrace();
        }
    }
}
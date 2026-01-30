package com.gatekeeper.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GateKeeperApplication {

    public static void main(String[] args) {
        SpringApplication.run(GateKeeperApplication.class, args);
        System.out.println("🚀 GateKeeper AI is running on http://localhost:8080");
    }
}
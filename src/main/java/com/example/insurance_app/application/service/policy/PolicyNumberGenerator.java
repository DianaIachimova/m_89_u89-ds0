package com.example.insurance_app.application.service.policy;

import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.UUID;

@Component
public class PolicyNumberGenerator {

    public String generate() {
        return "POL-" + Year.now().getValue() + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

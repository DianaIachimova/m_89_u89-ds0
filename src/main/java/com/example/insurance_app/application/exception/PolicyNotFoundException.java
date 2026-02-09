package com.example.insurance_app.application.exception;

import java.util.UUID;

public class PolicyNotFoundException extends ResourceNotFoundException {
    public PolicyNotFoundException(UUID policyId) {
        super("Policy", "id", policyId);
    }
}

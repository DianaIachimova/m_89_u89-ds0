package com.example.insurance_app.application.schedule;

import com.example.insurance_app.application.service.policy.PolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PolicyExpirationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PolicyExpirationScheduler.class);
    private final PolicyService policyService;

    public PolicyExpirationScheduler(PolicyService policyService) {
        this.policyService = policyService;
    }

    @Scheduled(cron = "0 10 0 * * *", zone = "UTC")
    public void expireOverduePolicies() {
        int updated = policyService.expire();

        logger.info("Expired {} overdue policies", updated);
    }
}

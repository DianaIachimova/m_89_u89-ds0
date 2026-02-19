package com.example.insurance_app.controller;

import com.example.insurance_app.application.service.policy.PolicyService;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyStatusEntity;
import com.example.insurance_app.infrastructure.persistence.repository.policy.PolicyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"/test-data.sql", "/policy-expiration-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Policy Expiration Integration Tests")
class PolicyExpirationIntegrationTest {

    private static final UUID OVERDUE_POLICY_1 = UUID.fromString("a1a1a1a1-0000-4000-a000-000000000001");
    private static final UUID OVERDUE_POLICY_2 = UUID.fromString("a1a1a1a1-0000-4000-a000-000000000002");
    private static final UUID FUTURE_END_POLICY = UUID.fromString("a1a1a1a1-0000-4000-a000-000000000003");

    @Autowired
    private PolicyService policyService;
    @Autowired
    private PolicyRepository policyRepository;

    @Nested
    @DisplayName("expireOverduePolicies")
    class ExpireOverduePolicies {

        @Test
        @DisplayName("Should expire overdue ACTIVE policies and return count")
        void shouldExpireOverduePolicies() {
            int count = policyService.expire();

            assertEquals(2, count);
            PolicyEntity p1 = policyRepository.findById(OVERDUE_POLICY_1).orElseThrow();
            PolicyEntity p2 = policyRepository.findById(OVERDUE_POLICY_2).orElseThrow();
            assertEquals(PolicyStatusEntity.EXPIRED, p1.getStatus());
            assertEquals(PolicyStatusEntity.EXPIRED, p2.getStatus());
            PolicyEntity futureEnd = policyRepository.findById(FUTURE_END_POLICY).orElseThrow();
            assertEquals(PolicyStatusEntity.ACTIVE, futureEnd.getStatus());
        }

        @Test
        @DisplayName("Should update updated_at when expiring")
        void shouldUpdateUpdatedAt() {
            Instant before = Instant.now();
            policyService.expire();
            Instant after = Instant.now();

            PolicyEntity p1 = policyRepository.findById(OVERDUE_POLICY_1).orElseThrow();
            assertTrue(!p1.getUpdatedAt().isBefore(before) && !p1.getUpdatedAt().isAfter(after.plusSeconds(1)));
        }

        @Test
        @DisplayName("Should return 0 when no overdue policies left")
        void shouldReturnZeroWhenNoOverduePolicies() {
            policyService.expire();
            int secondCall = policyService.expire();

            assertEquals(0, secondCall);
        }
    }
}

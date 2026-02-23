package com.example.insurance_app.domain.policy;

import com.example.insurance_app.domain.model.policy.vo.*;
import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.broker.vo.BrokerId;
import com.example.insurance_app.domain.model.building.vo.BuildingId;
import com.example.insurance_app.domain.model.client.vo.ClientId;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyId;
import com.example.insurance_app.domain.model.policy.Policy;
import com.example.insurance_app.domain.model.policy.PolicyStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Policy Domain Model Tests")
class PolicyTest {

    private static final PolicyNumber POLICY_NUMBER = new PolicyNumber("POL-2026-00001");
    private static final PolicyReferences REFS = new PolicyReferences(
            new ClientId(UUID.randomUUID()),
            new BuildingId(UUID.randomUUID()),
            new BrokerId(UUID.randomUUID()),
            new CurrencyId(UUID.randomUUID())
    );
    private static final PolicyPeriod FUTURE_PERIOD = new PolicyPeriod(
            LocalDate.now().plusDays(1), LocalDate.now().plusYears(1)
    );
    private static final PolicyPremium PREMIUM = new PolicyPremium(
            new PremiumAmount(new BigDecimal("1000.00")),
            new PremiumAmount(new BigDecimal("1200.00"))
    );

    private Policy createDraftPolicy() {
        return Policy.createDraft(POLICY_NUMBER, REFS, FUTURE_PERIOD, PREMIUM);
    }

    private Policy createActivePolicy() {
        Policy policy = createDraftPolicy();
        policy.activate(new PremiumAmount(new BigDecimal("1250.00")));
        return policy;
    }

    @Nested
    @DisplayName("createDraft")
    class CreateDraftTests {

        @Test
        @DisplayName("Should create policy in DRAFT status with correct fields")
        void shouldCreateDraftPolicy() {
            Policy policy = createDraftPolicy();

            assertEquals(PolicyStatus.DRAFT, policy.getStatus());
            assertEquals(POLICY_NUMBER, policy.getPolicyNumber());
            assertEquals(REFS, policy.getReferences());
            assertEquals(FUTURE_PERIOD, policy.getPeriod());
            assertEquals(PREMIUM, policy.getPremium());
            assertNull(policy.getCancellationInfo());
            assertNull(policy.getAudit());
            assertNull(policy.getId());
        }

        @Test
        @DisplayName("Should reject null policy number via PolicyNumber constructor")
        void shouldRejectNullPolicyNumber() {
            assertThrows(DomainValidationException.class,
                    () -> new PolicyNumber(null));
        }

        @Test
        @DisplayName("Should reject null references")
        void shouldRejectNullReferences() {
            assertThrows(DomainValidationException.class,
                    () -> Policy.createDraft(POLICY_NUMBER, null, FUTURE_PERIOD, PREMIUM));
        }

        @Test
        @DisplayName("Should reject null period")
        void shouldRejectNullPeriod() {
            assertThrows(DomainValidationException.class,
                    () -> Policy.createDraft(POLICY_NUMBER, REFS, null, PREMIUM));
        }

        @Test
        @DisplayName("Should reject null premium")
        void shouldRejectNullPremium() {
            assertThrows(DomainValidationException.class,
                    () -> Policy.createDraft(POLICY_NUMBER, REFS, FUTURE_PERIOD, null));
        }
    }

    @Nested
    @DisplayName("activate")
    class ActivateTests {

        @Test
        @DisplayName("Should activate DRAFT policy and update final premium")
        void shouldActivateDraftPolicy() {
            Policy policy = createDraftPolicy();
            PremiumAmount newFinal = new PremiumAmount(new BigDecimal("1350.00"));

            policy.activate(newFinal);

            assertEquals(PolicyStatus.ACTIVE, policy.getStatus());
            assertEquals(newFinal, policy.getFinalPremium());
            assertEquals(PREMIUM.base(), policy.getBasePremium());
        }

        @Test
        @DisplayName("Should reject activation from ACTIVE status")
        void shouldRejectActivationFromActive() {
            Policy policy = createActivePolicy();
            PremiumAmount amount = new PremiumAmount(new BigDecimal("1000.00"));

            assertThrows(DomainValidationException.class,
                    () -> policy.activate(amount));
        }

        @Test
        @DisplayName("Should reject activation from CANCELLED status")
        void shouldRejectActivationFromCancelled() {
            Policy policy = createActivePolicy();
            policy.cancel("Test reason");
            PremiumAmount amount = new PremiumAmount(new BigDecimal("1000.00"));

            assertThrows(DomainValidationException.class,
                    () -> policy.activate(amount));
        }

        @Test
        @DisplayName("Should reject activation from EXPIRED status")
        void shouldRejectActivationFromExpired() {
            Policy policy = createActivePolicy();
            policy.expire();
            PremiumAmount amount = new PremiumAmount(new BigDecimal("1000.00"));

            assertThrows(DomainValidationException.class,
                    () -> policy.activate(amount));
        }

        @Test
        @DisplayName("Should reject activation when start date is in the past")
        void shouldRejectActivationWhenStartDateInPast() {
            PolicyPeriod pastPeriod = new PolicyPeriod(
                    LocalDate.now().minusDays(1), LocalDate.now().plusYears(1)
            );
            Policy policy = Policy.createDraft(POLICY_NUMBER, REFS, pastPeriod, PREMIUM);
            PremiumAmount amount = new PremiumAmount(new BigDecimal("1000.00"));

            assertThrows(DomainValidationException.class,
                    () -> policy.activate(amount));
        }

        @Test
        @DisplayName("Should reject null final premium on activation")
        void shouldRejectNullFinalPremium() {
            Policy policy = createDraftPolicy();

            assertThrows(DomainValidationException.class, () -> policy.activate(null));
        }
    }

    @Nested
    @DisplayName("cancel")
    class CancelTests {

        @Test
        @DisplayName("Should cancel ACTIVE policy with reason and date")
        void shouldCancelActivePolicy() {
            Policy policy = createActivePolicy();

            policy.cancel("Customer requested cancellation");

            assertEquals(PolicyStatus.CANCELLED, policy.getStatus());
            assertNotNull(policy.getCancellationInfo());
            assertEquals(LocalDate.now(), policy.getCancellationInfo().cancelledAt());
            assertEquals("Customer requested cancellation", policy.getCancellationInfo().reason());
        }

        @Test
        @DisplayName("Should reject cancellation from DRAFT status")
        void shouldRejectCancellationFromDraft() {
            Policy policy = createDraftPolicy();

            assertThrows(DomainValidationException.class, () -> policy.cancel("reason"));
        }

        @Test
        @DisplayName("Should reject cancellation from CANCELLED status")
        void shouldRejectCancellationFromCancelled() {
            Policy policy = createActivePolicy();
            policy.cancel("first cancel");

            assertThrows(DomainValidationException.class, () -> policy.cancel("second cancel"));
        }

        @Test
        @DisplayName("Should reject cancellation from EXPIRED status")
        void shouldRejectCancellationFromExpired() {
            Policy policy = createActivePolicy();
            policy.expire();

            assertThrows(DomainValidationException.class, () -> policy.cancel("reason"));
        }
    }

    @Nested
    @DisplayName("expire")
    class ExpireTests {

        @Test
        @DisplayName("Should expire ACTIVE policy")
        void shouldExpireActivePolicy() {
            Policy policy = createActivePolicy();

            policy.expire();

            assertEquals(PolicyStatus.EXPIRED, policy.getStatus());
        }

        @Test
        @DisplayName("Should reject expiration from DRAFT status")
        void shouldRejectExpirationFromDraft() {
            Policy policy = createDraftPolicy();

            assertThrows(DomainValidationException.class, policy::expire);
        }

        @Test
        @DisplayName("Should reject expiration from CANCELLED status")
        void shouldRejectExpirationFromCancelled() {
            Policy policy = createActivePolicy();
            policy.cancel("reason");

            assertThrows(DomainValidationException.class, policy::expire);
        }

        @Test
        @DisplayName("Should reject expiration from EXPIRED status")
        void shouldRejectExpirationFromExpired() {
            Policy policy = createActivePolicy();
            policy.expire();

            assertThrows(DomainValidationException.class, policy::expire);
        }
    }
}

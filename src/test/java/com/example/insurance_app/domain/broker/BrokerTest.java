package com.example.insurance_app.domain.broker;

import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.broker.Broker;
import com.example.insurance_app.domain.model.broker.BrokerStatus;
import com.example.insurance_app.domain.model.broker.vo.BrokerCode;
import com.example.insurance_app.domain.model.broker.vo.BrokerName;
import com.example.insurance_app.domain.model.broker.vo.CommissionPercentage;
import com.example.insurance_app.domain.model.broker.vo.ContactInfo;
import com.example.insurance_app.domain.model.client.vo.EmailAddress;
import com.example.insurance_app.domain.model.client.vo.PhoneNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Broker Domain Model Tests")
class BrokerTest {

    private static final BrokerCode CODE = new BrokerCode("BRK-001");
    private static final BrokerName NAME = new BrokerName("Test Broker");
    private static final ContactInfo CONTACT = new ContactInfo(
            new EmailAddress("broker@test.com"),
            new PhoneNumber("+40712345678")
    );
    private static final CommissionPercentage COMMISSION = new CommissionPercentage(new BigDecimal("0.05"));

    private Broker createActiveBroker() {
        return Broker.createNew(CODE, NAME, CONTACT, BrokerStatus.ACTIVE, COMMISSION);
    }

    private Broker createInactiveBroker() {
        return Broker.createNew(CODE, NAME, CONTACT, BrokerStatus.INACTIVE, COMMISSION);
    }

    @Nested
    @DisplayName("createNew")
    class CreateNewTests {

        @Test
        @DisplayName("Should create broker with correct fields")
        void shouldCreateBrokerWithCorrectFields() {
            Broker broker = createActiveBroker();

            assertNull(broker.getId());
            assertEquals(CODE, broker.getCode());
            assertEquals(NAME, broker.getName());
            assertEquals(CONTACT, broker.getContactInfo());
            assertEquals(BrokerStatus.ACTIVE, broker.getStatus());
            assertEquals(COMMISSION, broker.getCommissionPercentage());
            assertNull(broker.getAudit());
        }

        @Test
        @DisplayName("Should reject null code")
        void shouldRejectNullCode() {
            assertThrows(DomainValidationException.class,
                    () -> Broker.createNew(null, NAME, CONTACT, BrokerStatus.ACTIVE, COMMISSION));
        }

        @Test
        @DisplayName("Should reject null name")
        void shouldRejectNullName() {
            assertThrows(DomainValidationException.class,
                    () -> Broker.createNew(CODE, null, CONTACT, BrokerStatus.ACTIVE, COMMISSION));
        }

        @Test
        @DisplayName("Should reject null contact info")
        void shouldRejectNullContactInfo() {
            assertThrows(DomainValidationException.class,
                    () -> Broker.createNew(CODE, NAME, null, BrokerStatus.ACTIVE, COMMISSION));
        }

        @Test
        @DisplayName("Should reject null status")
        void shouldRejectNullStatus() {
            assertThrows(DomainValidationException.class,
                    () -> Broker.createNew(CODE, NAME, CONTACT, null, COMMISSION));
        }

        @Test
        @DisplayName("Should allow null commission percentage")
        void shouldAllowNullCommission() {
            Broker broker = Broker.createNew(CODE, NAME, CONTACT, BrokerStatus.ACTIVE, null);
            assertNull(broker.getCommissionPercentage());
        }
    }

    @Nested
    @DisplayName("activate")
    class ActivateTests {

        @Test
        @DisplayName("Should activate INACTIVE broker")
        void shouldActivateInactiveBroker() {
            Broker broker = createInactiveBroker();

            broker.activate();

            assertEquals(BrokerStatus.ACTIVE, broker.getStatus());
        }

        @Test
        @DisplayName("Should throw when activating already ACTIVE broker")
        void shouldThrowWhenAlreadyActive() {
            Broker broker = createActiveBroker();

            assertThrows(DomainValidationException.class, broker::activate);
        }
    }

    @Nested
    @DisplayName("deactivate")
    class DeactivateTests {

        @Test
        @DisplayName("Should deactivate ACTIVE broker")
        void shouldDeactivateActiveBroker() {
            Broker broker = createActiveBroker();

            broker.deactivate();

            assertEquals(BrokerStatus.INACTIVE, broker.getStatus());
        }

        @Test
        @DisplayName("Should throw when deactivating already INACTIVE broker")
        void shouldThrowWhenAlreadyInactive() {
            Broker broker = createInactiveBroker();

            assertThrows(DomainValidationException.class, broker::deactivate);
        }
    }

    @Nested
    @DisplayName("ensureActive")
    class EnsureActiveTests {

        @Test
        @DisplayName("Should pass for ACTIVE broker")
        void shouldPassForActiveBroker() {
            Broker broker = createActiveBroker();

            assertDoesNotThrow(broker::ensureActive);
        }

        @Test
        @DisplayName("Should throw for INACTIVE broker")
        void shouldThrowForInactiveBroker() {
            Broker broker = createInactiveBroker();

            assertThrows(DomainValidationException.class, broker::ensureActive);
        }
    }

    @Nested
    @DisplayName("updateDetails")
    class UpdateDetailsTests {

        @Test
        @DisplayName("Should update name, contact and commission")
        void shouldUpdateDetails() {
            Broker broker = createActiveBroker();
            BrokerName newName = new BrokerName("Updated Broker");
            ContactInfo newContact = new ContactInfo(
                    new EmailAddress("updated@test.com"), null
            );
            CommissionPercentage newCommission = new CommissionPercentage(new BigDecimal("0.1"));

            broker.updateDetails(newName, newContact, newCommission);

            assertEquals(newName, broker.getName());
            assertEquals(newContact, broker.getContactInfo());
            assertEquals(newCommission, broker.getCommissionPercentage());
        }

        @Test
        @DisplayName("Should reject null name on update")
        void shouldRejectNullNameOnUpdate() {
            Broker broker = createActiveBroker();

            assertThrows(DomainValidationException.class,
                    () -> broker.updateDetails(null, CONTACT, COMMISSION));
        }

        @Test
        @DisplayName("Should reject null contact on update")
        void shouldRejectNullContactOnUpdate() {
            Broker broker = createActiveBroker();

            assertThrows(DomainValidationException.class,
                    () -> broker.updateDetails(NAME, null, COMMISSION));
        }

        @Test
        @DisplayName("Should allow null commission on update")
        void shouldAllowNullCommissionOnUpdate() {
            Broker broker = createActiveBroker();

            broker.updateDetails(NAME, CONTACT, null);

            assertNull(broker.getCommissionPercentage());
        }
    }
}

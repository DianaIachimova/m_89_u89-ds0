package com.example.insurance_app.domain.metadata;

import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.metadata.currency.Currency;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyCode;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyName;
import com.example.insurance_app.domain.model.metadata.currency.vo.ExchangeRateToBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Currency Domain Model Tests")
class CurrencyTest {

    private static final CurrencyCode CODE = new CurrencyCode("RON");
    private static final CurrencyName NAME = new CurrencyName("Romanian Leu");
    private static final ExchangeRateToBase RATE = new ExchangeRateToBase(new BigDecimal("1.000000"));

    @Nested
    @DisplayName("createNew")
    class CreateNewTests {

        @Test
        @DisplayName("Should create currency with correct fields")
        void shouldCreateWithCorrectFields() {
            Currency currency = Currency.createNew(CODE, NAME, RATE, true);

            assertNull(currency.getId());
            assertEquals(CODE, currency.getCode());
            assertEquals(NAME, currency.getName());
            assertEquals(RATE, currency.getExchangeRate());
            assertTrue(currency.isActive());
            assertNull(currency.getAudit());
        }

        @Test
        @DisplayName("Should create inactive currency")
        void shouldCreateInactive() {
            Currency currency = Currency.createNew(CODE, NAME, RATE, false);
            assertFalse(currency.isActive());
        }

        @Test
        @DisplayName("Should reject null code")
        void shouldRejectNullCode() {
            assertThrows(DomainValidationException.class,
                    () -> Currency.createNew(null, NAME, RATE, true));
        }

        @Test
        @DisplayName("Should reject null name")
        void shouldRejectNullName() {
            assertThrows(DomainValidationException.class,
                    () -> Currency.createNew(CODE, null, RATE, true));
        }

        @Test
        @DisplayName("Should reject null exchange rate")
        void shouldRejectNullRate() {
            assertThrows(DomainValidationException.class,
                    () -> Currency.createNew(CODE, NAME, null, true));
        }
    }

    @Nested
    @DisplayName("activate / deactivate")
    class StatusTests {

        @Test
        @DisplayName("Should activate inactive currency")
        void shouldActivate() {
            Currency currency = Currency.createNew(CODE, NAME, RATE, false);

            currency.activate();

            assertTrue(currency.isActive());
        }

        @Test
        @DisplayName("Activate is idempotent for active currency")
        void activateIdempotent() {
            Currency currency = Currency.createNew(CODE, NAME, RATE, true);
            currency.activate();
            assertTrue(currency.isActive());
        }

        @Test
        @DisplayName("Should deactivate active currency")
        void shouldDeactivate() {
            Currency currency = Currency.createNew(CODE, NAME, RATE, true);

            currency.deactivate();

            assertFalse(currency.isActive());
        }

        @Test
        @DisplayName("Deactivate is idempotent for inactive currency")
        void deactivateIdempotent() {
            Currency currency = Currency.createNew(CODE, NAME, RATE, false);
            currency.deactivate();
            assertFalse(currency.isActive());
        }
    }
}

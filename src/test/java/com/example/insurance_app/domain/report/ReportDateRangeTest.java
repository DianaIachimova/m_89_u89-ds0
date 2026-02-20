package com.example.insurance_app.domain.report;

import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.report.vo.ReportDateRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ReportDateRange Value Object Tests")
class ReportDateRangeTest {

    @Test
    @DisplayName("Should create valid date range when from is before to")
    void shouldCreateValidDateRange() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 12, 31);

        ReportDateRange range = new ReportDateRange(from, to);

        assertEquals(from, range.from());
        assertEquals(to, range.to());
    }

    @Test
    @DisplayName("Should create valid date range when from equals to")
    void shouldCreateValidDateRangeWhenEqual() {
        LocalDate date = LocalDate.of(2026, 6, 15);

        ReportDateRange range = new ReportDateRange(date, date);

        assertEquals(date, range.from());
        assertEquals(date, range.to());
    }

    @Test
    @DisplayName("Should throw exception when from is after to")
    void shouldThrowWhenFromIsAfterTo() {
        LocalDate from = LocalDate.of(2026, 12, 31);
        LocalDate to = LocalDate.of(2026, 1, 1);

        DomainValidationException exception = assertThrows(
            DomainValidationException.class,
            () -> new ReportDateRange(from, to)
        );

        assertTrue(exception.getMessage().contains("From date must not be after to date"));
    }

    @Test
    @DisplayName("Should throw exception when from is null")
    void shouldThrowWhenFromIsNull() {
        LocalDate to = LocalDate.of(2026, 12, 31);

        DomainValidationException exception = assertThrows(
            DomainValidationException.class,
            () -> new ReportDateRange(null, to)
        );

        assertTrue(exception.getMessage().contains("From date"));
    }

    @Test
    @DisplayName("Should throw exception when to is null")
    void shouldThrowWhenToIsNull() {
        LocalDate from = LocalDate.of(2026, 1, 1);

        DomainValidationException exception = assertThrows(
            DomainValidationException.class,
            () -> new ReportDateRange(from, null)
        );

        assertTrue(exception.getMessage().contains("To date"));
    }

    @Test
    @DisplayName("Should throw exception when both dates are null")
    void shouldThrowWhenBothDatesAreNull() {
        assertThrows(
            DomainValidationException.class,
            () -> new ReportDateRange(null, null)
        );
    }
}

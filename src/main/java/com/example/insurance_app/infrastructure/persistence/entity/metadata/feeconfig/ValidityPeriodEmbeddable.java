package com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
public class ValidityPeriodEmbeddable {

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    protected ValidityPeriodEmbeddable() {}

    public ValidityPeriodEmbeddable(LocalDate effectiveFrom, LocalDate effectiveTo) {
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }
}
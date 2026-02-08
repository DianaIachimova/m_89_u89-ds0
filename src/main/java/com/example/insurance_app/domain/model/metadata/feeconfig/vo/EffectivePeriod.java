package com.example.insurance_app.domain.model.metadata.feeconfig.vo;

import com.example.insurance_app.domain.util.DomainAssertions;
import java.time.LocalDate;

public record EffectivePeriod(LocalDate from, LocalDate to){
    public EffectivePeriod{
        DomainAssertions.requireEffectivePeriod(from,to);
    }

    public static EffectivePeriod of(LocalDate from, LocalDate to) {
        return new EffectivePeriod(from, to);
    }

    public boolean includes(LocalDate date) {
        DomainAssertions.notNull(date, "date");
        if (date.isBefore(from)) return false;
        return to == null || !date.isAfter(to);
    }

    public EffectivePeriod changeEnd(LocalDate newEnd) {
        return new EffectivePeriod(this.from, newEnd);
    }

}

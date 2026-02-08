package com.example.insurance_app.domain.model.metadata.feeconfig;

import com.example.insurance_app.domain.model.metadata.feeconfig.vo.EffectivePeriod;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeeCode;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeeName;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeePercentage;
import com.example.insurance_app.domain.util.DomainAssertions;

public record FeeDetails(
        FeeCode code,
        FeeName name,
        FeeConfigurationType type,
        FeePercentage percentage,
        EffectivePeriod period
) {
    public FeeDetails {
        DomainAssertions.notNull(code, "code");
        DomainAssertions.notNull(type, "type");
        DomainAssertions.notNull(percentage, "percentage");
        DomainAssertions.notNull(period, "effective period");
        DomainAssertions.notNull(name,"name");
    }

    public static FeeDetails of(FeeCode code, FeeName name, FeeConfigurationType type, FeePercentage percentage, EffectivePeriod period) {
        return new FeeDetails(code, name, type, percentage, period);
    }


}

package com.example.insurance_app.domain.model.policy.vo;

public record PolicyIdentity(
        PolicyId id,
        PolicyNumber number
) {
    public static PolicyIdentity ofNew(PolicyNumber number) {
        return new PolicyIdentity(null, number);
    }
}

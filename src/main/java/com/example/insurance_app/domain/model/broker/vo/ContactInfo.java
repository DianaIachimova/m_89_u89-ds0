package com.example.insurance_app.domain.model.broker.vo;

import com.example.insurance_app.domain.model.client.vo.EmailAddress;
import com.example.insurance_app.domain.model.client.vo.PhoneNumber;
import com.example.insurance_app.domain.util.DomainAssertions;

public record ContactInfo(
        EmailAddress email,
        PhoneNumber phone
) {
    public ContactInfo {
        DomainAssertions.notNull(email, "Email");
    }


}

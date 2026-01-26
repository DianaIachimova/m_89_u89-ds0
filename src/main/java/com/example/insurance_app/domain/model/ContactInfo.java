package com.example.insurance_app.domain.model;

import com.example.insurance_app.domain.model.vo.EmailAddress;
import com.example.insurance_app.domain.model.vo.PhoneNumber;
import com.example.insurance_app.domain.util.DomainAssertions;

public record ContactInfo(
        EmailAddress email,
        PhoneNumber phone) {

    public ContactInfo{
        DomainAssertions.notNull(email, "email");
        DomainAssertions.notNull(phone, "phone");
    }

    public String getEmail() {
        return email.value();
    }

    public String getPhone() {
        return phone.value();
    }
}

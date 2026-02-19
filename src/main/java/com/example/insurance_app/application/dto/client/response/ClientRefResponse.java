package com.example.insurance_app.application.dto.client.response;

import com.example.insurance_app.application.dto.client.ClientTypeDto;
import java.util.UUID;

public record ClientRefResponse(
        UUID id,
        ClientTypeDto clientType,
        String name,
        String identificationNumber,
        ContactInfoResponse contactInfo,
        AddressResponse address
) {
}

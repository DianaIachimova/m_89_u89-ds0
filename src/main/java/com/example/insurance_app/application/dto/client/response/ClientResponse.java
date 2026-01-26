package com.example.insurance_app.application.dto.client.response;
import com.example.insurance_app.application.dto.client.ClientTypeDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ClientResponse(
        UUID id,
        ClientTypeDto clientType,
        String name,
        String identificationNumber,
        ContactInfoResponse contactInfo,
        AddressResponse address,
        Instant createdAt,
        Instant updatedAt,
        List<IdentificationNumberChangeDto> identificationNumberHistory
) {
}

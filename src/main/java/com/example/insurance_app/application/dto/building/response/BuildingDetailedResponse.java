package com.example.insurance_app.application.dto.building.response;

import com.example.insurance_app.application.dto.building.RiskIndicatorsDto;
import java.util.UUID;

public record BuildingDetailedResponse(
        UUID id,
        UUID clientId,
        AddressDetailedResponse address,
        BuildingInfoResponse buildingInfo,
        RiskIndicatorsDto riskIndicators
) {
}

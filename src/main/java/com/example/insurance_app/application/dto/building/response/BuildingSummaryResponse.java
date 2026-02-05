package com.example.insurance_app.application.dto.building.response;

import com.example.insurance_app.application.dto.building.RiskIndicatorsDto;
import java.util.UUID;

public record BuildingSummaryResponse(
        UUID id,
        AddressSummaryResponse address,
        BuildingInfoResponse buildingInfo,
        RiskIndicatorsDto riskIndicators
) {
}

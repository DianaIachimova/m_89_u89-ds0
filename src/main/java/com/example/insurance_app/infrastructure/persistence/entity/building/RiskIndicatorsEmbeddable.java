package com.example.insurance_app.infrastructure.persistence.entity.building;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class RiskIndicatorsEmbeddable {
    @Column(name = "flood_zone")
    private Boolean floodZone;

    @Column(name = "earthquake_risk_zone")
    private Boolean earthquakeRiskZone;

    protected RiskIndicatorsEmbeddable() {}

    public RiskIndicatorsEmbeddable(Boolean floodZone, Boolean earthquakeRiskZone) {
        this.floodZone = floodZone;
        this.earthquakeRiskZone = earthquakeRiskZone;
    }

    public Boolean isFloodZone() {
        return floodZone;
    }

    public Boolean isEarthquakeRiskZone() {
        return earthquakeRiskZone;
    }
}

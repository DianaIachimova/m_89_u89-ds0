package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.building.vo.RiskIndicators;
import com.example.insurance_app.infrastructure.persistence.entity.building.RiskIndicatorsEmbeddable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BuildingRiskIndicatorsMapper {

    @Mapping(target = "earthquakeZone", source = "earthquakeRiskZone")
    RiskIndicators toDomain(RiskIndicatorsEmbeddable embeddable);

    @Mapping(target = "earthquakeRiskZone", source = "earthquakeZone")
    RiskIndicatorsEmbeddable toEmbeddable(RiskIndicators risk);
}

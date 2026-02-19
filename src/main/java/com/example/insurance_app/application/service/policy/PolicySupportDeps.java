package com.example.insurance_app.application.service.policy;

import com.example.insurance_app.infrastructure.persistence.mapper.BuildingEntityMapper;
import com.example.insurance_app.infrastructure.persistence.mapper.ClientEntityMapper;
import org.springframework.stereotype.Component;

@Component
public record PolicySupportDeps(
        ClientEntityMapper clientEntityMapper,
        BuildingEntityMapper buildingEntityMapper,
        PolicyNumberGenerator numberGenerator
) {
}

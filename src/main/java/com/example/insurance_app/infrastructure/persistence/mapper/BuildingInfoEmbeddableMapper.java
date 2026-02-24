package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.building.vo.BuildingInfo;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingInfoEmbeddable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = EnumEntityMapper.class)
public interface BuildingInfoEmbeddableMapper {

    @Mapping(target = "type", source = "buildingType")
    BuildingInfo toDomain(BuildingInfoEmbeddable embeddable);

    @Mapping(target = "buildingType", source = "type")
    BuildingInfoEmbeddable toEmbeddable(BuildingInfo info);
}

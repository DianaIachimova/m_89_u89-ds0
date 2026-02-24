package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.building.vo.BuildingAddress;
import com.example.insurance_app.infrastructure.persistence.entity.building.AddressEmbeddable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BuildingAddressEmbeddableMapper {

    BuildingAddress toDomain(AddressEmbeddable embeddable);

    AddressEmbeddable toEmbeddable(BuildingAddress address);
}

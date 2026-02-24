package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.client.response.IdentificationNumberChangeDto;
import com.example.insurance_app.infrastructure.persistence.entity.client.IdentificationNumberChangeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IdentificationNumberChangeMapper {

    @Mapping(target = "changeReason", source = "reason")
    IdentificationNumberChangeDto toDto(IdentificationNumberChangeEntity entity);
}

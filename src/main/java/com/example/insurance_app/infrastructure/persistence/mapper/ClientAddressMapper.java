package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.client.vo.Address;
import com.example.insurance_app.infrastructure.persistence.entity.client.AddressEmbeddable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientAddressMapper {

    Address toDomain(AddressEmbeddable embeddable);

    AddressEmbeddable toEmbeddable(Address address);
}

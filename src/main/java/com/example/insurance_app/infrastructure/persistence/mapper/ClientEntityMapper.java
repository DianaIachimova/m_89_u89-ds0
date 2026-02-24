package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.client.Client;
import com.example.insurance_app.domain.model.client.ClientType;
import com.example.insurance_app.domain.model.client.ContactInfo;
import com.example.insurance_app.domain.model.client.vo.Address;
import com.example.insurance_app.domain.model.client.vo.ClientId;
import com.example.insurance_app.domain.model.client.vo.EmailAddress;
import com.example.insurance_app.domain.model.client.vo.PhoneNumber;
import com.example.insurance_app.infrastructure.persistence.entity.client.AddressEmbeddable;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.ContactInfoEmbeddable;
import org.springframework.stereotype.Component;

@Component
public class ClientEntityMapper {

    private final EnumEntityMapper enumEntityMapper;
    private final ClientAddressMapper clientAddressMapper;

    public ClientEntityMapper(EnumEntityMapper enumEntityMapper, ClientAddressMapper clientAddressMapper) {
        this.enumEntityMapper = enumEntityMapper;
        this.clientAddressMapper = clientAddressMapper;
    }

    public Client toDomain(ClientEntity entity) {
        if (entity == null) return null;

        ClientId clientId = entity.getId() != null ? new ClientId(entity.getId()) : null;
        ClientType clientType = enumEntityMapper.toClientType(entity.getClientType());
        ContactInfo contactInfo = toContactInfo(entity.getContactInfo());
        Address address = clientAddressMapper.toDomain(entity.getAddress());

        return new Client(
                clientId,
                clientType,
                entity.getName(),
                entity.getIdentificationNumber(),
                contactInfo,
                address
        );
    }

    public ClientEntity toEntity(Client domain) {
        if (domain == null) return null;

        return new ClientEntity(
                domain.getId() != null ? domain.getId().value() : null,
                enumEntityMapper.toClientTypeEntity(domain.getClientType()),
                domain.getName(),
                domain.getIdentificationNumber(),
                toContactInfoEmbeddable(domain.getContactInfo()),
                clientAddressMapper.toEmbeddable(domain.getAddress())
        );
    }

    public void updateEntity(Client domain, ClientEntity entity) {
        if (domain == null || entity == null) return;

        entity.setName(domain.getName());
        entity.setIdentificationNumber(domain.getIdentificationNumber());
        entity.setContactInfo(toContactInfoEmbeddable(domain.getContactInfo()));
        entity.setAddress(clientAddressMapper.toEmbeddable(domain.getAddress()));
    }

    private ContactInfo toContactInfo(ContactInfoEmbeddable embeddable) {
        if (embeddable == null) return null;
        return new ContactInfo(
                new EmailAddress(embeddable.getEmail()),
                new PhoneNumber(embeddable.getPhone())
        );
    }

    private ContactInfoEmbeddable toContactInfoEmbeddable(ContactInfo contactInfo) {
        if (contactInfo == null) return null;
        return new ContactInfoEmbeddable(contactInfo.getEmail(), contactInfo.getPhone());
    }
}

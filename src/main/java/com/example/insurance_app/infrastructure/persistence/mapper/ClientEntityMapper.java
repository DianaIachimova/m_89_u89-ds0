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

    public ClientEntityMapper(EnumEntityMapper enumEntityMapper) {
        this.enumEntityMapper = enumEntityMapper;
    }

    public Client toDomain(ClientEntity entity) {
        if (entity == null) {
            return null;
        }

        ClientId clientId = entity.getId() != null ? new ClientId(entity.getId()) : null;
        ClientType clientType = enumEntityMapper.toClientType(entity.getClientType());
        ContactInfo contactInfo = toContactInfo(entity.getContactInfo());
        Address address = toAddress(entity.getAddress());

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
        if (domain == null) {
            return null;
        }

        return new ClientEntity(
                domain.getId() != null ? domain.getId().value() : null,
                enumEntityMapper.toClientTypeEntity(domain.getClientType()),
                domain.getName(),
                domain.getIdentificationNumber(),
                toContactInfoEmbeddable(domain.getContactInfo()),
                toAddressEmbeddable(domain.getAddress())
        );
    }

    public void updateEntity(Client domain, ClientEntity entity) {
        if (domain == null || entity == null) {
            return;
        }

        entity.setName(domain.getName());
        entity.setIdentificationNumber(domain.getIdentificationNumber());
        entity.setContactInfo(toContactInfoEmbeddable(domain.getContactInfo()));
        entity.setAddress(toAddressEmbeddable(domain.getAddress()));
    }


    private ContactInfo toContactInfo(ContactInfoEmbeddable embeddable) {
        if (embeddable == null) {
            return null;
        }
        return new ContactInfo(
                new EmailAddress(embeddable.getEmail()),
                new PhoneNumber(embeddable.getPhone())
        );
    }

    private ContactInfoEmbeddable toContactInfoEmbeddable(ContactInfo contactInfo) {
        if (contactInfo == null) {
            return null;
        }
        return new ContactInfoEmbeddable(
                contactInfo.getEmail(),
                contactInfo.getPhone()
        );
    }

    private Address toAddress(AddressEmbeddable embeddable) {
        if (embeddable == null) {
            return null;
        }
        return new Address(
                embeddable.getStreet(),
                embeddable.getCity(),
                embeddable.getCounty(),
                embeddable.getPostalCode(),
                embeddable.getCountry()
        );
    }

    private AddressEmbeddable toAddressEmbeddable(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressEmbeddable(
                address.street(),
                address.city(),
                address.county(),
                address.postalCode(),
                address.country()
        );
    }
}

package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.Address;
import com.example.insurance_app.domain.model.Client;
import com.example.insurance_app.domain.model.ClientType;
import com.example.insurance_app.domain.model.ContactInfo;
import com.example.insurance_app.domain.model.vo.ClientId;
import com.example.insurance_app.domain.model.vo.EmailAddress;
import com.example.insurance_app.domain.model.vo.PhoneNumber;
import com.example.insurance_app.infrastructure.persistence.entity.client.AddressEmbeddable;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.ContactInfoEmbeddable;
import org.springframework.stereotype.Component;

@Component
public class ClientEntityMapper {

    public Client toDomain(ClientEntity entity) {
        if (entity == null) {
            return null;
        }

        ClientId clientId = entity.getId() != null ? new ClientId(entity.getId()) : null;
        ClientType clientType = toClientType(entity.getClientType());
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
                toClientTypeEntity(domain.getClientType()),
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

    private ClientType toClientType(ClientTypeEntity entity) {
        if (entity == null) {
            return null;
        }
        return switch (entity) {
            case INDIVIDUAL -> ClientType.INDIVIDUAL;
            case COMPANY -> ClientType.COMPANY;
        };
    }

    private ClientTypeEntity toClientTypeEntity(ClientType domain) {
        if (domain == null) {
            return null;
        }
        return switch (domain) {
            case INDIVIDUAL -> ClientTypeEntity.INDIVIDUAL;
            case COMPANY -> ClientTypeEntity.COMPANY;
        };
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

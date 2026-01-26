package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.client.ClientTypeDto;
import com.example.insurance_app.application.dto.client.request.AddressRequest;
import com.example.insurance_app.application.dto.client.request.ContactInfoRequest;
import com.example.insurance_app.application.dto.client.request.CreateClientRequest;
import com.example.insurance_app.application.dto.client.response.AddressResponse;
import com.example.insurance_app.application.dto.client.response.ClientResponse;
import com.example.insurance_app.application.dto.client.response.ContactInfoResponse;
import com.example.insurance_app.application.dto.client.response.IdentificationNumberChangeDto;
import com.example.insurance_app.domain.model.Address;
import com.example.insurance_app.domain.model.Client;
import com.example.insurance_app.domain.model.ClientType;
import com.example.insurance_app.domain.model.ContactInfo;
import com.example.insurance_app.domain.model.vo.EmailAddress;
import com.example.insurance_app.domain.model.vo.PhoneNumber;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.IdentificationNumberChangeEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientDtoMapper {

    public Client toDomain(CreateClientRequest request) {
        if (request == null) {
            return null;
        }

        ClientType clientType = toClientType(request.clientType());
        ContactInfo contactInfo = toContactInfo(request.contactInfo());
        Address address = request.address() != null ? toAddress(request.address()) : null;

        return new Client(
                clientType,
                request.name(),
                request.identificationNumber(),
                contactInfo,
                address
        );
    }

    public ClientResponse toResponse(Client domain, ClientEntity entity, List<IdentificationNumberChangeEntity> history) {
        if (domain == null || entity == null) {
            return null;
        }

        List<IdentificationNumberChangeDto> historyDto = history != null
                ? history.stream().map(this::toHistoryDto).toList()
                : List.of();

        return new ClientResponse(
                domain.getId() != null ? domain.getId().value() : null,
                toClientTypeDto(domain.getClientType()),
                domain.getName(),
                domain.getIdentificationNumber(),
                toContactInfoResponse(domain.getContactInfo()),
                toAddressResponse(domain.getAddress()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                historyDto
        );
    }

    public ContactInfo toContactInfo(ContactInfoRequest request) {
        if (request == null) {
            return null;
        }
        return new ContactInfo(
                new EmailAddress(request.email()),
                new PhoneNumber(request.phone())
        );
    }

    public Address toAddress(AddressRequest request) {
        if (request == null) {
            return null;
        }
        return new Address(
                request.street(),
                request.city(),
                request.county(),
                request.postalCode(),
                request.country()
        );
    }

    private ClientType toClientType(ClientTypeDto dto) {
        if (dto == null) {
            return null;
        }
        return switch (dto) {
            case INDIVIDUAL -> ClientType.INDIVIDUAL;
            case COMPANY -> ClientType.COMPANY;
        };
    }

    private ClientTypeDto toClientTypeDto(ClientType domain) {
        if (domain == null) {
            return null;
        }
        return switch (domain) {
            case INDIVIDUAL -> ClientTypeDto.INDIVIDUAL;
            case COMPANY -> ClientTypeDto.COMPANY;
        };
    }

    private ContactInfoResponse toContactInfoResponse(ContactInfo contactInfo) {
        if (contactInfo == null) {
            return null;
        }
        return new ContactInfoResponse(
                contactInfo.getEmail(),
                contactInfo.getPhone()
        );
    }

    private AddressResponse toAddressResponse(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressResponse(
                address.street(),
                address.city(),
                address.county(),
                address.postalCode(),
                address.country()
        );
    }

    private IdentificationNumberChangeDto toHistoryDto(IdentificationNumberChangeEntity entity) {
        if (entity == null) {
            return null;
        }
        return new IdentificationNumberChangeDto(
                entity.getChangedAt(),
                entity.getChangedBy(),
                entity.getReason()
        );
    }
}

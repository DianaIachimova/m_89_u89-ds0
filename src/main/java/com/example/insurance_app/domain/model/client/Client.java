package com.example.insurance_app.domain.model.client;

import com.example.insurance_app.domain.model.client.vo.Address;
import com.example.insurance_app.domain.model.client.vo.ClientId;
import com.example.insurance_app.domain.util.DomainAssertions;
import java.util.Objects;

public class Client {

    private final ClientId id;
    private final ClientType clientType;
    private String name;
    private String identificationNumber;
    private ContactInfo contactInfo;
    private Address address;


    public Client(ClientType clientType, String name, String identificationNumber,
                  ContactInfo contactInfo, Address address) {
        this(null, clientType, name, identificationNumber, contactInfo, address);
    }


    public Client(ClientId id, ClientType clientType, String name, String identificationNumber,
                  ContactInfo contactInfo, Address address) {

        validateRequired(clientType, name, identificationNumber, contactInfo);
        this.id = id;
        this.clientType = clientType;
        this.name = DomainAssertions.normalize(name);
        this.identificationNumber = DomainAssertions.normalizeIdentificationNumber(identificationNumber);
        this.contactInfo = contactInfo;
        this.address = address;
        DomainAssertions.validateIdentificationNumber(this.clientType, this.identificationNumber);
    }

    private static void validateRequired(ClientType clientType, String name, String identificationNumber,
                                         ContactInfo contactInfo) {
        DomainAssertions.notNull(clientType, "Client type");
        DomainAssertions.notBlank(name, "Name");
        DomainAssertions.notBlank(identificationNumber, "Identification number");
        DomainAssertions.notNull(contactInfo, "Contact info");
    }

    public void updateInformation(String name, String identificationNumber, ContactInfo contactInfo,
                                  Address address) {
        DomainAssertions.notBlank(name, "Name");
        DomainAssertions.notNull(contactInfo, "Contact info");

        this.name = DomainAssertions.normalize(name);
        this.contactInfo = contactInfo;

        if(address !=null)
            this.address = address;


        String normalized = identificationNumber != null
                ? DomainAssertions.normalizeIdentificationNumber(identificationNumber) : null;

        if (normalized != null && !normalized.equals(this.identificationNumber)) {
            DomainAssertions.validateIdentificationNumber(this.clientType, normalized);
            this.identificationNumber = normalized;
        }
    }

    public ClientId getId() {
        return id;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public String getName() {
        return name;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public Address getAddress() {
        return address;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client other)) return false;
        return id!=null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

package com.example.insurance_app.infrastructure.persistence.entity.client;

import com.example.insurance_app.infrastructure.persistence.entity.geography.CityEntity;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "clients", uniqueConstraints = {
                @UniqueConstraint(name = "uk_clients_identification_number", columnNames = "identification_number")
        })
@EntityListeners(AuditingEntityListener.class)
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false)
    private ClientTypeEntity clientType;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "identification_number", nullable = false, length = 13)
    private String identificationNumber;

    @Embedded
    private ContactInfoEmbeddable contactInfo;

    @Embedded
    private AddressEmbeddable address;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public ClientEntity() {
    }

    public ClientEntity(UUID id, ClientTypeEntity clientType, String name, String identificationNumber,
                        ContactInfoEmbeddable contactInfo, AddressEmbeddable address) {
        this.id = id;
        this.clientType = clientType;
        this.name = name;
        this.identificationNumber = identificationNumber;
        this.contactInfo = contactInfo;
        this.address = address;
    }

    public UUID getId() {
        return id;
    }

    public ClientTypeEntity getClientType() {
        return clientType;
    }

    public String getName() {
        return name;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public ContactInfoEmbeddable getContactInfo() {
        return contactInfo;
    }

    public AddressEmbeddable getAddress() {
        return address;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public void setContactInfo(ContactInfoEmbeddable contactInfo) {
        this.contactInfo = contactInfo;
    }

    public void setAddress(AddressEmbeddable address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientEntity other)) return false;
        return id!=null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

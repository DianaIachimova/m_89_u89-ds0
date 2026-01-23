package com.example.insurance_app.infrastructure.persistence.entity.geography;
import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name="countries", uniqueConstraints = {
        @UniqueConstraint(name = "uq_country_name", columnNames = "name")})

public class CountryEntity {
    @Id
    @Column(name="id", nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 80)
    private String name;

    protected CountryEntity() {}

    public CountryEntity(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CountryEntity other)) return false;
        return id!=null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

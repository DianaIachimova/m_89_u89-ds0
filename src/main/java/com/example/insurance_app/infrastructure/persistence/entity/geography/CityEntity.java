package com.example.insurance_app.infrastructure.persistence.entity.geography;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name="cities", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cities_county", columnNames = {"county_id", "name"})
})

public class CityEntity {
    @Id
    @Column(name="id", nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 180)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name="county_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name="fk_cities_county")
    )
    private CountyEntity county;

    protected CityEntity() {}

    public CityEntity(String name, CountyEntity county) {
        this.name = name;
        this.county = county;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CountyEntity getCounty() {
        return county;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CityEntity other)) return false;
        return id!=null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

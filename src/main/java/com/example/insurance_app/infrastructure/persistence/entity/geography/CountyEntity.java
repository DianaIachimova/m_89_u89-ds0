package com.example.insurance_app.infrastructure.persistence.entity.geography;
import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name="counties",
        uniqueConstraints = {
        @UniqueConstraint(name = "uq_counties_country", columnNames = {"country_id", "code"})

})

public class CountyEntity {

    @Id
    @Column(name="id", nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 10)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name="country_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name="fk_counties_country")
    )
    private CountryEntity country;


    protected CountyEntity() {}

    public CountyEntity(String code, String name, CountryEntity country) {
        this.code = code;
        this.name = name;
        this.country = country;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public CountryEntity getCountry() {
        return country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CountyEntity other)) return false;
        return id!=null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

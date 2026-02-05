package com.example.insurance_app.domain.model.building.vo;

import com.example.insurance_app.domain.model.building.BuildingType;
import com.example.insurance_app.domain.util.DomainAssertions;
import java.math.BigDecimal;
import static com.example.insurance_app.domain.model.building.vo.BuildingInfoFields.*;

public record BuildingInfo(
        Integer constructionYear,
        BuildingType type,
        Integer numberOfFloors,
        BigDecimal surfaceArea,
        BigDecimal insuredValue


) {

    public BuildingInfo{
        //not null
        DomainAssertions.notNull(constructionYear, "constructionYear");
        DomainAssertions.notNull(type, "BuildingType");
        DomainAssertions.notNull(surfaceArea, SURFACE_AREA);
        DomainAssertions.notNull(insuredValue, INSURED_VALUE);

        //constructionYear
        int currentYear = java.time.Year.now().getValue();
        DomainAssertions.requireInRange(constructionYear,1800, currentYear, "constructionYear");

        //numberOfFloors
        if (numberOfFloors != null) {
            DomainAssertions.requireInRange(numberOfFloors,1, 200, "numberOfFloors");
        }

        //surfaceArea
        DomainAssertions.requirePositive(surfaceArea, SURFACE_AREA);
        DomainAssertions.requireBigDecimalFormat(surfaceArea, 8, 2, SURFACE_AREA);

        //insuredValue
        DomainAssertions.requirePositive(insuredValue, INSURED_VALUE);
        DomainAssertions.requireBigDecimalFormat(insuredValue, 13, 2,INSURED_VALUE );

    }

}

package com.example.insurance_app.application.mapper;


import com.example.insurance_app.application.dto.metadata.feeconfig.request.CreateFeeConfigRequest;
import com.example.insurance_app.application.dto.metadata.feeconfig.response.FeeConfigResponse;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeConfiguration;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeDetails;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.EffectivePeriod;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeeCode;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeeName;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeePercentage;
import org.springframework.stereotype.Component;

import static com.example.insurance_app.application.mapper.EnumDtoMapper.toFeeConfigTypeDto;
import static com.example.insurance_app.application.mapper.EnumDtoMapper.toFeeConfigurationType;

@Component
public class FeeConfigDtoMapper {

    public FeeConfiguration toDomain(CreateFeeConfigRequest req) {
        if (req == null)
            return null;

        var details = FeeDetails.of(
                new FeeCode(req.code()),
                new FeeName(req.name()),
                toFeeConfigurationType(req.type()),
                new FeePercentage(req.percentage()),
                new EffectivePeriod(req.effectiveFrom(), req.effectiveTo())
        );


        return FeeConfiguration.createNew(
                details,
                req.isActive()
        );
    }

    public FeeConfigResponse toResponse(FeeConfiguration domain) {
        if (domain == null)
            return null;

        return new FeeConfigResponse(
                domain.getId() != null ? domain.getId().value() : null,
                domain.getDetails().code().value(),
                domain.getDetails().name().value(),
                toFeeConfigTypeDto(domain.getDetails().type()),
                domain.getDetails().percentage().value(),
                domain.getDetails().period().from(),
                domain.getDetails().period().to(),
                domain.isActive(),
                domain.getAudit().createdAt(),
                domain.getAudit().updatedAt()
        );

    }

}

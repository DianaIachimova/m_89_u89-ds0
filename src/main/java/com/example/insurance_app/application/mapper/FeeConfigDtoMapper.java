package com.example.insurance_app.application.mapper;


import com.example.insurance_app.application.dto.metadata.feeconfig.FeeConfigTypeDto;
import com.example.insurance_app.application.dto.metadata.feeconfig.request.CreateFeeConfigRequest;
import com.example.insurance_app.application.dto.metadata.feeconfig.response.FeeConfigResponse;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeConfiguration;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeConfigurationType;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeDetails;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.EffectivePeriod;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeeCode;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeeName;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeePercentage;
import org.springframework.stereotype.Component;

@Component
public class FeeConfigDtoMapper {

    public FeeConfiguration toDomain(CreateFeeConfigRequest req) {
        if (req == null)
            return null;

        var details = FeeDetails.of(
                new FeeCode(req.code()),
                new FeeName(req.name()),
                toDomainType(req.type()),
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
                toDtoType(domain.getDetails().type()),
                domain.getDetails().percentage().value(),
                domain.getDetails().period().from(),
                domain.getDetails().period().to(),
                domain.isActive(),
                domain.getAudit().createdAt(),
                domain.getAudit().updatedAt()
        );


    }



    public FeeConfigurationType toDomainType(FeeConfigTypeDto dto) {
        if (dto == null) {
            return null;
        }
        return switch (dto) {
            case BROKER_COMMISSION -> FeeConfigurationType.BROKER_COMMISSION;
            case RISK_ADJUSTMENT -> FeeConfigurationType.RISK_ADJUSTMENT;
            case ADMIN_FEE -> FeeConfigurationType.ADMIN_FEE;
        };
    }

    public FeeConfigTypeDto toDtoType(FeeConfigurationType domain) {
        if (domain == null) {
            return null;
        }
        return switch (domain) {
            case BROKER_COMMISSION -> FeeConfigTypeDto.BROKER_COMMISSION;
            case RISK_ADJUSTMENT -> FeeConfigTypeDto.RISK_ADJUSTMENT;
            case ADMIN_FEE -> FeeConfigTypeDto.ADMIN_FEE;
        };
    }
}

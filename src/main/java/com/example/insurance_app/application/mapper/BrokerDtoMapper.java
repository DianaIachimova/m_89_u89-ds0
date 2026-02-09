package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.broker.request.CreateBrokerRequest;
import com.example.insurance_app.application.dto.broker.response.BrokerResponse;
import com.example.insurance_app.domain.model.broker.Broker;
import com.example.insurance_app.domain.model.broker.BrokerStatus;
import com.example.insurance_app.domain.model.broker.vo.BrokerCode;
import com.example.insurance_app.domain.model.broker.vo.BrokerName;
import com.example.insurance_app.domain.model.broker.vo.CommissionPercentage;
import com.example.insurance_app.domain.model.broker.vo.ContactInfo;
import com.example.insurance_app.domain.model.client.vo.EmailAddress;
import com.example.insurance_app.domain.model.client.vo.PhoneNumber;
import org.springframework.stereotype.Component;

@Component
public class BrokerDtoMapper {

    public Broker toDomain(CreateBrokerRequest request) {
        if (request == null) return null;

        ContactInfo contactInfo = new ContactInfo(
                new EmailAddress(request.email()),
                request.phone() != null ? new PhoneNumber(request.phone()) : null
        );

        return Broker.createNew(
                new BrokerCode(request.brokerCode()),
                new BrokerName(request.name()),
                contactInfo,
                Boolean.TRUE.equals(request.active()) ? BrokerStatus.ACTIVE : BrokerStatus.INACTIVE,
                request.commissionPercentage() != null
                        ? new CommissionPercentage(request.commissionPercentage())
                        : null
        );
    }

    public BrokerResponse toResponse(Broker domain) {
        if (domain == null) return null;

        return new BrokerResponse(
                domain.getId() != null ? domain.getId().value() : null,
                domain.getCode().value(),
                domain.getName().value(),
                domain.getContactInfo().email().value(),
                domain.getContactInfo().phone() != null ? domain.getContactInfo().phone().value() : null,
                domain.getStatus().name(),
                domain.getCommissionPercentage() != null ? domain.getCommissionPercentage().value() : null,
                domain.getAudit().createdAt(),
                domain.getAudit().updatedAt()
        );
    }
}

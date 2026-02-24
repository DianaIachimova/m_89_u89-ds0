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
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BrokerDtoMapper {

    default Broker toDomain(CreateBrokerRequest request) {
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
                request.commissionPercentage() != null ? new CommissionPercentage(request.commissionPercentage()) : null
        );
    }

    @Mapping(target = "id", expression = "java(domain.getId() != null ? domain.getId().value() : null)")
    @Mapping(target = "brokerCode", source = "code.value")
    @Mapping(target = "name", source = "name.value")
    @Mapping(target = "email", source = "contactInfo.email.value")
    @Mapping(target = "phone", expression = "java(domain.getContactInfo().phone() != null ? domain.getContactInfo().phone().value() : null)")
    @Mapping(target = "status", expression = "java(domain.getStatus().name())")
    @Mapping(target = "commissionPercentage", expression = "java(domain.getCommissionPercentage() != null ? domain.getCommissionPercentage().value() : null)")
    @Mapping(target = "createdAt", source = "audit.createdAt")
    @Mapping(target = "updatedAt", source = "audit.updatedAt")
    BrokerResponse toResponse(Broker domain);
}

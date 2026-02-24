package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.AuditInfo;
import com.example.insurance_app.domain.model.broker.Broker;
import com.example.insurance_app.domain.model.broker.BrokerStatus;
import com.example.insurance_app.domain.model.broker.vo.*;
import com.example.insurance_app.domain.model.client.vo.EmailAddress;
import com.example.insurance_app.domain.model.client.vo.PhoneNumber;
import com.example.insurance_app.infrastructure.persistence.entity.broker.BrokerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BrokerEntityMapper {

    default Broker toDomain(BrokerEntity entity) {
        if (entity == null) return null;
        ContactInfo contactInfo = new ContactInfo(
                new EmailAddress(entity.getEmail()),
                entity.getPhone() != null ? new PhoneNumber(entity.getPhone()) : null
        );
        return Broker.rehydrate(
                new BrokerId(entity.getId()),
                new BrokerCode(entity.getBrokerCode()),
                new BrokerName(entity.getName()),
                contactInfo,
                BrokerStatus.valueOf(entity.getStatus()),
                entity.getCommissionPercentage() != null ? new CommissionPercentage(entity.getCommissionPercentage()) : null,
                new AuditInfo(entity.getCreatedAt(), entity.getUpdatedAt())
        );
    }

    @Mapping(target = "id", expression = "java(domain.getId() != null ? domain.getId().value() : null)")
    @Mapping(target = "brokerCode", source = "code.value")
    @Mapping(target = "name", source = "name.value")
    @Mapping(target = "email", source = "contactInfo.email.value")
    @Mapping(target = "phone", expression = "java(domain.getContactInfo().phone() != null ? domain.getContactInfo().phone().value() : null)")
    @Mapping(target = "status", expression = "java(domain.getStatus().name())")
    @Mapping(target = "commissionPercentage", expression = "java(domain.getCommissionPercentage() != null ? domain.getCommissionPercentage().value() : null)")
    BrokerEntity toEntity(Broker domain);

    default void updateEntity(Broker domain, BrokerEntity entity) {
        if (domain == null || entity == null) return;
        entity.setName(domain.getName().value());
        entity.setEmail(domain.getContactInfo().email().value());
        entity.setPhone(domain.getContactInfo().phone() != null ? domain.getContactInfo().phone().value() : null);
        entity.setStatus(domain.getStatus().name());
        entity.setCommissionPercentage(domain.getCommissionPercentage() != null ? domain.getCommissionPercentage().value() : null);
    }
}

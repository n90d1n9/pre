package tech.kayys.erp.integration.infrastructure.persistence.mapper;

import tech.kayys.erp.integration.domain.identifier.IntegrationId;
import tech.kayys.erp.integration.domain.model.Integration;
import tech.kayys.erp.integration.domain.valueobject.IntegrationStatus;
import tech.kayys.erp.integration.domain.valueobject.IntegrationType;
import tech.kayys.erp.integration.infrastructure.persistence.entity.IntegrationEntity;

import java.time.Instant;

/**
 * Mapper between Integration domain model and JPA entity.
 */
public class IntegrationMapper {

    public static Integration toDomain(IntegrationEntity entity) {
        if (entity == null) {
            return null;
        }
        
        IntegrationId id = IntegrationId.of(entity.getId());
        Integration integration = Integration.create(
            id,
            entity.getCode(),
            entity.getName(),
            IntegrationType.valueOf(entity.getType()),
            entity.getBaseUrl(),
            entity.getAuthType()
        );
        
        integration.setDescription(entity.getDescription());
        integration.setStatus(IntegrationStatus.valueOf(entity.getStatus()));
        integration.setTimeout(entity.getTimeoutSeconds(), entity.getRetryCount(), entity.getRetryDelaySeconds());
        integration.setVersion(entity.getVersion());
        integration.setNotes(entity.getNotes());
        integration.setActive(entity.isActive());
        
        return integration;
    }

    public static IntegrationEntity toEntity(Integration integration) {
        if (integration == null) {
            return null;
        }
        
        IntegrationEntity entity = new IntegrationEntity();
        entity.setId(integration.getId().getValue());
        entity.setCode(integration.getCode());
        entity.setName(integration.getName());
        entity.setDescription(integration.getDescription());
        entity.setType(integration.getType().name());
        entity.setStatus(integration.getStatus().name());
        entity.setBaseUrl(integration.getBaseUrl());
        entity.setAuthType(integration.getAuthType());
        entity.setTimeoutSeconds(integration.getTimeoutSeconds());
        entity.setRetryCount(integration.getRetryCount());
        entity.setRetryDelaySeconds(integration.getRetryDelaySeconds());
        entity.setVersion(integration.getVersion());
        entity.setNotes(integration.getNotes());
        entity.setActive(integration.isActive());
        entity.setCreatedAt(Instant.now());
        
        return entity;
    }
}

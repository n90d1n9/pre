package tech.kayys.syirkah.workforce.domain.compliance.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.compliance.ComplianceRequirementId;
import tech.kayys.syirkah.workforce.domain.compliance.ComplianceRequirementType;

import java.time.Instant;
import java.util.UUID;

public record ComplianceRequirementCreated(
        UUID eventId,
        Instant occurredAt,
        ComplianceRequirementId id,
        TenantId tenantId,
        String code,
        ComplianceRequirementType type
) implements DomainEvent {
    public ComplianceRequirementCreated(ComplianceRequirementId id, TenantId tenantId, String code, ComplianceRequirementType type) {
        this(UUID.randomUUID(), Instant.now(), id, tenantId, code, type);
    }

    @Override
    public String eventType() {
        return "workforce.compliance.requirement.created";
    }
}

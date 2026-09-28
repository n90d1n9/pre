package tech.kayys.syirkah.workforce.domain.payroll.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.payroll.PayrollPeriodId;
import tech.kayys.syirkah.workforce.domain.payroll.PayrollRunId;

import java.time.Instant;
import java.util.UUID;

public record PayrollRunCreated(
        UUID eventId,
        Instant occurredAt,
        PayrollRunId runId,
        PayrollPeriodId periodId,
        TenantId tenantId
) implements DomainEvent {
    public PayrollRunCreated(PayrollRunId runId, PayrollPeriodId periodId, TenantId tenantId) {
        this(UUID.randomUUID(), Instant.now(), runId, periodId, tenantId);
    }

    @Override
    public String eventType() { return "workforce.payroll.run-created"; }
}

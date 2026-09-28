package tech.kayys.syirkah.workforce.domain.payroll.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.payroll.PayrollPeriodId;
import tech.kayys.syirkah.workforce.domain.payroll.PayrollRunId;

import java.time.Instant;
import java.util.UUID;

public record PayrollRunApproved(
        UUID eventId,
        Instant occurredAt,
        PayrollRunId runId,
        PayrollPeriodId periodId
) implements DomainEvent {
    public PayrollRunApproved(PayrollRunId runId, PayrollPeriodId periodId) {
        this(UUID.randomUUID(), Instant.now(), runId, periodId);
    }

    @Override
    public String eventType() { return "workforce.payroll.run-approved"; }
}

package tech.kayys.syirkah.workforce.domain.payslip.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.payslip.PayslipId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record PayslipApproved(
        UUID eventId,
        Instant occurredAt,
        PayslipId payslipId,
        WorkerId workerId
) implements DomainEvent {
    public PayslipApproved(PayslipId payslipId, WorkerId workerId) {
        this(UUID.randomUUID(), Instant.now(), payslipId, workerId);
    }

    @Override
    public String eventType() { return "workforce.payroll.payslip-approved"; }
}

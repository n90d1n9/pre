package tech.kayys.syirkah.workforce.domain.payslip.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.payroll.PayrollPeriodId;
import tech.kayys.syirkah.workforce.domain.payroll.PayrollRunId;
import tech.kayys.syirkah.workforce.domain.payslip.PayslipId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record PayslipGenerated(
        UUID eventId,
        Instant occurredAt,
        PayslipId payslipId,
        PayrollRunId payrollRunId,
        PayrollPeriodId periodId,
        WorkerId workerId,
        EmploymentId employmentId
) implements DomainEvent {
    public PayslipGenerated(
            PayslipId payslipId,
            PayrollRunId payrollRunId,
            PayrollPeriodId periodId,
            WorkerId workerId,
            EmploymentId employmentId
    ) {
        this(UUID.randomUUID(), Instant.now(), payslipId, payrollRunId, periodId, workerId, employmentId);
    }

    @Override
    public String eventType() { return "workforce.payroll.payslip-generated"; }
}

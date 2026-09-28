package tech.kayys.syirkah.workforce.domain.timesheet.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.timesheet.TimesheetId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TimesheetCreated(
        UUID eventId,
        Instant occurredAt,
        TimesheetId timesheetId,
        WorkerId workerId,
        EmploymentId employmentId,
        LocalDate periodStart,
        LocalDate periodEnd
) implements DomainEvent {
    public TimesheetCreated(TimesheetId id, WorkerId workerId, EmploymentId employmentId,
                            LocalDate periodStart, LocalDate periodEnd) {
        this(UUID.randomUUID(), Instant.now(), id, workerId, employmentId, periodStart, periodEnd);
    }
    @Override public String eventType() { return "workforce.timesheet.created"; }
}

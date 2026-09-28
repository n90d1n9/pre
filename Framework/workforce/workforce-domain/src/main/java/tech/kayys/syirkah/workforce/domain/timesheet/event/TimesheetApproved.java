package tech.kayys.syirkah.workforce.domain.timesheet.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.timesheet.TimesheetId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record TimesheetApproved(
        UUID eventId,
        Instant occurredAt,
        TimesheetId timesheetId,
        WorkerId workerId,
        String approvedBy
) implements DomainEvent {
    public TimesheetApproved(TimesheetId id, WorkerId workerId, String approvedBy) {
        this(UUID.randomUUID(), Instant.now(), id, workerId, approvedBy);
    }
    @Override public String eventType() { return "workforce.timesheet.approved"; }
}

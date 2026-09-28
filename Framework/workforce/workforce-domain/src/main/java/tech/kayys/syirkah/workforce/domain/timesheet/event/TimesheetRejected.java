package tech.kayys.syirkah.workforce.domain.timesheet.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.timesheet.TimesheetId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record TimesheetRejected(
        UUID eventId,
        Instant occurredAt,
        TimesheetId timesheetId,
        WorkerId workerId,
        String rejectedBy,
        String reason
) implements DomainEvent {
    public TimesheetRejected(TimesheetId id, WorkerId workerId, String rejectedBy, String reason) {
        this(UUID.randomUUID(), Instant.now(), id, workerId, rejectedBy, reason);
    }
    @Override public String eventType() { return "workforce.timesheet.rejected"; }
}

package tech.kayys.syirkah.workforce.domain.timesheet.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.timesheet.TimesheetId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public record TimesheetSubmitted(
        UUID eventId,
        Instant occurredAt,
        TimesheetId timesheetId,
        WorkerId workerId,
        Duration totalDuration
) implements DomainEvent {
    public TimesheetSubmitted(TimesheetId id, WorkerId workerId, Duration totalDuration) {
        this(UUID.randomUUID(), Instant.now(), id, workerId, totalDuration);
    }
    @Override public String eventType() { return "workforce.timesheet.submitted"; }
}

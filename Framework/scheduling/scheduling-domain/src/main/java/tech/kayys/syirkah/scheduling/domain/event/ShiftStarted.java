package tech.kayys.syirkah.scheduling.domain.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.foundation.domain.ref.ResourceRef;
import tech.kayys.syirkah.scheduling.domain.ScheduleId;
import tech.kayys.syirkah.scheduling.domain.ShiftId;

import java.time.Instant;
import java.util.UUID;

public record ShiftStarted(
        UUID eventId,
        Instant occurredAt,
        ShiftId shiftId,
        ScheduleId scheduleId,
        ResourceRef assignee
) implements DomainEvent {
    public ShiftStarted(ShiftId shiftId, ScheduleId scheduleId, ResourceRef assignee) {
        this(UUID.randomUUID(), Instant.now(), shiftId, scheduleId, assignee);
    }
    @Override
    public String eventType() {
        return "scheduling.shift.started";
    }
}

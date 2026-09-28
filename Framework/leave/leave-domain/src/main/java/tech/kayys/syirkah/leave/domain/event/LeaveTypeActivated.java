package tech.kayys.syirkah.leave.domain.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.leave.domain.LeaveTypeId;

import java.time.Instant;
import java.util.UUID;

public record LeaveTypeActivated(
        UUID eventId,
        Instant occurredAt,
        LeaveTypeId leaveTypeId
) implements DomainEvent {
    public LeaveTypeActivated(LeaveTypeId leaveTypeId) {
        this(UUID.randomUUID(), Instant.now(), leaveTypeId);
    }
    @Override
    public String eventType() {
        return "leave.type.activated";
    }
}

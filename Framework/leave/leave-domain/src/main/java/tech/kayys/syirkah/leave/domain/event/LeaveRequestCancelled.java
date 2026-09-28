package tech.kayys.syirkah.leave.domain.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.leave.domain.LeaveRequestId;

import java.time.Instant;
import java.util.UUID;

public record LeaveRequestCancelled(
        UUID eventId,
        Instant occurredAt,
        LeaveRequestId requestId,
        UUID subjectId,
        String cancelReason
) implements DomainEvent {
    public LeaveRequestCancelled(LeaveRequestId requestId, UUID subjectId, String cancelReason) {
        this(UUID.randomUUID(), Instant.now(), requestId, subjectId, cancelReason);
    }
    @Override
    public String eventType() {
        return "leave.request.cancelled";
    }
}

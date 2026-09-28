package tech.kayys.syirkah.leave.domain.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.leave.domain.LeaveRequestId;

import java.time.Instant;
import java.util.UUID;

public record LeaveRequestApproved(
        UUID eventId,
        Instant occurredAt,
        LeaveRequestId requestId,
        UUID subjectId,
        String approvedBy
) implements DomainEvent {
    public LeaveRequestApproved(LeaveRequestId requestId, UUID subjectId, String approvedBy) {
        this(UUID.randomUUID(), Instant.now(), requestId, subjectId, approvedBy);
    }
    @Override
    public String eventType() {
        return "leave.request.approved";
    }
}

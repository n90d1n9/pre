package tech.kayys.syirkah.workforce.domain.position.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.position.PositionAssignmentId;
import tech.kayys.syirkah.workforce.domain.position.PositionId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PositionAssigned(
        UUID eventId,
        Instant occurredAt,
        PositionAssignmentId assignmentId,
        WorkerId workerId,
        EmploymentId employmentId,
        PositionId positionId,
        LocalDate startDate
) implements DomainEvent {

    public PositionAssigned(
            PositionAssignmentId assignmentId,
            WorkerId workerId,
            EmploymentId employmentId,
            PositionId positionId,
            LocalDate startDate
    ) {
        this(UUID.randomUUID(), Instant.now(), assignmentId, workerId, employmentId, positionId, startDate);
    }

    @Override
    public String eventType() {
        return "workforce.position-assignment.assigned";
    }
}

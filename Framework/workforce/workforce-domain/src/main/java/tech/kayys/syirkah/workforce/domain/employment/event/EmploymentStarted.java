package tech.kayys.syirkah.workforce.domain.employment.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentType;
import tech.kayys.syirkah.workforce.domain.employment.OrganizationRef;
import tech.kayys.syirkah.workforce.domain.employment.PositionRef;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EmploymentStarted(
        UUID eventId,
        Instant occurredAt,
        EmploymentId employmentId,
        WorkerId workerId,
        OrganizationRef organization,
        PositionRef position,
        EmploymentType type,
        LocalDate startDate
) implements DomainEvent {

    public EmploymentStarted(
            EmploymentId employmentId,
            WorkerId workerId,
            OrganizationRef organization,
            PositionRef position,
            EmploymentType type,
            LocalDate startDate
    ) {
        this(UUID.randomUUID(), Instant.now(), employmentId, workerId, organization, position, type, startDate);
    }

    @Override
    public String eventType() {
        return "workforce.employment.started";
    }
}

package tech.kayys.syirkah.workforce.domain.qualification.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.qualification.QualificationId;

import java.time.Instant;
import java.util.UUID;

public record QualificationDeactivated(
        UUID eventId,
        Instant occurredAt,
        QualificationId qualificationId
) implements DomainEvent {

    public QualificationDeactivated(QualificationId qualificationId) {
        this(UUID.randomUUID(), Instant.now(), qualificationId);
    }

    @Override
    public String eventType() {
        return "workforce.qualification.deactivated";
    }
}

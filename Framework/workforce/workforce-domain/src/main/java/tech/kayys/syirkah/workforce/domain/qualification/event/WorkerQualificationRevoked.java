package tech.kayys.syirkah.workforce.domain.qualification.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.qualification.QualificationId;
import tech.kayys.syirkah.workforce.domain.qualification.WorkerQualificationId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record WorkerQualificationRevoked(
        UUID eventId,
        Instant occurredAt,
        WorkerQualificationId workerQualificationId,
        WorkerId workerId,
        QualificationId qualificationId
) implements DomainEvent {

    public WorkerQualificationRevoked(WorkerQualificationId workerQualificationId,
                                       WorkerId workerId, QualificationId qualificationId) {
        this(UUID.randomUUID(), Instant.now(), workerQualificationId, workerId, qualificationId);
    }

    @Override
    public String eventType() {
        return "workforce.worker-qualification.revoked";
    }
}

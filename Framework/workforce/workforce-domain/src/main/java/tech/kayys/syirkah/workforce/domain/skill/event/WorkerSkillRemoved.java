package tech.kayys.syirkah.workforce.domain.skill.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.skill.SkillId;
import tech.kayys.syirkah.workforce.domain.skill.WorkerSkillId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record WorkerSkillRemoved(
        UUID eventId,
        Instant occurredAt,
        WorkerSkillId workerSkillId,
        WorkerId workerId,
        SkillId skillId
) implements DomainEvent {

    public WorkerSkillRemoved(WorkerSkillId workerSkillId, WorkerId workerId, SkillId skillId) {
        this(UUID.randomUUID(), Instant.now(), workerSkillId, workerId, skillId);
    }

    @Override
    public String eventType() {
        return "workforce.worker-skill.removed";
    }
}

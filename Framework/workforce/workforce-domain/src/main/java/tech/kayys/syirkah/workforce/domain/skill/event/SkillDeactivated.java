package tech.kayys.syirkah.workforce.domain.skill.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.skill.SkillId;

import java.time.Instant;
import java.util.UUID;

public record SkillDeactivated(
        UUID eventId,
        Instant occurredAt,
        SkillId skillId
) implements DomainEvent {

    public SkillDeactivated(SkillId skillId) {
        this(UUID.randomUUID(), Instant.now(), skillId);
    }

    @Override
    public String eventType() {
        return "workforce.skill.deactivated";
    }
}

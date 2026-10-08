package tech.kayys.syirkah.workforce.domain.learning;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;

import java.util.Objects;
import java.util.UUID;

public record LearningProgramId(UUID value) implements DomainId<UUID> {
    public LearningProgramId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static LearningProgramId generate() {
        return new LearningProgramId(UUID.randomUUID());
    }

    public static LearningProgramId of(UUID value) {
        return new LearningProgramId(value);
    }
}

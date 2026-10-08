package tech.kayys.syirkah.workforce.domain.learning;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;

import java.util.Objects;
import java.util.UUID;

public record LearningAssessmentId(UUID value) implements DomainId<UUID> {
    public LearningAssessmentId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static LearningAssessmentId generate() {
        return new LearningAssessmentId(UUID.randomUUID());
    }

    public static LearningAssessmentId of(UUID value) {
        return new LearningAssessmentId(value);
    }
}

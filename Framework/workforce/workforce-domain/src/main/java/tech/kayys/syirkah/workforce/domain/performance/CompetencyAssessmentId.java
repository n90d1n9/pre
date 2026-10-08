package tech.kayys.syirkah.workforce.domain.performance;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;

import java.util.Objects;
import java.util.UUID;

public record CompetencyAssessmentId(UUID value) implements DomainId<UUID> {
    public CompetencyAssessmentId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static CompetencyAssessmentId generate() {
        return new CompetencyAssessmentId(UUID.randomUUID());
    }

    public static CompetencyAssessmentId of(UUID value) {
        return new CompetencyAssessmentId(value);
    }
}

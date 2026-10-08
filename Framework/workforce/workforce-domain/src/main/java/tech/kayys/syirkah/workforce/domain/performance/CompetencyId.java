package tech.kayys.syirkah.workforce.domain.performance;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;

import java.util.Objects;
import java.util.UUID;

public record CompetencyId(UUID value) implements DomainId<UUID> {
    public CompetencyId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static CompetencyId generate() {
        return new CompetencyId(UUID.randomUUID());
    }

    public static CompetencyId of(UUID value) {
        return new CompetencyId(value);
    }
}

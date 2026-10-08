package tech.kayys.syirkah.workforce.domain.performance;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;

import java.util.Objects;
import java.util.UUID;

public record PerformanceCycleId(UUID value) implements DomainId<UUID> {
    public PerformanceCycleId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static PerformanceCycleId generate() {
        return new PerformanceCycleId(UUID.randomUUID());
    }

    public static PerformanceCycleId of(UUID value) {
        return new PerformanceCycleId(value);
    }
}

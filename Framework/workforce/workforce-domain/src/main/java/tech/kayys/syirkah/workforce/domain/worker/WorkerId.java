package tech.kayys.syirkah.workforce.domain.worker;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;

import java.util.Objects;
import java.util.UUID;

/**
 * Strongly typed identifier for a Worker aggregate.
 */
public record WorkerId(UUID value) implements DomainId<UUID> {

    public WorkerId {
        Objects.requireNonNull(value, "Worker ID must not be null");
    }

    public static WorkerId generate() {
        return new WorkerId(UUID.randomUUID());
    }

    public static WorkerId of(UUID value) {
        return new WorkerId(value);
    }
}

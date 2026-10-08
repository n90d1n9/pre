package tech.kayys.syirkah.workforce.domain.analytics;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;

import java.util.Objects;
import java.util.UUID;

public record WorkforceMetricDefinitionId(UUID value) implements DomainId<UUID> {
    public WorkforceMetricDefinitionId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static WorkforceMetricDefinitionId generate() {
        return new WorkforceMetricDefinitionId(UUID.randomUUID());
    }

    public static WorkforceMetricDefinitionId of(UUID value) {
        return new WorkforceMetricDefinitionId(value);
    }
}

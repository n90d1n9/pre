package tech.kayys.syirkah.workforce.domain.talent;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;

import java.util.Objects;
import java.util.UUID;

public record SuccessionPlanId(UUID value) implements DomainId<UUID> {
    public SuccessionPlanId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static SuccessionPlanId generate() {
        return new SuccessionPlanId(UUID.randomUUID());
    }

    public static SuccessionPlanId of(UUID value) {
        return new SuccessionPlanId(value);
    }
}

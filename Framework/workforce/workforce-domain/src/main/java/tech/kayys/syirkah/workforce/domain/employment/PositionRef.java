package tech.kayys.syirkah.workforce.domain.employment;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;

import java.util.Objects;
import java.util.UUID;

/**
 * Reference to a Position definition.
 */
public record PositionRef(UUID value) implements DomainId<UUID> {

    public PositionRef {
        Objects.requireNonNull(value, "Position ID cannot be null");
    }

    public static PositionRef of(UUID value) {
        return new PositionRef(value);
    }
}

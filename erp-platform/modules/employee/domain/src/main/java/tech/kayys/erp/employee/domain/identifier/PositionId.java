package tech.kayys.erp.employee.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Position identifier.
 */
public final class PositionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public PositionId(UUID value) {
        super(value);
    }

    public static PositionId of(UUID value) {
        return new PositionId(value);
    }

    public static PositionId generate() {
        return new PositionId(UUID.randomUUID());
    }

    public static PositionId fromString(String value) {
        return new PositionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PositionId{" + value + "}";
    }
}
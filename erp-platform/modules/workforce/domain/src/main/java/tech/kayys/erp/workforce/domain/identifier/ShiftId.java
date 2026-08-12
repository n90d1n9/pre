package tech.kayys.erp.workforce.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Shift identifier.
 */
public final class ShiftId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ShiftId(UUID value) {
        super(value);
    }

    public static ShiftId of(UUID value) {
        return new ShiftId(value);
    }

    public static ShiftId generate() {
        return new ShiftId(UUID.randomUUID());
    }

    public static ShiftId fromString(String value) {
        return new ShiftId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ShiftId{" + value + "}";
    }
}
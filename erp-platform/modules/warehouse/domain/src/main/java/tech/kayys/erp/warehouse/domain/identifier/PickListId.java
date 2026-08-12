package tech.kayys.erp.warehouse.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Pick list identifier.
 */
public final class PickListId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public PickListId(UUID value) {
        super(value);
    }

    public static PickListId of(UUID value) {
        return new PickListId(value);
    }

    public static PickListId generate() {
        return new PickListId(UUID.randomUUID());
    }

    public static PickListId fromString(String value) {
        return new PickListId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PickListId{" + value + "}";
    }
}
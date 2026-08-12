package tech.kayys.erp.warehouse.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Putaway task identifier.
 */
public final class PutawayTaskId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public PutawayTaskId(UUID value) {
        super(value);
    }

    public static PutawayTaskId of(UUID value) {
        return new PutawayTaskId(value);
    }

    public static PutawayTaskId generate() {
        return new PutawayTaskId(UUID.randomUUID());
    }

    public static PutawayTaskId fromString(String value) {
        return new PutawayTaskId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PutawayTaskId{" + value + "}";
    }
}
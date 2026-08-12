package tech.kayys.erp.warehouse.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Receiving task identifier.
 */
public final class ReceivingTaskId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ReceivingTaskId(UUID value) {
        super(value);
    }

    public static ReceivingTaskId of(UUID value) {
        return new ReceivingTaskId(value);
    }

    public static ReceivingTaskId generate() {
        return new ReceivingTaskId(UUID.randomUUID());
    }

    public static ReceivingTaskId fromString(String value) {
        return new ReceivingTaskId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ReceivingTaskId{" + value + "}";
    }
}
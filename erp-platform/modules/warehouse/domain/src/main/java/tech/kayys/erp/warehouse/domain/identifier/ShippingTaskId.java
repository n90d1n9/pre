package tech.kayys.erp.warehouse.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Shipping task identifier.
 */
public final class ShippingTaskId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ShippingTaskId(UUID value) {
        super(value);
    }

    public static ShippingTaskId of(UUID value) {
        return new ShippingTaskId(value);
    }

    public static ShippingTaskId generate() {
        return new ShippingTaskId(UUID.randomUUID());
    }

    public static ShippingTaskId fromString(String value) {
        return new ShippingTaskId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ShippingTaskId{" + value + "}";
    }
}
package tech.kayys.erp.inventory.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Warehouse identifier.
 */
public final class WarehouseId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public WarehouseId(UUID value) {
        super(value);
    }

    public static WarehouseId of(UUID value) {
        return new WarehouseId(value);
    }

    public static WarehouseId generate() {
        return new WarehouseId(UUID.randomUUID());
    }

    public static WarehouseId fromString(String value) {
        return new WarehouseId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "WarehouseId{" + value + "}";
    }
}
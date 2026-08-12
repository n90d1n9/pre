package tech.kayys.erp.warehouse.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Inventory movement identifier.
 */
public final class InventoryMovementId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public InventoryMovementId(UUID value) {
        super(value);
    }

    public static InventoryMovementId of(UUID value) {
        return new InventoryMovementId(value);
    }

    public static InventoryMovementId generate() {
        return new InventoryMovementId(UUID.randomUUID());
    }

    public static InventoryMovementId fromString(String value) {
        return new InventoryMovementId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "InventoryMovementId{" + value + "}";
    }
}
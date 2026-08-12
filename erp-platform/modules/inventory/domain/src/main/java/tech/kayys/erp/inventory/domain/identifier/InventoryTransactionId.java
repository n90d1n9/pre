package tech.kayys.erp.inventory.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Inventory transaction identifier.
 */
public final class InventoryTransactionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public InventoryTransactionId(UUID value) {
        super(value);
    }

    public static InventoryTransactionId of(UUID value) {
        return new InventoryTransactionId(value);
    }

    public static InventoryTransactionId generate() {
        return new InventoryTransactionId(UUID.randomUUID());
    }

    public static InventoryTransactionId fromString(String value) {
        return new InventoryTransactionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "InventoryTransactionId{" + value + "}";
    }
}
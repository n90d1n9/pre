package tech.kayys.erp.purchasing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Purchase Order identifier.
 */
public final class PurchaseOrderId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public PurchaseOrderId(UUID value) {
        super(value);
    }

    public static PurchaseOrderId of(UUID value) {
        return new PurchaseOrderId(value);
    }

    public static PurchaseOrderId generate() {
        return new PurchaseOrderId(UUID.randomUUID());
    }

    public static PurchaseOrderId fromString(String value) {
        return new PurchaseOrderId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PurchaseOrderId{" + value + "}";
    }
}
package tech.kayys.erp.purchasing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class PurchaseRequisitionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public PurchaseRequisitionId(UUID value) {
        super(value);
    }

    public static PurchaseRequisitionId of(UUID value) {
        return new PurchaseRequisitionId(value);
    }

    public static PurchaseRequisitionId generate() {
        return new PurchaseRequisitionId(UUID.randomUUID());
    }

    public static PurchaseRequisitionId fromString(String value) {
        return new PurchaseRequisitionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PurchaseRequisitionId{" + value + "}";
    }
}
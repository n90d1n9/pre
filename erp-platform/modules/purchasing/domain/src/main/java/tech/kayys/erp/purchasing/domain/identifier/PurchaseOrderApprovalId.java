package tech.kayys.erp.purchasing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class PurchaseOrderApprovalId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public PurchaseOrderApprovalId(UUID value) {
        super(value);
    }

    public static PurchaseOrderApprovalId of(UUID value) {
        return new PurchaseOrderApprovalId(value);
    }

    public static PurchaseOrderApprovalId generate() {
        return new PurchaseOrderApprovalId(UUID.randomUUID());
    }

    public static PurchaseOrderApprovalId fromString(String value) {
        return new PurchaseOrderApprovalId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PurchaseOrderApprovalId{" + value + "}";
    }
}
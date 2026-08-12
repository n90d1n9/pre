package tech.kayys.erp.billing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Invoice batch identifier for batch billing.
 */
public final class InvoiceBatchId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public InvoiceBatchId(UUID value) {
        super(value);
    }

    public static InvoiceBatchId of(UUID value) {
        return new InvoiceBatchId(value);
    }

    public static InvoiceBatchId generate() {
        return new InvoiceBatchId(UUID.randomUUID());
    }

    public static InvoiceBatchId fromString(String value) {
        return new InvoiceBatchId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "InvoiceBatchId{" + value + "}";
    }
}
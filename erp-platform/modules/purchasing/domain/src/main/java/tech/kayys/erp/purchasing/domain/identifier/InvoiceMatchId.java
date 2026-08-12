package tech.kayys.erp.purchasing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class InvoiceMatchId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public InvoiceMatchId(UUID value) {
        super(value);
    }

    public static InvoiceMatchId of(UUID value) {
        return new InvoiceMatchId(value);
    }

    public static InvoiceMatchId generate() {
        return new InvoiceMatchId(UUID.randomUUID());
    }

    public static InvoiceMatchId fromString(String value) {
        return new InvoiceMatchId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "InvoiceMatchId{" + value + "}";
    }
}
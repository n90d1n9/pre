package tech.kayys.erp.billing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class CreditNoteId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public CreditNoteId(UUID value) {
        super(value);
    }

    public static CreditNoteId of(UUID value) {
        return new CreditNoteId(value);
    }

    public static CreditNoteId generate() {
        return new CreditNoteId(UUID.randomUUID());
    }

    public static CreditNoteId fromString(String value) {
        return new CreditNoteId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CreditNoteId{" + value + "}";
    }
}
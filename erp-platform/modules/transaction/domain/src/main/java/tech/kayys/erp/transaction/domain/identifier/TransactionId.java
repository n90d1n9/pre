package tech.kayys.erp.transaction.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Transaction identifier for financial transactions.
 */
public final class TransactionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public TransactionId(UUID value) {
        super(value);
    }

    public static TransactionId of(UUID value) {
        return new TransactionId(value);
    }

    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }

    public static TransactionId fromString(String value) {
        return new TransactionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "TransactionId{" + value + "}";
    }
}
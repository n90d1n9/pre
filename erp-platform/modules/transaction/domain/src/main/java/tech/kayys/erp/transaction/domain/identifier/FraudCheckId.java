package tech.kayys.erp.transaction.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class FraudCheckId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public FraudCheckId(UUID value) {
        super(value);
    }

    public static FraudCheckId of(UUID value) {
        return new FraudCheckId(value);
    }

    public static FraudCheckId generate() {
        return new FraudCheckId(UUID.randomUUID());
    }

    public static FraudCheckId fromString(String value) {
        return new FraudCheckId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "FraudCheckId{" + value + "}";
    }
}
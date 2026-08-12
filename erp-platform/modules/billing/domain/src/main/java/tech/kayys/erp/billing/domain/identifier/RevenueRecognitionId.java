package tech.kayys.erp.billing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class RevenueRecognitionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public RevenueRecognitionId(UUID value) {
        super(value);
    }

    public static RevenueRecognitionId of(UUID value) {
        return new RevenueRecognitionId(value);
    }

    public static RevenueRecognitionId generate() {
        return new RevenueRecognitionId(UUID.randomUUID());
    }

    public static RevenueRecognitionId fromString(String value) {
        return new RevenueRecognitionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "RevenueRecognitionId{" + value + "}";
    }
}
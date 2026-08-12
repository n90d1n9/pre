package tech.kayys.erp.transaction.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class DisputeId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public DisputeId(UUID value) {
        super(value);
    }

    public static DisputeId of(UUID value) {
        return new DisputeId(value);
    }

    public static DisputeId generate() {
        return new DisputeId(UUID.randomUUID());
    }

    public static DisputeId fromString(String value) {
        return new DisputeId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "DisputeId{" + value + "}";
    }
}
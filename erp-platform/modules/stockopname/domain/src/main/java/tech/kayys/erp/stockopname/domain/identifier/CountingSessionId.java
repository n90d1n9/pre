package tech.kayys.erp.stockopname.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Counting session identifier.
 */
public final class CountingSessionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public CountingSessionId(UUID value) {
        super(value);
    }

    public static CountingSessionId of(UUID value) {
        return new CountingSessionId(value);
    }

    public static CountingSessionId generate() {
        return new CountingSessionId(UUID.randomUUID());
    }

    public static CountingSessionId fromString(String value) {
        return new CountingSessionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CountingSessionId{" + value + "}";
    }
}
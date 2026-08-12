package tech.kayys.erp.stockopname.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Counting item identifier.
 */
public final class CountingItemId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public CountingItemId(UUID value) {
        super(value);
    }

    public static CountingItemId of(UUID value) {
        return new CountingItemId(value);
    }

    public static CountingItemId generate() {
        return new CountingItemId(UUID.randomUUID());
    }

    public static CountingItemId fromString(String value) {
        return new CountingItemId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CountingItemId{" + value + "}";
    }
}
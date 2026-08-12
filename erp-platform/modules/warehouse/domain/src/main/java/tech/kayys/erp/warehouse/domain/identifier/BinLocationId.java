package tech.kayys.erp.warehouse.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Bin location identifier.
 */
public final class BinLocationId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public BinLocationId(UUID value) {
        super(value);
    }

    public static BinLocationId of(UUID value) {
        return new BinLocationId(value);
    }

    public static BinLocationId generate() {
        return new BinLocationId(UUID.randomUUID());
    }

    public static BinLocationId fromString(String value) {
        return new BinLocationId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "BinLocationId{" + value + "}";
    }
}
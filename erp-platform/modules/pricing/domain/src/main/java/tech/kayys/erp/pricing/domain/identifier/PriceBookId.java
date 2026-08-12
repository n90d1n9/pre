package tech.kayys.erp.pricing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class PriceBookId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public PriceBookId(UUID value) {
        super(value);
    }

    public static PriceBookId of(UUID value) {
        return new PriceBookId(value);
    }

    public static PriceBookId generate() {
        return new PriceBookId(UUID.randomUUID());
    }

    public static PriceBookId fromString(String value) {
        return new PriceBookId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PriceBookId{" + value + "}";
    }
}
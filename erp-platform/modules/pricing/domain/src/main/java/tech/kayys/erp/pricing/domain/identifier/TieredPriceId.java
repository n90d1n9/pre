package tech.kayys.erp.pricing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class TieredPriceId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public TieredPriceId(UUID value) {
        super(value);
    }

    public static TieredPriceId of(UUID value) {
        return new TieredPriceId(value);
    }

    public static TieredPriceId generate() {
        return new TieredPriceId(UUID.randomUUID());
    }

    public static TieredPriceId fromString(String value) {
        return new TieredPriceId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "TieredPriceId{" + value + "}";
    }
}
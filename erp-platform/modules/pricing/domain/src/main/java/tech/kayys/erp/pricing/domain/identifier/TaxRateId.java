package tech.kayys.erp.pricing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Tax rate identifier.
 */
public final class TaxRateId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public TaxRateId(UUID value) {
        super(value);
    }

    public static TaxRateId of(UUID value) {
        return new TaxRateId(value);
    }

    public static TaxRateId generate() {
        return new TaxRateId(UUID.randomUUID());
    }

    public static TaxRateId fromString(String value) {
        return new TaxRateId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "TaxRateId{" + value + "}";
    }
}
package tech.kayys.erp.promotion.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Loyalty program identifier.
 */
public final class LoyaltyId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public LoyaltyId(UUID value) {
        super(value);
    }

    public static LoyaltyId of(UUID value) {
        return new LoyaltyId(value);
    }

    public static LoyaltyId generate() {
        return new LoyaltyId(UUID.randomUUID());
    }

    public static LoyaltyId fromString(String value) {
        return new LoyaltyId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "LoyaltyId{" + value + "}";
    }
}
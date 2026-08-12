package tech.kayys.erp.promotion.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Promotion identifier.
 */
public final class PromotionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public PromotionId(UUID value) {
        super(value);
    }

    public static PromotionId of(UUID value) {
        return new PromotionId(value);
    }

    public static PromotionId generate() {
        return new PromotionId(UUID.randomUUID());
    }

    public static PromotionId fromString(String value) {
        return new PromotionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PromotionId{" + value + "}";
    }
}
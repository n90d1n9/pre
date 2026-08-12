package tech.kayys.erp.pricing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Pricing rule identifier.
 */
public final class PricingRuleId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public PricingRuleId(UUID value) {
        super(value);
    }

    public static PricingRuleId of(UUID value) {
        return new PricingRuleId(value);
    }

    public static PricingRuleId generate() {
        return new PricingRuleId(UUID.randomUUID());
    }

    public static PricingRuleId fromString(String value) {
        return new PricingRuleId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PricingRuleId{" + value + "}";
    }
}
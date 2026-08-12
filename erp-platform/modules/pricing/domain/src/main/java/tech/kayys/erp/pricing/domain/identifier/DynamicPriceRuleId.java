package tech.kayys.erp.pricing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class DynamicPriceRuleId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public DynamicPriceRuleId(UUID value) {
        super(value);
    }

    public static DynamicPriceRuleId of(UUID value) {
        return new DynamicPriceRuleId(value);
    }

    public static DynamicPriceRuleId generate() {
        return new DynamicPriceRuleId(UUID.randomUUID());
    }

    public static DynamicPriceRuleId fromString(String value) {
        return new DynamicPriceRuleId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "DynamicPriceRuleId{" + value + "}";
    }
}
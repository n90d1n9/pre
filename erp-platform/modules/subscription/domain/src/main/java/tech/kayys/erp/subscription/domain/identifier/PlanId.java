package tech.kayys.erp.subscription.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Subscription plan identifier.
 */
public final class PlanId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public PlanId(UUID value) {
        super(value);
    }

    public static PlanId of(UUID value) {
        return new PlanId(value);
    }

    public static PlanId generate() {
        return new PlanId(UUID.randomUUID());
    }

    public static PlanId fromString(String value) {
        return new PlanId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PlanId{" + value + "}";
    }
}
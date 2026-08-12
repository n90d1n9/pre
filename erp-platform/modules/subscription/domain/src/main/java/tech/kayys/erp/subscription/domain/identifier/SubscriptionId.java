package tech.kayys.erp.subscription.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Subscription identifier.
 */
public final class SubscriptionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public SubscriptionId(UUID value) {
        super(value);
    }

    public static SubscriptionId of(UUID value) {
        return new SubscriptionId(value);
    }

    public static SubscriptionId generate() {
        return new SubscriptionId(UUID.randomUUID());
    }

    public static SubscriptionId fromString(String value) {
        return new SubscriptionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "SubscriptionId{" + value + "}";
    }
}
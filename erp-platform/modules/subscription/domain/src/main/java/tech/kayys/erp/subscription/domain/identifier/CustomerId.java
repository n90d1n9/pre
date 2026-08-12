package tech.kayys.erp.subscription.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Customer identifier in the Subscription context.
 * Represents a customer from CRM context.
 */
public final class CustomerId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public CustomerId(UUID value) {
        super(value);
    }

    public static CustomerId of(UUID value) {
        return new CustomerId(value);
    }

    public static CustomerId fromString(String value) {
        return new CustomerId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CustomerId{" + value + "}";
    }
}
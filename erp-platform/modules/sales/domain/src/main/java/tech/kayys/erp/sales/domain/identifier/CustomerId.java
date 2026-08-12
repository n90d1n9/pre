package tech.kayys.erp.sales.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Customer ID in the Sales bounded context.
 * This represents a customer from the perspective of Sales.
 * It's a value object, not an entity reference to CRM's Customer.
 */
public final class CustomerId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public CustomerId(UUID value) {
        super(value);
    }

    public static CustomerId of(UUID value) {
        return new CustomerId(value);
    }

    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return "CustomerId{" + value + "}";
    }
}
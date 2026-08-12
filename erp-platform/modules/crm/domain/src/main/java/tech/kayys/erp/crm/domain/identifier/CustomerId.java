package tech.kayys.erp.crm.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Customer identifier in the CRM context.
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

    public static CustomerId fromString(String value) {
        return new CustomerId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CustomerId{" + value + "}";
    }
}
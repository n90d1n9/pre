package tech.kayys.erp.groceries.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class GroceryProductId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public GroceryProductId(UUID value) {
        super(value);
    }

    public static GroceryProductId of(UUID value) {
        return new GroceryProductId(value);
    }

    public static GroceryProductId generate() {
        return new GroceryProductId(UUID.randomUUID());
    }

    public static GroceryProductId fromString(String value) {
        return new GroceryProductId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "GroceryProductId{" + value + "}";
    }
}
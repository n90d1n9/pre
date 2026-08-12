package tech.kayys.erp.groceries.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Shelf item identifier for tracking shelf placement.
 */
public final class ShelfItemId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    public ShelfItemId(UUID value) {
        super(value);
    }

    public static ShelfItemId of(UUID value) {
        return new ShelfItemId(value);
    }

    public static ShelfItemId generate() {
        return new ShelfItemId(UUID.randomUUID());
    }

    public static ShelfItemId fromString(String value) {
        return new ShelfItemId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ShelfItemId{" + value + "}";
    }
}

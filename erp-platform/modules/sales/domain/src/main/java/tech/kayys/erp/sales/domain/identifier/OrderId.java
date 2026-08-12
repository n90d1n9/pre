package tech.kayys.erp.sales.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Order identifier in the Sales bounded context.
 */
public final class OrderId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public OrderId(UUID value) {
        super(value);
    }

    public static OrderId of(UUID value) {
        return new OrderId(value);
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }

    public static OrderId fromString(String value) {
        return new OrderId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "OrderId{" + value + "}";
    }
}
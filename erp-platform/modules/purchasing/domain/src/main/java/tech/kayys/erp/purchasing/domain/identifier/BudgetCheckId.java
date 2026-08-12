package tech.kayys.erp.purchasing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class BudgetCheckId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public BudgetCheckId(UUID value) {
        super(value);
    }

    public static BudgetCheckId of(UUID value) {
        return new BudgetCheckId(value);
    }

    public static BudgetCheckId generate() {
        return new BudgetCheckId(UUID.randomUUID());
    }

    public static BudgetCheckId fromString(String value) {
        return new BudgetCheckId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "BudgetCheckId{" + value + "}";
    }
}
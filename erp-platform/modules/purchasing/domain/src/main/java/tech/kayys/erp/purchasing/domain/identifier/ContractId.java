package tech.kayys.erp.purchasing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Vendor contract identifier.
 */
public final class ContractId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ContractId(UUID value) {
        super(value);
    }

    public static ContractId of(UUID value) {
        return new ContractId(value);
    }

    public static ContractId generate() {
        return new ContractId(UUID.randomUUID());
    }

    public static ContractId fromString(String value) {
        return new ContractId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ContractId{" + value + "}";
    }
}
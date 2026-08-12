package tech.kayys.erp.company.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Branch identifier.
 */
public final class BranchId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public BranchId(UUID value) {
        super(value);
    }

    public static BranchId of(UUID value) {
        return new BranchId(value);
    }

    public static BranchId generate() {
        return new BranchId(UUID.randomUUID());
    }

    public static BranchId fromString(String value) {
        return new BranchId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "BranchId{" + value + "}";
    }
}

package tech.kayys.erp.company.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Department identifier.
 */
public final class DepartmentId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public DepartmentId(UUID value) {
        super(value);
    }

    public static DepartmentId of(UUID value) {
        return new DepartmentId(value);
    }

    public static DepartmentId generate() {
        return new DepartmentId(UUID.randomUUID());
    }

    public static DepartmentId fromString(String value) {
        return new DepartmentId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "DepartmentId{" + value + "}";
    }
}
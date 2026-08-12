package tech.kayys.erp.employee.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Employee identifier.
 */
public final class EmployeeId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public EmployeeId(UUID value) {
        super(value);
    }

    public static EmployeeId of(UUID value) {
        return new EmployeeId(value);
    }

    public static EmployeeId generate() {
        return new EmployeeId(UUID.randomUUID());
    }

    public static EmployeeId fromString(String value) {
        return new EmployeeId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "EmployeeId{" + value + "}";
    }
}
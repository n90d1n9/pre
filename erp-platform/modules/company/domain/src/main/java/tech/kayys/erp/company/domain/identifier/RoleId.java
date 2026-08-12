package tech.kayys.erp.company.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Role identifier.
 */
public final class RoleId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public RoleId(UUID value) {
        super(value);
    }

    public static RoleId of(UUID value) {
        return new RoleId(value);
    }

    public static RoleId generate() {
        return new RoleId(UUID.randomUUID());
    }

    public static RoleId fromString(String value) {
        return new RoleId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "RoleId{" + value + "}";
    }
}

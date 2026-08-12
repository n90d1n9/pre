package tech.kayys.erp.compliance.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Tenant identifier.
 */
public final class TenantId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public TenantId(UUID value) {
        super(value);
    }

    public static TenantId of(UUID value) {
        return new TenantId(value);
    }

    public static TenantId generate() {
        return new TenantId(UUID.randomUUID());
    }

    public static TenantId fromString(String value) {
        return new TenantId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "TenantId{" + value + "}";
    }
}

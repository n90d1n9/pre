package tech.kayys.syirkah.accounting.domain.multitenancy;

import tech.kayys.syirkah.foundation.domain.valueobject.ValueObject;
import java.util.Objects;
import java.util.UUID;

/**
 * Tenant identifier for multi-tenant isolation across all accounting aggregates.
 */
public record TenantId(String value) implements ValueObject {
    public TenantId {
        Objects.requireNonNull(value, "TenantId value cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("TenantId cannot be blank");
        }
    }

    public static TenantId of(String value) {
        return new TenantId(value);
    }

    public static TenantId generate() {
        return new TenantId(UUID.randomUUID().toString());
    }

    public static TenantId defaultTenant() {
        return new TenantId("default");
    }
}

package tech.kayys.syirkah.security.domain.authorization;

import tech.kayys.syirkah.security.domain.identifier.PrincipalId;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Who is asking (base01.md §P1-17).
 *
 * <p>A principal may belong to several tenants, so tenancy is not a
 * property of the principal - it is part of the request being authorised.
 *
 * @param id        principal identity
 * @param handle    login / service name for human-readable audit
 * @param tenantIds tenants this principal is a member of
 */
public record Principal(
        PrincipalId id,
        String handle,
        Set<UUID> tenantIds) {

    public Principal {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(handle, "handle cannot be null");
        tenantIds = tenantIds == null ? Set.of() : Set.copyOf(tenantIds);
    }

    /** True when this principal is a member of the given tenant. */
    public boolean isMemberOf(UUID tenantId) {
        return tenantId != null && tenantIds.contains(tenantId);
    }

    /** Convenience builder for a single-tenant principal. */
    public static Principal of(PrincipalId id, String handle, UUID tenantId) {
        final var tenants = new LinkedHashSet<UUID>();
        if (tenantId != null) {
            tenants.add(tenantId);
        }
        return new Principal(id, handle, Collections.unmodifiableSet(tenants));
    }
}

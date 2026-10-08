package tech.kayys.syirkah.security.domain.authorization;

import tech.kayys.syirkah.foundation.domain.tenant.TenantId;

import java.util.Objects;
import java.util.Set;

/**
 * Canonical runtime security context capturing the active authenticated principal,
 * tenant scope, and effective permissions (security01.md §3.3).
 */
public record SecurityContext(
        Principal principal,
        TenantId tenantId,
        Set<Permission> permissions) {

    public SecurityContext {
        Objects.requireNonNull(principal, "principal cannot be null");
        Objects.requireNonNull(tenantId, "tenantId cannot be null");
        permissions = permissions == null ? Set.of() : Set.copyOf(permissions);
    }

    public boolean authenticated() {
        return principal != null;
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
}

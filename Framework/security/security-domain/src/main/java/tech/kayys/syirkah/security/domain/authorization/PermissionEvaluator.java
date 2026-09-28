package tech.kayys.syirkah.security.domain.authorization;

import tech.kayys.syirkah.foundation.domain.exception.BusinessRuleViolation;

import java.util.Objects;
import java.util.Set;

/**
 * Domain service that decides an {@link AccessRequest} given the
 * permissions granted to the principal (base01.md §P1-17).
 *
 * <p>Order of the checks is deliberate and stable, because it is what
 * auditors read:
 *
 * <pre>
 *   1. tenant membership  - a non-member is denied before anything else
 *   2. permission grant    - the verb/resource pair must be granted
 *   3. resource scope      - a specific resource id is only allowed when
 *                            it was included in the grant
 * </pre>
 */
public final class PermissionEvaluator {

    /** Grants of the principal being evaluated. */
    private final Set<Permission> granted;

    /** Resource ids the grant is narrowed to; empty means "all". */
    private final Set<String> scopedResourceIds;

    public PermissionEvaluator(Set<Permission> granted, Set<String> scopedResourceIds) {
        this.granted = granted == null ? Set.of() : Set.copyOf(granted);
        this.scopedResourceIds = scopedResourceIds == null
                ? Set.of()
                : Set.copyOf(scopedResourceIds);
    }

    /** Evaluates a request. Never throws for denials - denial is a result. */
    public AccessDecision decide(AccessRequest request) {
        Objects.requireNonNull(request, "request cannot be null");

        if (!request.principal().isMemberOf(request.tenantId())) {
            return AccessDecision.deny("Principal is not a member of tenant " + request.tenantId());
        }

        final var permission = Permission.of(request.action(), request.resourceType());
        if (!granted.contains(permission)) {
            return AccessDecision.deny(
                    "Permission " + permission.action() + " on " + permission.resourceType()
                            + " is not granted");
        }

        if (!request.resourceId().isBlank()
                && !scopedResourceIds.isEmpty()
                && !scopedResourceIds.contains(request.resourceId())) {
            return AccessDecision.deny(
                    "Permission is scoped and does not cover " + request.resourceId());
        }

        return AccessDecision.allow(
                "Member of tenant " + request.tenantId()
                        + " with permission " + permission.action()
                        + " on " + permission.resourceType());
    }

    /** Convenience factory for an all-resources grant. */
    public static PermissionEvaluator with(Set<Permission> granted) {
        if (granted == null || granted.isEmpty()) {
            throw new BusinessRuleViolation("At least one permission grant is required");
        }
        return new PermissionEvaluator(granted, Set.of());
    }
}

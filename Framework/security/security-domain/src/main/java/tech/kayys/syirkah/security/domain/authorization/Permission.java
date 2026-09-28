package tech.kayys.syirkah.security.domain.authorization;

import tech.kayys.syirkah.security.domain.valueobject.ActionType;

import java.util.Objects;

/**
 * A grant: "this role may perform this action on this resource type".
 *
 * @param action       the verb
 * @param resourceType the domain concept it applies to, e.g. Shipment
 */
public record Permission(ActionType action, String resourceType) {

    public Permission {
        Objects.requireNonNull(action, "action cannot be null");
        Objects.requireNonNull(resourceType, "resourceType cannot be null");
        if (resourceType.isBlank()) {
            throw new IllegalArgumentException("resourceType cannot be blank");
        }
    }

    public static Permission of(ActionType action, String resourceType) {
        return new Permission(action, resourceType);
    }
}

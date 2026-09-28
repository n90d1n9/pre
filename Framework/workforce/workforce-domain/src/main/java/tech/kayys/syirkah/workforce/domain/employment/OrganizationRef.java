package tech.kayys.syirkah.workforce.domain.employment;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;

import java.util.Objects;
import java.util.UUID;

/**
 * Reference to an external Organization in the organization/party domain.
 */
public record OrganizationRef(UUID value) implements DomainId<UUID> {

    public OrganizationRef {
        Objects.requireNonNull(value, "Organization ID cannot be null");
    }

    public static OrganizationRef of(UUID value) {
        return new OrganizationRef(value);
    }
}

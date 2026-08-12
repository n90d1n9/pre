package tech.kayys.erp.compliance.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Compliance requirement identifier.
 */
public final class ComplianceRequirementId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ComplianceRequirementId(UUID value) {
        super(value);
    }

    public static ComplianceRequirementId of(UUID value) {
        return new ComplianceRequirementId(value);
    }

    public static ComplianceRequirementId generate() {
        return new ComplianceRequirementId(UUID.randomUUID());
    }

    public static ComplianceRequirementId fromString(String value) {
        return new ComplianceRequirementId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ComplianceRequirementId{" + value + "}";
    }
}
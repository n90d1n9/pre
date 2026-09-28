package tech.kayys.syirkah.security.domain.audit.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

/**
 * Identifier for audit trail entries.
 */
public final class AuditTrailId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    public AuditTrailId(UUID value) {
        super(value);
    }

    public static AuditTrailId of(UUID value) {
        return new AuditTrailId(value);
    }

    public static AuditTrailId generate() {
        return new AuditTrailId(UUID.randomUUID());
    }

    public static AuditTrailId fromString(String value) {
        return new AuditTrailId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "AuditTrailId{" + value + "}";
    }
}

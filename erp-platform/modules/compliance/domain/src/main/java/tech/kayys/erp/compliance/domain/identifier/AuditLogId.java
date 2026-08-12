package tech.kayys.erp.compliance.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Audit log entry identifier.
 */
public final class AuditLogId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public AuditLogId(UUID value) {
        super(value);
    }

    public static AuditLogId of(UUID value) {
        return new AuditLogId(value);
    }

    public static AuditLogId generate() {
        return new AuditLogId(UUID.randomUUID());
    }

    public static AuditLogId fromString(String value) {
        return new AuditLogId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "AuditLogId{" + value + "}";
    }
}

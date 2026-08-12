package tech.kayys.erp.risk.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Incident identifier.
 */
public final class IncidentId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public IncidentId(UUID value) {
        super(value);
    }

    public static IncidentId of(UUID value) {
        return new IncidentId(value);
    }

    public static IncidentId generate() {
        return new IncidentId(UUID.randomUUID());
    }

    public static IncidentId fromString(String value) {
        return new IncidentId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "IncidentId{" + value + "}";
    }
}
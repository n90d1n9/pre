package tech.kayys.erp.compliance.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Regulation identifier.
 */
public final class RegulationId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public RegulationId(UUID value) {
        super(value);
    }

    public static RegulationId of(UUID value) {
        return new RegulationId(value);
    }

    public static RegulationId generate() {
        return new RegulationId(UUID.randomUUID());
    }

    public static RegulationId fromString(String value) {
        return new RegulationId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "RegulationId{" + value + "}";
    }
}

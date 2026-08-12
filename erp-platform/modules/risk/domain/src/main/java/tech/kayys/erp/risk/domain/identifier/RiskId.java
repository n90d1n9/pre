package tech.kayys.erp.risk.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Risk identifier.
 */
public final class RiskId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public RiskId(UUID value) {
        super(value);
    }

    public static RiskId of(UUID value) {
        return new RiskId(value);
    }

    public static RiskId generate() {
        return new RiskId(UUID.randomUUID());
    }

    public static RiskId fromString(String value) {
        return new RiskId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "RiskId{" + value + "}";
    }
}
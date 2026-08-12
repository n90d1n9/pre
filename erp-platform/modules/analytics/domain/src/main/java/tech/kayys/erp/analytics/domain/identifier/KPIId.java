package tech.kayys.erp.analytics.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * KPI identifier.
 */
public final class KPIId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public KPIId(UUID value) {
        super(value);
    }

    public static KPIId of(UUID value) {
        return new KPIId(value);
    }

    public static KPIId generate() {
        return new KPIId(UUID.randomUUID());
    }

    public static KPIId fromString(String value) {
        return new KPIId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "KPIId{" + value + "}";
    }
}
package tech.kayys.erp.asset.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Maintenance record identifier.
 */
public final class MaintenanceId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public MaintenanceId(UUID value) {
        super(value);
    }

    public static MaintenanceId of(UUID value) {
        return new MaintenanceId(value);
    }

    public static MaintenanceId generate() {
        return new MaintenanceId(UUID.randomUUID());
    }

    public static MaintenanceId fromString(String value) {
        return new MaintenanceId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "MaintenanceId{" + value + "}";
    }
}
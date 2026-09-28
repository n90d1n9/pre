package tech.kayys.syirkah.analytics.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

/**
 * Dashboard identifier.
 */
public final class DashboardId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public DashboardId(UUID value) {
        super(value);
    }

    public static DashboardId of(UUID value) {
        return new DashboardId(value);
    }

    public static DashboardId generate() {
        return new DashboardId(UUID.randomUUID());
    }

    public static DashboardId fromString(String value) {
        return new DashboardId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "DashboardId{" + value + "}";
    }
}
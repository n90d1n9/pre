package tech.kayys.syirkah.fms.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

public final class GeofenceId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    public GeofenceId(UUID value) {
        super(value);
    }

    public static GeofenceId of(UUID value) {
        return new GeofenceId(value);
    }

    public static GeofenceId generate() {
        return new GeofenceId(UUID.randomUUID());
    }

    public static GeofenceId fromString(String value) {
        return new GeofenceId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "GeofenceId{" + value + "}";
    }
}

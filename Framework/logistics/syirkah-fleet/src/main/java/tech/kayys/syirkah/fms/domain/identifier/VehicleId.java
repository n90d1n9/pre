package tech.kayys.syirkah.fms.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

public final class VehicleId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    public VehicleId(UUID value) {
        super(value);
    }

    public static VehicleId of(UUID value) {
        return new VehicleId(value);
    }

    public static VehicleId generate() {
        return new VehicleId(UUID.randomUUID());
    }

    public static VehicleId fromString(String value) {
        return new VehicleId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "VehicleId{" + value + "}";
    }
}

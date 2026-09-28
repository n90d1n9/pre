package tech.kayys.syirkah.fms.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

public final class DriverId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    public DriverId(UUID value) {
        super(value);
    }

    public static DriverId of(UUID value) {
        return new DriverId(value);
    }

    public static DriverId generate() {
        return new DriverId(UUID.randomUUID());
    }

    public static DriverId fromString(String value) {
        return new DriverId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "DriverId{" + value + "}";
    }
}

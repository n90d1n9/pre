package tech.kayys.syirkah.fms.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

public final class RouteId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    public RouteId(UUID value) {
        super(value);
    }

    public static RouteId of(UUID value) {
        return new RouteId(value);
    }

    public static RouteId generate() {
        return new RouteId(UUID.randomUUID());
    }

    public static RouteId fromString(String value) {
        return new RouteId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "RouteId{" + value + "}";
    }
}

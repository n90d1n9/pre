package tech.kayys.syirkah.fms.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

public final class FuelTransactionId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    public FuelTransactionId(UUID value) {
        super(value);
    }

    public static FuelTransactionId of(UUID value) {
        return new FuelTransactionId(value);
    }

    public static FuelTransactionId generate() {
        return new FuelTransactionId(UUID.randomUUID());
    }

    public static FuelTransactionId fromString(String value) {
        return new FuelTransactionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "FuelTransactionId{" + value + "}";
    }
}

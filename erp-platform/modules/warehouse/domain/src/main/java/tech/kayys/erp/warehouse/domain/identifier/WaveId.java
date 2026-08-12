package tech.kayys.erp.warehouse.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Wave identifier.
 */
public final class WaveId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public WaveId(UUID value) {
        super(value);
    }

    public static WaveId of(UUID value) {
        return new WaveId(value);
    }

    public static WaveId generate() {
        return new WaveId(UUID.randomUUID());
    }

    public static WaveId fromString(String value) {
        return new WaveId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "WaveId{" + value + "}";
    }
}
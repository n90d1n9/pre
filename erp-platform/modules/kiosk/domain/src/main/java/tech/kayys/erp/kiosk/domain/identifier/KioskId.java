package tech.kayys.erp.kiosk.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Kiosk device identifier.
 */
public final class KioskId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public KioskId(UUID value) {
        super(value);
    }

    public static KioskId of(UUID value) {
        return new KioskId(value);
    }

    public static KioskId generate() {
        return new KioskId(UUID.randomUUID());
    }

    public static KioskId fromString(String value) {
        return new KioskId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "KioskId{" + value + "}";
    }
}
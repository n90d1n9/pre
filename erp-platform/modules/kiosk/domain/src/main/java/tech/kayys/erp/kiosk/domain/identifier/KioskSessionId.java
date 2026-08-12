package tech.kayys.erp.kiosk.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Kiosk session identifier for customer sessions.
 */
public final class KioskSessionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public KioskSessionId(UUID value) {
        super(value);
    }

    public static KioskSessionId of(UUID value) {
        return new KioskSessionId(value);
    }

    public static KioskSessionId generate() {
        return new KioskSessionId(UUID.randomUUID());
    }

    public static KioskSessionId fromString(String value) {
        return new KioskSessionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "KioskSessionId{" + value + "}";
    }
}
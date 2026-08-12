package tech.kayys.erp.groceries.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Scale device identifier.
 */
public final class ScaleId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ScaleId(UUID value) {
        super(value);
    }

    public static ScaleId of(UUID value) {
        return new ScaleId(value);
    }

    public static ScaleId generate() {
        return new ScaleId(UUID.randomUUID());
    }

    public static ScaleId fromString(String value) {
        return new ScaleId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ScaleId{" + value + "}";
    }
}
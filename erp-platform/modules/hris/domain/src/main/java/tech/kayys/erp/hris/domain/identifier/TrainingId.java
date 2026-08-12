package tech.kayys.erp.hris.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Training program identifier.
 */
public final class TrainingId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public TrainingId(UUID value) {
        super(value);
    }

    public static TrainingId of(UUID value) {
        return new TrainingId(value);
    }

    public static TrainingId generate() {
        return new TrainingId(UUID.randomUUID());
    }

    public static TrainingId fromString(String value) {
        return new TrainingId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "TrainingId{" + value + "}";
    }
}
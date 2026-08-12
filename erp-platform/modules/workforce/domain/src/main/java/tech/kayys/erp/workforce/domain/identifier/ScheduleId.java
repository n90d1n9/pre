package tech.kayys.erp.workforce.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Schedule identifier.
 */
public final class ScheduleId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ScheduleId(UUID value) {
        super(value);
    }

    public static ScheduleId of(UUID value) {
        return new ScheduleId(value);
    }

    public static ScheduleId generate() {
        return new ScheduleId(UUID.randomUUID());
    }

    public static ScheduleId fromString(String value) {
        return new ScheduleId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ScheduleId{" + value + "}";
    }
}
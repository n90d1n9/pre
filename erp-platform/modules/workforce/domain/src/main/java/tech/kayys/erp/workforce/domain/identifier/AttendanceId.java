package tech.kayys.erp.workforce.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Attendance record identifier.
 */
public final class AttendanceId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public AttendanceId(UUID value) {
        super(value);
    }

    public static AttendanceId of(UUID value) {
        return new AttendanceId(value);
    }

    public static AttendanceId generate() {
        return new AttendanceId(UUID.randomUUID());
    }

    public static AttendanceId fromString(String value) {
        return new AttendanceId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "AttendanceId{" + value + "}";
    }
}
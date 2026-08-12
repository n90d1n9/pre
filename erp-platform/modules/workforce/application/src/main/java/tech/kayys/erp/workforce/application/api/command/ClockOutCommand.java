package tech.kayys.erp.workforce.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.workforce.domain.identifier.AttendanceId;

import java.time.LocalTime;

/**
 * Command to clock out an employee.
 */
public record ClockOutCommand(
        AttendanceId attendanceId,
        LocalTime clockOutTime,
        String modifiedBy
) implements Command<AttendanceId> {

    public ClockOutCommand {
        if (attendanceId == null) {
            throw new IllegalArgumentException("Attendance ID cannot be null");
        }
        if (clockOutTime == null) {
            throw new IllegalArgumentException("Clock out time cannot be null");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private AttendanceId attendanceId;
        private LocalTime clockOutTime;
        private String modifiedBy;

        public Builder attendanceId(AttendanceId attendanceId) {
            this.attendanceId = attendanceId;
            return this;
        }

        public Builder clockOutTime(LocalTime clockOutTime) {
            this.clockOutTime = clockOutTime;
            return this;
        }

        public Builder modifiedBy(String modifiedBy) {
            this.modifiedBy = modifiedBy;
            return this;
        }

        public ClockOutCommand build() {
            if (clockOutTime == null) {
                clockOutTime = LocalTime.now();
            }
            return new ClockOutCommand(attendanceId, clockOutTime, modifiedBy);
        }
    }
}
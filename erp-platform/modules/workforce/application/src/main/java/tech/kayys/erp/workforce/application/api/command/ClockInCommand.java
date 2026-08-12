package tech.kayys.erp.workforce.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.workforce.domain.identifier.AttendanceId;
import tech.kayys.erp.workforce.domain.identifier.EmployeeId;

import java.time.LocalTime;
import java.util.UUID;

/**
 * Command to clock in an employee.
 */
public record ClockInCommand(
        AttendanceId attendanceId,
        UUID employeeId,
        String employeeName,
        String employeeNumber,
        LocalTime clockInTime,
        String location,
        String shiftId,
        String shiftName,
        String department,
        String createdBy
) implements Command<AttendanceId> {

    public ClockInCommand {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        if (clockInTime == null) {
            throw new IllegalArgumentException("Clock in time cannot be null");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private AttendanceId attendanceId;
        private UUID employeeId;
        private String employeeName;
        private String employeeNumber;
        private LocalTime clockInTime;
        private String location;
        private String shiftId;
        private String shiftName;
        private String department;
        private String createdBy;

        public Builder attendanceId(AttendanceId attendanceId) {
            this.attendanceId = attendanceId;
            return this;
        }

        public Builder employeeId(UUID employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public Builder employeeName(String employeeName) {
            this.employeeName = employeeName;
            return this;
        }

        public Builder employeeNumber(String employeeNumber) {
            this.employeeNumber = employeeNumber;
            return this;
        }

        public Builder clockInTime(LocalTime clockInTime) {
            this.clockInTime = clockInTime;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder shiftId(String shiftId) {
            this.shiftId = shiftId;
            return this;
        }

        public Builder shiftName(String shiftName) {
            this.shiftName = shiftName;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public ClockInCommand build() {
            if (attendanceId == null) {
                attendanceId = AttendanceId.generate();
            }
            if (clockInTime == null) {
                clockInTime = LocalTime.now();
            }
            return new ClockInCommand(
                attendanceId, employeeId, employeeName, employeeNumber,
                clockInTime, location, shiftId, shiftName, department, createdBy
            );
        }
    }
}
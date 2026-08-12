package tech.kayys.erp.employee.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.employee.domain.identifier.LeaveRequestId;
import tech.kayys.erp.employee.domain.valueobject.LeaveType;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Command to create a leave request.
 */
public record CreateLeaveRequestCommand(
        LeaveRequestId leaveRequestId,
        UUID employeeId,
        LeaveType leaveType,
        LocalDate startDate,
        LocalDate endDate,
        String reason
) implements Command<LeaveRequestId> {

    public CreateLeaveRequestCommand {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        if (leaveType == null) {
            throw new IllegalArgumentException("Leave type is required");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date is required");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LeaveRequestId leaveRequestId;
        private UUID employeeId;
        private LeaveType leaveType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String reason;

        public Builder leaveRequestId(LeaveRequestId leaveRequestId) {
            this.leaveRequestId = leaveRequestId;
            return this;
        }

        public Builder employeeId(UUID employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public Builder leaveType(LeaveType leaveType) {
            this.leaveType = leaveType;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public CreateLeaveRequestCommand build() {
            if (leaveRequestId == null) {
                leaveRequestId = LeaveRequestId.generate();
            }
            return new CreateLeaveRequestCommand(
                leaveRequestId, employeeId, leaveType, startDate, endDate, reason
            );
        }
    }
}
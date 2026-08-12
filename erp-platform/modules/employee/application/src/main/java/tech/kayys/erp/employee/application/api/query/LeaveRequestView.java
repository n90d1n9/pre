package tech.kayys.erp.employee.application.api.query;

import tech.kayys.erp.employee.domain.model.LeaveRequest;

import java.time.Instant;
import java.time.LocalDate;

/**
 * View of a leave request.
 */
public record LeaveRequestView(
        String leaveRequestId,
        String employeeId,
        String leaveType,
        String status,
        LocalDate startDate,
        LocalDate endDate,
        double days,
        String reason,
        String approvedBy,
        Instant approvedAt,
        String rejectedBy,
        Instant rejectedAt,
        String rejectionReason,
        String notes,
        boolean active
) {

    public static LeaveRequestView fromDomain(LeaveRequest request) {
        return new LeaveRequestView(
            request.getId().toString(),
            request.getEmployeeId().toString(),
            request.getLeaveType().name(),
            request.getStatus().name(),
            request.getStartDate(),
            request.getEndDate(),
            request.getDays(),
            request.getReason(),
            request.getApprovedBy(),
            request.getApprovedAt(),
            request.getRejectedBy(),
            request.getRejectedAt(),
            request.getRejectionReason(),
            request.getNotes(),
            request.isActive()
        );
    }
}
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <module>modules/catalog/domain</module>
    <module>modules/catalog/application</module>
    <module>modules/catalog/infrastructure</module>
    <module>modules/catalog/interfaces</module>

    <module>modules/sales/domain</module>
    <module>modules/sales/application</module>
    <module>modules/sales/infrastructure</module>
    <module>modules/sales/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/subscription/domain</module>
    <module>modules/subscription/application</module>
    <module>modules/subscription/infrastructure</module>
    <module>modules/subscription/interfaces</module>

    <module>modules/accounting/domain</module>
    <module>modules/accounting/application</module>
    <module>modules/accounting/infrastructure</module>
    <module>modules/accounting/interfaces</module>

    <module>modules/purchasing/domain</module>
    <module>modules/purchasing/application</module>
    <module>modules/purchasing/infrastructure</module>
    <module>modules/purchasing/interfaces</module>

    <module>modules/promotion/domain</module>
    <module>modules/promotion/application</module>
    <module>modules/promotion/infrastructure</module>
    <module>modules/promotion/interfaces</module>

    <module>modules/employee/domain</module>
    <module>modules/employee/application</module>
    <module>modules/employee/infrastructure</module>
    <module>modules/employee/interfaces</module>
</modules>
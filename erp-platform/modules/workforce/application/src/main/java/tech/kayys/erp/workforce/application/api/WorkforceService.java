package tech.kayys.erp.workforce.application.api;

import tech.kayys.erp.workforce.application.api.command.ClockInCommand;
import tech.kayys.erp.workforce.application.api.command.ClockOutCommand;
import tech.kayys.erp.workforce.application.api.query.AttendanceView;
import tech.kayys.erp.workforce.domain.identifier.AttendanceId;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Public API for workforce operations.
 */
public interface WorkforceService {

    /**
     * Clocks in an employee.
     */
    CompletionStage<AttendanceId> clockIn(ClockInCommand command);

    /**
     * Clocks out an employee.
     */
    CompletionStage<AttendanceId> clockOut(ClockOutCommand command);

    /**
     * Approves an attendance record.
     */
    CompletionStage<AttendanceId> approveAttendance(AttendanceId attendanceId, String approvedBy);

    /**
     * Gets an attendance record by ID.
     */
    CompletionStage<AttendanceView> getAttendance(AttendanceId attendanceId);

    /**
     * Gets attendance records for an employee.
     */
    CompletionStage<List<AttendanceView>> getEmployeeAttendance(
        UUID employeeId, String startDate, String endDate, int page, int size
    );

    /**
     * Gets today's attendance records.
     */
    CompletionStage<List<AttendanceView>> getTodayAttendance();

    /**
     * Gets attendance records by date.
     */
    CompletionStage<List<AttendanceView>> getAttendanceByDate(LocalDate date);

    /**
     * Gets pending approval attendance records.
     */
    CompletionStage<List<AttendanceView>> getPendingApproval();

    /**
     * Gets attendance summary for an employee.
     */
    CompletionStage<AttendanceSummaryView> getEmployeeSummary(UUID employeeId, LocalDate start, LocalDate end);
}
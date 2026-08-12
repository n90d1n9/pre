package tech.kayys.erp.workforce.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.workforce.domain.identifier.AttendanceId;
import tech.kayys.erp.workforce.domain.identifier.EmployeeId;
import tech.kayys.erp.workforce.domain.model.AttendanceRecord;
import tech.kayys.erp.workforce.domain.valueobject.AttendanceStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for AttendanceRecord aggregates.
 */
public interface AttendanceRepository extends Repository<AttendanceRecord, AttendanceId> {

    /**
     * Finds attendance by employee ID.
     */
    CompletionStage<List<AttendanceRecord>> findByEmployee(EmployeeId employeeId);

    /**
     * Finds attendance by employee ID and date range.
     */
    CompletionStage<List<AttendanceRecord>> findByEmployeeAndDateRange(
        EmployeeId employeeId, LocalDate start, LocalDate end
    );

    /**
     * Finds attendance by date.
     */
    CompletionStage<List<AttendanceRecord>> findByDate(LocalDate date);

    /**
     * Finds attendance by date range.
     */
    CompletionStage<List<AttendanceRecord>> findByDateRange(LocalDate start, LocalDate end);

    /**
     * Finds attendance by status.
     */
    CompletionStage<List<AttendanceRecord>> findByStatus(AttendanceStatus status);

    /**
     * Finds attendance by employee and status.
     */
    CompletionStage<List<AttendanceRecord>> findByEmployeeAndStatus(
        EmployeeId employeeId, AttendanceStatus status
    );

    /**
     * Finds pending approval attendance.
     */
    CompletionStage<List<AttendanceRecord>> findPendingApproval();

    /**
     * Finds attendance for a specific date and employee.
     */
    CompletionStage<AttendanceRecord> findByEmployeeAndDate(
        EmployeeId employeeId, LocalDate date
    );

    /**
     * Counts attendance by status.
     */
    CompletionStage<Long> countByStatus(AttendanceStatus status);

    /**
     * Counts attendance by employee and date range.
     */
    CompletionStage<Long> countByEmployeeAndDateRange(
        EmployeeId employeeId, LocalDate start, LocalDate end
    );

    /**
     * Gets total hours by employee and date range.
     */
    CompletionStage<Double> getTotalHoursByEmployeeAndDateRange(
        EmployeeId employeeId, LocalDate start, LocalDate end
    );

    /**
     * Gets overtime hours by employee and date range.
     */
    CompletionStage<Double> getOvertimeHoursByEmployeeAndDateRange(
        EmployeeId employeeId, LocalDate start, LocalDate end
    );
}
package tech.kayys.erp.employee.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.employee.domain.identifier.EmployeeId;
import tech.kayys.erp.employee.domain.identifier.LeaveRequestId;
import tech.kayys.erp.employee.domain.valueobject.LeaveStatus;
import tech.kayys.erp.employee.domain.valueobject.LeaveType;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Leave request aggregate root.
 * Represents an employee's request for leave.
 */
public final class LeaveRequest extends AggregateRoot<LeaveRequestId> {
    
    private static final long serialVersionUID = 1L;
    
    private EmployeeId employeeId;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private double days;
    private String reason;
    private LeaveStatus status;
    private String approvedBy;
    private Instant approvedAt;
    private String rejectedBy;
    private Instant rejectedAt;
    private String rejectionReason;
    private String notes;
    private boolean active;

    private LeaveRequest(LeaveRequestId id) {
        super(id);
        this.status = LeaveStatus.PENDING;
        this.active = true;
    }

    private LeaveRequest() {
        super();
    }

    /**
     * Factory method to create a new leave request.
     */
    public static LeaveRequest create(
            LeaveRequestId id,
            EmployeeId employeeId,
            LeaveType leaveType,
            LocalDate startDate,
            LocalDate endDate,
            String reason) {
        LeaveRequest request = new LeaveRequest(id);
        request.employeeId = employeeId;
        request.leaveType = leaveType;
        request.startDate = startDate;
        request.endDate = endDate;
        request.days = calculateDays(startDate, endDate);
        request.reason = reason;
        return request;
    }

    private static double calculateDays(LocalDate start, LocalDate end) {
        return (double) java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
    }

    /**
     * Approves the leave request.
     */
    public void approve(String approvedBy) {
        if (status != LeaveStatus.PENDING) {
            throw new IllegalStateException("Leave request is not pending: " + status);
        }
        this.status = LeaveStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Rejects the leave request.
     */
    public void reject(String rejectedBy, String reason) {
        if (status != LeaveStatus.PENDING) {
            throw new IllegalStateException("Leave request is not pending: " + status);
        }
        this.status = LeaveStatus.REJECTED;
        this.rejectedBy = rejectedBy;
        this.rejectedAt = Instant.now();
        this.rejectionReason = reason;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the leave request.
     */
    public void cancel(String reason) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Cannot cancel leave request in status: " + status);
        }
        this.status = LeaveStatus.CANCELLED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the leave as taken.
     */
    public void markAsTaken() {
        if (status != LeaveStatus.APPROVED) {
            throw new IllegalStateException("Leave request is not approved: " + status);
        }
        this.status = LeaveStatus.TAKEN;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the number of working days requested.
     */
    public double getWorkingDays() {
        // In a real system, this would exclude weekends and holidays
        return days;
    }

    // Getters
    public EmployeeId getEmployeeId() { return employeeId; }
    public LeaveType getLeaveType() { return leaveType; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getDays() { return days; }
    public String getReason() { return reason; }
    public LeaveStatus getStatus() { return status; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public String getRejectedBy() { return rejectedBy; }
    public Instant getRejectedAt() { return rejectedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "LeaveRequest{" +
                "id=" + getId() +
                ", employeeId=" + employeeId +
                ", leaveType=" + leaveType +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }
}
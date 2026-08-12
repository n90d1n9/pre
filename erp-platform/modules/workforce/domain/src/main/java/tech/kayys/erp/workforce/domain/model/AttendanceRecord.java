package tech.kayys.erp.workforce.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.workforce.domain.identifier.AttendanceId;
import tech.kayys.erp.workforce.domain.identifier.EmployeeId;
import tech.kayys.erp.workforce.domain.valueobject.AttendanceStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Attendance record aggregate root.
 * Represents an employee's attendance for a specific day.
 */
public final class AttendanceRecord extends AggregateRoot<AttendanceId> {
    
    private static final long serialVersionUID = 1L;
    
    private EmployeeId employeeId;
    private String employeeName;
    private LocalDate date;
    private LocalTime clockInTime;
    private LocalTime clockOutTime;
    private LocalTime breakStartTime;
    private LocalTime breakEndTime;
    private double totalHours;
    private double regularHours;
    private double overtimeHours;
    private double breakHours;
    private AttendanceStatus status;
    private String shiftId;
    private String shiftName;
    private String location;
    private String department;
    private String notes;
    private boolean approved;
    private String approvedBy;
    private Instant approvedAt;
    private boolean active;

    private AttendanceRecord(AttendanceId id) {
        super(id);
        this.status = AttendanceStatus.PRESENT;
        this.active = true;
        this.approved = false;
    }

    private AttendanceRecord() {
        super();
    }

    /**
     * Factory method to create a new attendance record.
     */
    public static AttendanceRecord create(
            AttendanceId id,
            EmployeeId employeeId,
            String employeeName,
            LocalDate date,
            String location) {
        AttendanceRecord record = new AttendanceRecord(id);
        record.employeeId = employeeId;
        record.employeeName = employeeName;
        record.date = date;
        record.location = location;
        return record;
    }

    /**
     * Records clock in.
     */
    public void clockIn(LocalTime time, String location) {
        if (clockInTime != null) {
            throw new IllegalStateException("Already clocked in at: " + clockInTime);
        }
        this.clockInTime = time;
        this.location = location;
        this.status = AttendanceStatus.PRESENT;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records clock out.
     */
    public void clockOut(LocalTime time) {
        if (clockInTime == null) {
            throw new IllegalStateException("Cannot clock out without clocking in");
        }
        if (clockOutTime != null) {
            throw new IllegalStateException("Already clocked out at: " + clockOutTime);
        }
        this.clockOutTime = time;
        calculateHours();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records break start.
     */
    public void startBreak(LocalTime time) {
        if (breakStartTime != null) {
            throw new IllegalStateException("Break already started at: " + breakStartTime);
        }
        this.breakStartTime = time;
        this.status = AttendanceStatus.ON_BREAK;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records break end.
     */
    public void endBreak(LocalTime time) {
        if (breakStartTime == null) {
            throw new IllegalStateException("No break in progress");
        }
        this.breakEndTime = time;
        calculateHours();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records a break manually.
     */
    public void recordBreak(LocalTime start, LocalTime end) {
        this.breakStartTime = start;
        this.breakEndTime = end;
        calculateHours();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Calculates total hours worked.
     */
    private void calculateHours() {
        if (clockInTime != null && clockOutTime != null) {
            // Total time between clock in and clock out
            long totalMinutes = java.time.Duration.between(clockInTime, clockOutTime).toMinutes();
            
            // Subtract break time
            long breakMinutes = 0;
            if (breakStartTime != null && breakEndTime != null) {
                breakMinutes = java.time.Duration.between(breakStartTime, breakEndTime).toMinutes();
            }
            
            long workingMinutes = totalMinutes - breakMinutes;
            this.totalHours = workingMinutes / 60.0;
            this.breakHours = breakMinutes / 60.0;
            
            // Determine regular vs overtime (assuming 8 hours regular)
            if (this.totalHours > 8.0) {
                this.regularHours = 8.0;
                this.overtimeHours = this.totalHours - 8.0;
            } else {
                this.regularHours = this.totalHours;
                this.overtimeHours = 0.0;
            }
        }
    }

    /**
     * Approves the attendance record.
     */
    public void approve(String approvedBy) {
        if (clockOutTime == null) {
            throw new IllegalStateException("Cannot approve incomplete attendance");
        }
        this.approved = true;
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Rejects the attendance record.
     */
    public void reject(String reason) {
        this.approved = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the status.
     */
    public void updateStatus(AttendanceStatus status) {
        this.status = status;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if the employee is clocked in.
     */
    public boolean isClockedIn() {
        return clockInTime != null && clockOutTime == null;
    }

    /**
     * Checks if the record is complete.
     */
    public boolean isComplete() {
        return clockInTime != null && clockOutTime != null;
    }

    // Getters
    public EmployeeId getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public LocalDate getDate() { return date; }
    public LocalTime getClockInTime() { return clockInTime; }
    public LocalTime getClockOutTime() { return clockOutTime; }
    public LocalTime getBreakStartTime() { return breakStartTime; }
    public LocalTime getBreakEndTime() { return breakEndTime; }
    public double getTotalHours() { return totalHours; }
    public double getRegularHours() { return regularHours; }
    public double getOvertimeHours() { return overtimeHours; }
    public double getBreakHours() { return breakHours; }
    public AttendanceStatus getStatus() { return status; }
    public String getShiftId() { return shiftId; }
    public String getShiftName() { return shiftName; }
    public String getLocation() { return location; }
    public String getDepartment() { return department; }
    public String getNotes() { return notes; }
    public boolean isApproved() { return approved; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public boolean isActive() { return active; }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDepartment(String department) {
        this.department = department;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "AttendanceRecord{" +
                "id=" + getId() +
                ", employeeName='" + employeeName + '\'' +
                ", date=" + date +
                ", clockIn=" + clockInTime +
                ", clockOut=" + clockOutTime +
                ", totalHours=" + totalHours +
                ", status=" + status +
                '}';
    }
}
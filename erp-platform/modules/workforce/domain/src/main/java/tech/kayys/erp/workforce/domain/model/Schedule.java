package tech.kayys.erp.workforce.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.workforce.domain.identifier.EmployeeId;
import tech.kayys.erp.workforce.domain.identifier.ScheduleId;
import tech.kayys.erp.workforce.domain.identifier.ShiftId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Schedule aggregate root.
 * Represents an employee's work schedule for a period.
 */
public final class Schedule extends AggregateRoot<ScheduleId> {
    
    private static final long serialVersionUID = 1L;
    
    private EmployeeId employeeId;
    private String employeeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<ScheduledDay> scheduledDays;
    private String status;
    private String notes;
    private boolean active;
    private String createdBy;
    private String approvedBy;
    private Instant approvedAt;

    private Schedule(ScheduleId id) {
        super(id);
        this.scheduledDays = new ArrayList<>();
        this.status = "DRAFT";
        this.active = true;
    }

    private Schedule() {
        super();
    }

    /**
     * Factory method to create a new schedule.
     */
    public static Schedule create(
            ScheduleId id,
            EmployeeId employeeId,
            String employeeName,
            LocalDate startDate,
            LocalDate endDate,
            String createdBy) {
        Schedule schedule = new Schedule(id);
        schedule.employeeId = employeeId;
        schedule.employeeName = employeeName;
        schedule.startDate = startDate;
        schedule.endDate = endDate;
        schedule.createdBy = createdBy;
        return schedule;
    }

    /**
     * Adds a scheduled day.
     */
    public void addScheduledDay(ScheduledDay day) {
        scheduledDays.add(day);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes a scheduled day.
     */
    public void removeScheduledDay(LocalDate date) {
        scheduledDays.removeIf(d -> d.getDate().equals(date));
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Submits the schedule for approval.
     */
    public void submitForApproval() {
        if (!"DRAFT".equals(status)) {
            throw new IllegalStateException("Cannot submit schedule in status: " + status);
        }
        this.status = "PENDING_APPROVAL";
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Approves the schedule.
     */
    public void approve(String approvedBy) {
        if (!"PENDING_APPROVAL".equals(status)) {
            throw new IllegalStateException("Cannot approve schedule in status: " + status);
        }
        this.status = "APPROVED";
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Rejects the schedule.
     */
    public void reject(String reason) {
        if (!"PENDING_APPROVAL".equals(status)) {
            throw new IllegalStateException("Cannot reject schedule in status: " + status);
        }
        this.status = "REJECTED";
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the schedule.
     */
    public void cancel(String reason) {
        if ("APPROVED".equals(status)) {
            throw new IllegalStateException("Cannot cancel approved schedule");
        }
        this.status = "CANCELLED";
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets a scheduled day for a specific date.
     */
    public ScheduledDay getScheduledDay(LocalDate date) {
        return scheduledDays.stream()
            .filter(d -> d.getDate().equals(date))
            .findFirst()
            .orElse(null);
    }

    /**
     * Gets the total scheduled hours.
     */
    public double getTotalScheduledHours() {
        return scheduledDays.stream()
            .mapToDouble(ScheduledDay::getHours)
            .sum();
    }

    // Getters
    public EmployeeId getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public List<ScheduledDay> getScheduledDays() { return Collections.unmodifiableList(scheduledDays); }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }
    public String getCreatedBy() { return createdBy; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + getId() +
                ", employeeName='" + employeeName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }

    /**
     * Scheduled day value object.
     */
    public static final class ScheduledDay implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final LocalDate date;
        private final ShiftId shiftId;
        private final String shiftName;
        private final double hours;
        private final String notes;

        public ScheduledDay(LocalDate date, ShiftId shiftId, String shiftName, double hours, String notes) {
            this.date = date;
            this.shiftId = shiftId;
            this.shiftName = shiftName;
            this.hours = hours;
            this.notes = notes;
            validate();
        }

        @Override
        public void validate() {
            if (date == null) {
                throw new IllegalArgumentException("Date cannot be null");
            }
            if (shiftId == null) {
                throw new IllegalArgumentException("Shift ID cannot be null");
            }
            if (hours < 0) {
                throw new IllegalArgumentException("Hours cannot be negative");
            }
        }

        public LocalDate getDate() { return date; }
        public ShiftId getShiftId() { return shiftId; }
        public String getShiftName() { return shiftName; }
        public double getHours() { return hours; }
        public String getNotes() { return notes; }

        @Override
        public String toString() {
            return "ScheduledDay{" +
                    "date=" + date +
                    ", shiftName='" + shiftName + '\'' +
                    ", hours=" + hours +
                    '}';
        }
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

    <module>modules/payroll/domain</module>
    <module>modules/payroll/application</module>
    <module>modules/payroll/infrastructure</module>
    <module>modules/payroll/interfaces</module>

    <module>modules/hris/domain</module>
    <module>modules/hris/application</module>
    <module>modules/hris/infrastructure</module>
    <module>modules/hris/interfaces</module>

    <module>modules/inventory/domain</module>
    <module>modules/inventory/application</module>
    <module>modules/inventory/infrastructure</module>
    <module>modules/inventory/interfaces</module>

    <module>modules/stockopname/domain</module>
    <module>modules/stockopname/application</module>
    <module>modules/stockopname/infrastructure</module>
    <module>modules/stockopname/interfaces</module>

    <module>modules/warehouse/domain</module>
    <module>modules/warehouse/application</module>
    <module>modules/warehouse/infrastructure</module>
    <module>modules/warehouse/interfaces</module>

    <module>modules/crm/domain</module>
    <module>modules/crm/application</module>
    <module>modules/crm/infrastructure</module>
    <module>modules/crm/interfaces</module>

    <module>modules/tenant/domain</module>
    <module>modules/tenant/application</module>
    <module>modules/tenant/infrastructure</module>
    <module>modules/tenant/interfaces</module>

    <module>modules/compliance/domain</module>
    <module>modules/compliance/application</module>
    <module>modules/compliance/infrastructure</module>
    <module>modules/compliance/interfaces</module>

    <module>modules/communication/domain</module>
    <module>modules/communication/application</module>
    <module>modules/communication/infrastructure</module>
    <module>modules/communication/interfaces</module>

    <module>modules/asset/domain</module>
    <module>modules/asset/application</module>
    <module>modules/asset/infrastructure</module>
    <module>modules/asset/interfaces</module>

    <module>modules/workforce/domain</module>
    <module>modules/workforce/application</module>
    <module>modules/workforce/infrastructure</module>
    <module>modules/workforce/interfaces</module>
</modules>
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
    private String employeeNumber;
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
    private String createdBy;
    private String modifiedBy;

    private AttendanceRecord(AttendanceId id) {
        super(id);
        this.status = AttendanceStatus.PRESENT;
        this.active = true;
        this.approved = false;
        this.date = LocalDate.now();
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
            String employeeNumber,
            LocalDate date,
            String location,
            String createdBy) {
        AttendanceRecord record = new AttendanceRecord(id);
        record.employeeId = employeeId;
        record.employeeName = employeeName;
        record.employeeNumber = employeeNumber;
        record.date = date;
        record.location = location;
        record.createdBy = createdBy;
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

    /**
     * Gets the employee name.
     */
    public String getEmployeeName() { return employeeName; }

    /**
     * Gets the employee ID.
     */
    public EmployeeId getEmployeeId() { return employeeId; }

    /**
     * Gets the employee number.
     */
    public String getEmployeeNumber() { return employeeNumber; }

    /**
     * Gets the date.
     */
    public LocalDate getDate() { return date; }

    /**
     * Gets the clock in time.
     */
    public LocalTime getClockInTime() { return clockInTime; }

    /**
     * Gets the clock out time.
     */
    public LocalTime getClockOutTime() { return clockOutTime; }

    /**
     * Gets the break start time.
     */
    public LocalTime getBreakStartTime() { return breakStartTime; }

    /**
     * Gets the break end time.
     */
    public LocalTime getBreakEndTime() { return breakEndTime; }

    /**
     * Gets the total hours.
     */
    public double getTotalHours() { return totalHours; }

    /**
     * Gets the regular hours.
     */
    public double getRegularHours() { return regularHours; }

    /**
     * Gets the overtime hours.
     */
    public double getOvertimeHours() { return overtimeHours; }

    /**
     * Gets the break hours.
     */
    public double getBreakHours() { return breakHours; }

    /**
     * Gets the status.
     */
    public AttendanceStatus getStatus() { return status; }

    /**
     * Gets the shift ID.
     */
    public String getShiftId() { return shiftId; }

    /**
     * Gets the shift name.
     */
    public String getShiftName() { return shiftName; }

    /**
     * Gets the location.
     */
    public String getLocation() { return location; }

    /**
     * Gets the department.
     */
    public String getDepartment() { return department; }

    /**
     * Gets the notes.
     */
    public String getNotes() { return notes; }

    /**
     * Checks if approved.
     */
    public boolean isApproved() { return approved; }

    /**
     * Gets the approved by.
     */
    public String getApprovedBy() { return approvedBy; }

    /**
     * Gets the approved at.
     */
    public Instant getApprovedAt() { return approvedAt; }

    /**
     * Checks if active.
     */
    public boolean isActive() { return active; }

    /**
     * Gets the created by.
     */
    public String getCreatedBy() { return createdBy; }

    /**
     * Gets the modified by.
     */
    public String getModifiedBy() { return modifiedBy; }

    // Setters
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

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "AttendanceRecord{" +
                "id=" + getId() +
                ", employeeName='" + employeeName + '\'' +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", date=" + date +
                ", totalHours=" + totalHours +
                ", status=" + status +
                ", approved=" + approved +
                '}';
    }
}
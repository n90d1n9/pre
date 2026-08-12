package tech.kayys.erp.workforce.infrastructure.persistence.entity;

import tech.kayys.erp.foundation.persistence.BaseEntity;
import tech.kayys.erp.workforce.domain.valueobject.AttendanceStatus;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Attendance entity for persistence.
 */
@Entity
@Table(name = "workforce_attendance", indexes = {
    @Index(name = "idx_attendance_employee", columnList = "employee_id"),
    @Index(name = "idx_attendance_date", columnList = "attendance_date"),
    @Index(name = "idx_attendance_status", columnList = "status"),
    @Index(name = "idx_attendance_approved", columnList = "approved")
})
public class AttendanceEntity extends BaseEntity {

    @Column(name = "employee_id", nullable = false, columnDefinition = "UUID")
    public UUID employeeId;

    @Column(name = "employee_name", length = 100)
    public String employeeName;

    @Column(name = "employee_number", length = 50)
    public String employeeNumber;

    @Column(name = "attendance_date", nullable = false)
    public LocalDate date;

    @Column(name = "clock_in_time")
    public LocalTime clockInTime;

    @Column(name = "clock_out_time")
    public LocalTime clockOutTime;

    @Column(name = "break_start_time")
    public LocalTime breakStartTime;

    @Column(name = "break_end_time")
    public LocalTime breakEndTime;

    @Column(name = "total_hours")
    public double totalHours;

    @Column(name = "regular_hours")
    public double regularHours;

    @Column(name = "overtime_hours")
    public double overtimeHours;

    @Column(name = "break_hours")
    public double breakHours;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    public AttendanceStatus status;

    @Column(name = "shift_id", length = 50)
    public String shiftId;

    @Column(name = "shift_name", length = 100)
    public String shiftName;

    @Column(name = "location", length = 100)
    public String location;

    @Column(name = "department", length = 100)
    public String department;

    @Column(name = "notes", length = 2000)
    public String notes;

    @Column(name = "approved", nullable = false)
    public boolean approved;

    @Column(name = "approved_by", length = 100)
    public String approvedBy;

    @Column(name = "approved_at")
    public Instant approvedAt;

    @Column(name = "created_by", length = 100)
    public String createdBy;

    @Column(name = "modified_by", length = 100)
    public String modifiedBy;
}
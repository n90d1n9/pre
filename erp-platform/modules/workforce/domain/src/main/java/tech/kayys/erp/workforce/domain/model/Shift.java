package tech.kayys.erp.workforce.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.workforce.domain.identifier.ShiftId;
import tech.kayys.erp.workforce.domain.valueobject.ShiftType;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shift aggregate root.
 * Represents a work shift definition.
 */
public final class Shift extends AggregateRoot<ShiftId> {
    
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String name;
    private String description;
    private ShiftType shiftType;
    private LocalTime startTime;
    private LocalTime endTime;
    private double breakDurationMinutes;
    private List<String> daysOfWeek;
    private boolean active;
    private String department;
    private double hourlyRate;
    private double overtimeRate;

    private Shift(ShiftId id) {
        super(id);
        this.daysOfWeek = new ArrayList<>();
        this.active = true;
        this.breakDurationMinutes = 30;
    }

    private Shift() {
        super();
    }

    /**
     * Factory method to create a new shift.
     */
    public static Shift create(
            ShiftId id,
            String code,
            String name,
            ShiftType shiftType,
            LocalTime startTime,
            LocalTime endTime) {
        Shift shift = new Shift(id);
        shift.code = code;
        shift.name = name;
        shift.shiftType = shiftType;
        shift.startTime = startTime;
        shift.endTime = endTime;
        return shift;
    }

    /**
     * Adds a day of week to the shift.
     */
    public void addDayOfWeek(String day) {
        if (!daysOfWeek.contains(day)) {
            daysOfWeek.add(day);
            setUpdatedAt(java.time.Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a day of week from the shift.
     */
    public void removeDayOfWeek(String day) {
        daysOfWeek.remove(day);
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    /**
     * Sets the shift days.
     */
    public void setDaysOfWeek(List<String> days) {
        this.daysOfWeek = new ArrayList<>(days);
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    /**
     * Activates the shift.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the shift.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    /**
     * Gets the shift duration in hours.
     */
    public double getDurationHours() {
        if (startTime == null || endTime == null) {
            return 0.0;
        }
        long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
        return minutes / 60.0;
    }

    /**
     * Calculates overtime rate based on shift type.
     */
    public double getOvertimeMultiplier() {
        return switch (shiftType) {
            case NIGHT -> 1.5;
            case WEEKEND -> 1.5;
            case HOLIDAY -> 2.0;
            default -> 1.0;
        };
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ShiftType getShiftType() { return shiftType; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public double getBreakDurationMinutes() { return breakDurationMinutes; }
    public List<String> getDaysOfWeek() { return Collections.unmodifiableList(daysOfWeek); }
    public boolean isActive() { return active; }
    public String getDepartment() { return department; }
    public double getHourlyRate() { return hourlyRate; }
    public double getOvertimeRate() { return overtimeRate; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    public void setBreakDurationMinutes(double breakDurationMinutes) {
        this.breakDurationMinutes = breakDurationMinutes;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    public void setDepartment(String department) {
        this.department = department;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    public void setOvertimeRate(double overtimeRate) {
        this.overtimeRate = overtimeRate;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Shift{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", shiftType=" + shiftType +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
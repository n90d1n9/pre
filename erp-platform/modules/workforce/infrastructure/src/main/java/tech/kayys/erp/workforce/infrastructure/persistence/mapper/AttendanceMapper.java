package tech.kayys.erp.workforce.infrastructure.persistence.mapper;

import tech.kayys.erp.workforce.domain.identifier.AttendanceId;
import tech.kayys.erp.workforce.domain.identifier.EmployeeId;
import tech.kayys.erp.workforce.domain.model.AttendanceRecord;
import tech.kayys.erp.workforce.infrastructure.persistence.entity.AttendanceEntity;

import javax.enterprise.context.ApplicationScoped;

/**
 * Mapper between AttendanceRecord domain and persistence entities.
 */
@ApplicationScoped
public class AttendanceMapper {

    public AttendanceEntity toEntity(AttendanceRecord record) {
        AttendanceEntity entity = new AttendanceEntity();
        entity.id = record.getId().getValue();
        entity.employeeId = record.getEmployeeId().getValue();
        entity.employeeName = record.getEmployeeName();
        entity.employeeNumber = record.getEmployeeNumber();
        entity.date = record.getDate();
        entity.clockInTime = record.getClockInTime();
        entity.clockOutTime = record.getClockOutTime();
        entity.breakStartTime = record.getBreakStartTime();
        entity.breakEndTime = record.getBreakEndTime();
        entity.totalHours = record.getTotalHours();
        entity.regularHours = record.getRegularHours();
        entity.overtimeHours = record.getOvertimeHours();
        entity.breakHours = record.getBreakHours();
        entity.status = record.getStatus();
        entity.shiftId = record.getShiftId();
        entity.shiftName = record.getShiftName();
        entity.location = record.getLocation();
        entity.department = record.getDepartment();
        entity.notes = record.getNotes();
        entity.approved = record.isApproved();
        entity.approvedBy = record.getApprovedBy();
        entity.approvedAt = record.getApprovedAt();
        entity.createdBy = record.getCreatedBy();
        entity.modifiedBy = record.getModifiedBy();
        entity.active = record.isActive();
        entity.version = record.getVersion();
        entity.createdAt = record.getCreatedAt();
        entity.updatedAt = record.getUpdatedAt();
        return entity;
    }

    public AttendanceRecord toDomain(AttendanceEntity entity) {
        AttendanceRecord record = new AttendanceRecord(AttendanceId.of(entity.id));
        record.setEmployeeId(EmployeeId.of(entity.employeeId));
        record.setEmployeeName(entity.employeeName);
        record.setEmployeeNumber(entity.employeeNumber);
        record.setDate(entity.date);
        record.setClockInTime(entity.clockInTime);
        record.setClockOutTime(entity.clockOutTime);
        record.setBreakStartTime(entity.breakStartTime);
        record.setBreakEndTime(entity.breakEndTime);
        record.setTotalHours(entity.totalHours);
        record.setRegularHours(entity.regularHours);
        record.setOvertimeHours(entity.overtimeHours);
        record.setBreakHours(entity.breakHours);
        record.setStatus(entity.status);
        record.setShiftId(entity.shiftId);
        record.setShiftName(entity.shiftName);
        record.setLocation(entity.location);
        record.setDepartment(entity.department);
        record.setNotes(entity.notes);
        record.setApproved(entity.approved);
        record.setApprovedBy(entity.approvedBy);
        record.setApprovedAt(entity.approvedAt);
        record.setCreatedBy(entity.createdBy);
        record.setModifiedBy(entity.modifiedBy);
        record.setActive(entity.active);
        record.setVersion(entity.version);
        record.setCreatedAt(entity.createdAt);
        record.setUpdatedAt(entity.updatedAt);
        return record;
    }
}
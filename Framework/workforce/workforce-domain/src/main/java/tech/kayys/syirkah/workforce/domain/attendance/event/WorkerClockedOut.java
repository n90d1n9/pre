package tech.kayys.syirkah.workforce.domain.attendance.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.attendance.AttendanceRecordId;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record WorkerClockedOut(
        UUID eventId,
        Instant occurredAt,
        AttendanceRecordId attendanceRecordId,
        WorkerId workerId,
        EmploymentId employmentId,
        LocalDateTime timestamp
) implements DomainEvent {
    public WorkerClockedOut(AttendanceRecordId id, WorkerId workerId, EmploymentId employmentId, LocalDateTime timestamp) {
        this(UUID.randomUUID(), Instant.now(), id, workerId, employmentId, timestamp);
    }
    @Override public String eventType() { return "workforce.attendance.clocked-out"; }
}

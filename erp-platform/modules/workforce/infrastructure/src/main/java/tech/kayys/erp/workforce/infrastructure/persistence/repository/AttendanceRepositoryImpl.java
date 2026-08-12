package tech.kayys.erp.workforce.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import tech.kayys.erp.workforce.domain.identifier.AttendanceId;
import tech.kayys.erp.workforce.domain.identifier.EmployeeId;
import tech.kayys.erp.workforce.domain.model.AttendanceRecord;
import tech.kayys.erp.workforce.domain.repository.AttendanceRepository;
import tech.kayys.erp.workforce.domain.valueobject.AttendanceStatus;
import tech.kayys.erp.workforce.infrastructure.persistence.entity.AttendanceEntity;
import tech.kayys.erp.workforce.infrastructure.persistence.mapper.AttendanceMapper;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Implementation of AttendanceRepository using Hibernate Reactive Panache.
 */
@ApplicationScoped
public class AttendanceRepositoryImpl implements AttendanceRepository {

    private final AttendanceMapper mapper;

    public AttendanceRepositoryImpl(AttendanceMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @WithTransaction
    public CompletionStage<AttendanceRecord> save(AttendanceRecord record) {
        AttendanceEntity entity = mapper.toEntity(record);
        
        if (entity.id != null) {
            return Panache.withTransaction(() -> entity.<AttendanceEntity>persist()
                .onItem()
                .transform(v -> {
                    record.clearEvents();
                    return record;
                })
                .subscribe()
                .asCompletionStage());
        } else {
            entity.id = UUID.randomUUID();
            return Panache.withTransaction(() -> entity.<AttendanceEntity>persist()
                .onItem()
                .transform(v -> {
                    record.clearEvents();
                    return record;
                })
                .subscribe()
                .asCompletionStage());
        }
    }

    @Override
    @WithSession
    public CompletionStage<Optional<AttendanceRecord>> findById(AttendanceId id) {
        return AttendanceEntity.<AttendanceEntity>findById(id.getValue())
            .onItem()
            .transform(entity -> {
                if (entity == null) {
                    return Optional.empty();
                }
                return Optional.of(mapper.toDomain(entity));
            })
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Boolean> existsById(AttendanceId id) {
        return AttendanceEntity.<AttendanceEntity>findById(id.getValue())
            .onItem()
            .transform(entity -> entity != null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> delete(AttendanceRecord record) {
        return AttendanceEntity.deleteById(record.getId().getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> deleteById(AttendanceId id) {
        return AttendanceEntity.deleteById(id.getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<AttendanceRecord>> findByEmployee(EmployeeId employeeId) {
        return AttendanceEntity.list("employeeId = ?1 order by date desc", employeeId.getValue())
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<AttendanceRecord>> findByEmployeeAndDateRange(
            EmployeeId employeeId, LocalDate start, LocalDate end) {
        return AttendanceEntity.list("employeeId = ?1 and date between ?2 and ?3 order by date desc", 
                employeeId.getValue(), start, end)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<AttendanceRecord>> findByDate(LocalDate date) {
        return AttendanceEntity.list("date = ?1 order by clockInTime", date)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<AttendanceRecord>> findByDateRange(LocalDate start, LocalDate end) {
        return AttendanceEntity.list("date between ?1 and ?2 order by date desc, clockInTime", start, end)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<AttendanceRecord>> findByStatus(AttendanceStatus status) {
        return AttendanceEntity.list("status = ?1", status)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<AttendanceRecord>> findByEmployeeAndStatus(
            EmployeeId employeeId, AttendanceStatus status) {
        return AttendanceEntity.list("employeeId = ?1 and status = ?2", 
                employeeId.getValue(), status)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<AttendanceRecord>> findPendingApproval() {
        return AttendanceEntity.list("approved = false and clockOutTime is not null")
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<AttendanceRecord> findByEmployeeAndDate(
            EmployeeId employeeId, LocalDate date) {
        return AttendanceEntity.find("employeeId = ?1 and date = ?2", 
                employeeId.getValue(), date)
            .firstResult()
            .onItem()
            .transform(entity -> entity != null ? mapper.toDomain(entity) : null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Long> countByStatus(AttendanceStatus status) {
        return AttendanceEntity.count("status = ?1", status)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Long> countByEmployeeAndDateRange(
            EmployeeId employeeId, LocalDate start, LocalDate end) {
        return AttendanceEntity.count("employeeId = ?1 and date between ?2 and ?3", 
                employeeId.getValue(), start, end)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Double> getTotalHoursByEmployeeAndDateRange(
            EmployeeId employeeId, LocalDate start, LocalDate end) {
        return AttendanceEntity.find("select sum(totalHours) from AttendanceEntity where employeeId = ?1 and date between ?2 and ?3", 
                employeeId.getValue(), start, end)
            .firstResult()
            .onItem()
            .transform(result -> result != null ? (Double) result : 0.0)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Double> getOvertimeHoursByEmployeeAndDateRange(
            EmployeeId employeeId, LocalDate start, LocalDate end) {
        return AttendanceEntity.find("select sum(overtimeHours) from AttendanceEntity where employeeId = ?1 and date between ?2 and ?3", 
                employeeId.getValue(), start, end)
            .firstResult()
            .onItem()
            .transform(result -> result != null ? (Double) result : 0.0)
            .subscribe()
            .asCompletionStage();
    }
}
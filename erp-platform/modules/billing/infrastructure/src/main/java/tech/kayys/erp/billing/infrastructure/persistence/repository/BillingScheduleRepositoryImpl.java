package tech.kayys.erp.billing.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import tech.kayys.erp.billing.domain.identifier.BillingScheduleId;
import tech.kayys.erp.billing.domain.model.BillingSchedule;
import tech.kayys.erp.billing.domain.repository.BillingScheduleRepository;
import tech.kayys.erp.billing.domain.valueobject.BillingStatus;
import tech.kayys.erp.billing.infrastructure.persistence.entity.BillingScheduleEntity;
import tech.kayys.erp.billing.domain.valueobject.Money;
import tech.kayys.erp.foundation.persistence.BaseRepository;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Reactive repository implementation for BillingSchedule.
 */
@ApplicationScoped
public class BillingScheduleRepositoryImpl extends BaseRepository<BillingScheduleEntity> 
        implements BillingScheduleRepository {

    @Override
    @WithTransaction
    public Uni<BillingSchedule> save(BillingSchedule schedule) {
        BillingScheduleEntity entity = BillingScheduleEntity.fromDomain(schedule);
        
        if (entity.id != null) {
            return findById(entity.id)
                .chain(existing -> {
                    if (existing == null) {
                        return Uni.createFrom().failure(
                            new IllegalArgumentException("Billing schedule not found: " + schedule.getId())
                        );
                    }
                    // Update fields
                    existing.subscriptionId = entity.subscriptionId;
                    existing.customerId = entity.customerId;
                    existing.customerEmail = entity.customerEmail;
                    existing.frequency = entity.frequency;
                    existing.status = entity.status;
                    existing.startDate = entity.startDate;
                    existing.endDate = entity.endDate;
                    existing.nextBillingDate = entity.nextBillingDate;
                    existing.lastBillingDate = entity.lastBillingDate;
                    existing.amount = entity.amount;
                    existing.currency = entity.currency;
                    existing.paymentMethodToken = entity.paymentMethodToken;
                    existing.currentCycle = entity.currentCycle;
                    existing.totalCycles = entity.totalCycles;
                    existing.failedPaymentCount = entity.failedPaymentCount;
                    existing.maxFailedPayments = entity.maxFailedPayments;
                    existing.sendEmailNotifications = entity.sendEmailNotifications;
                    existing.sendSmsNotifications = entity.sendSmsNotifications;
                    existing.active = entity.active;
                    existing.updatedAt = entity.updatedAt;
                    existing.version = entity.version;
                    
                    return persist(existing)
                        .onItem()
                        .transform(v -> {
                            schedule.clearEvents();
                            return schedule;
                        });
                });
        } else {
            return persist(entity)
                .onItem()
                .transform(v -> {
                    schedule.clearEvents();
                    return schedule;
                });
        }
    }

    @Override
    public Uni<Optional<BillingSchedule>> findById(BillingScheduleId id) {
        return findByIdOptional(id.getValue())
            .onItem()
            .transform(entityOpt -> entityOpt.map(BillingScheduleEntity::toDomain));
    }

    @Override
    public Uni<Boolean> existsById(BillingScheduleId id) {
        return findById(id)
            .onItem()
            .transform(opt -> opt.isPresent());
    }

    @Override
    @WithTransaction
    public Uni<Void> delete(BillingSchedule schedule) {
        return deleteById(schedule.getId().getValue())
            .onItem()
            .transform(v -> null);
    }

    @Override
    @WithTransaction
    public Uni<Void> deleteById(BillingScheduleId id) {
        return deleteById(id.getValue())
            .onItem()
            .transform(v -> null);
    }

    @Override
    public Uni<Optional<BillingSchedule>> findBySubscriptionId(UUID subscriptionId) {
        return find("subscriptionId = ?1", subscriptionId)
            .firstResult()
            .onItem()
            .transform(entity -> entity != null ? Optional.of(entity.toDomain()) : Optional.empty());
    }

    @Override
    public Uni<List<BillingSchedule>> findByCustomerId(String customerId) {
        return find("customerId = ?1 order by startDate desc", customerId)
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(BillingScheduleEntity::toDomain)
                .collect(Collectors.toList())
            );
    }

    @Override
    public Uni<List<BillingSchedule>> findByStatus(BillingStatus status) {
        return find("status = ?1 order by nextBillingDate asc", status.name())
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(BillingScheduleEntity::toDomain)
                .collect(Collectors.toList())
            );
    }

    @Override
    public Uni<List<BillingSchedule>> findDueSchedules() {
        Instant now = Instant.now();
        return find("status = 'ACTIVE' and nextBillingDate <= ?1 order by nextBillingDate asc", now)
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(BillingScheduleEntity::toDomain)
                .collect(Collectors.toList())
            );
    }

    @Override
    public Uni<List<BillingSchedule>> findSchedulesWithPaymentFailures() {
        return find("status = 'ACTIVE' and failedPaymentCount > 0 order by failedPaymentCount desc")
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(BillingScheduleEntity::toDomain)
                .collect(Collectors.toList())
            );
    }

    @Override
    public Uni<List<BillingSchedule>> findUpcomingBilling(int daysAhead) {
        Instant now = Instant.now();
        Instant future = now.plusSeconds(daysAhead * 24L * 60L * 60L);
        return find("status = 'ACTIVE' and nextBillingDate between ?1 and ?2 order by nextBillingDate asc", now, future)
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(BillingScheduleEntity::toDomain)
                .collect(Collectors.toList())
            );
    }

    @Override
    public Uni<BillingStatistics> getStatistics(Instant fromDate, Instant toDate) {
        // This is a complex query - simplified for demonstration
        return count("status = 'ACTIVE'")
            .chain(activeCount -> {
                // Get total due amount
                return find("status = 'ACTIVE' and nextBillingDate between ?1 and ?2", fromDate, toDate)
                    .list()
                    .onItem()
                    .transform(entities -> {
                        long totalActive = activeCount;
                        Money totalDue = entities.stream()
                            .map(entity -> new Money(entity.amount, 
                                java.util.Currency.getInstance(entity.currency)))
                            .reduce(Money.zero("USD"), Money::add);
                        
                        // For simplicity, assume all due are collected
                        Money totalCollected = totalDue;
                        
                        return new BillingStatistics(
                            fromDate,
                            toDate,
                            totalActive,
                            totalDue,
                            totalCollected,
                            0, // failed payments
                            100.0, // success rate
                            totalDue.multiply(java.math.BigDecimal.valueOf(0.1)) // average revenue
                        );
                    });
            });
    }

    /**
     * Finds expired billing schedules.
     */
    public Uni<List<BillingSchedule>> findExpiredSchedules() {
        Instant now = Instant.now();
        return find("status = 'ACTIVE' and endDate is not null and endDate <= ?1", now)
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(BillingScheduleEntity::toDomain)
                .collect(Collectors.toList())
            );
    }

    /**
     * Updates billing schedule status.
     */
    @WithTransaction
    public Uni<BillingSchedule> updateStatus(UUID scheduleId, BillingStatus newStatus) {
        return findById(scheduleId)
            .chain(entity -> {
                if (entity == null) {
                    return Uni.createFrom().failure(
                        new IllegalArgumentException("Billing schedule not found: " + scheduleId)
                    );
                }
                entity.status = newStatus.name();
                entity.updatedAt = Instant.now();
                return persist(entity)
                    .onItem()
                    .transform(BillingScheduleEntity::toDomain);
            });
    }
}
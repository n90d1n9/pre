package tech.kayys.erp.billing.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import tech.kayys.erp.billing.domain.identifier.UsageRecordId;
import tech.kayys.erp.billing.domain.model.UsageRecord;
import tech.kayys.erp.billing.domain.repository.UsageRecordRepository;
import tech.kayys.erp.billing.infrastructure.persistence.entity.UsageRecordEntity;
import tech.kayys.erp.foundation.persistence.BaseRepository;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class UsageRecordRepositoryImpl extends BaseRepository<UsageRecordEntity> 
        implements UsageRecordRepository {

    @Override
    @WithTransaction
    public Uni<UsageRecord> save(UsageRecord record) {
        UsageRecordEntity entity = UsageRecordEntity.fromDomain(record);
        return persist(entity)
            .onItem()
            .transform(v -> {
                record.clearEvents();
                return record;
            });
    }

    @Override
    public Uni<Optional<UsageRecord>> findById(UsageRecordId id) {
        return findByIdOptional(id.getValue())
            .onItem()
            .transform(entityOpt -> entityOpt.map(UsageRecordEntity::toDomain));
    }

    @Override
    public Uni<Boolean> existsById(UsageRecordId id) {
        return findById(id)
            .onItem()
            .transform(opt -> opt.isPresent());
    }

    @Override
    @WithTransaction
    public Uni<Void> delete(UsageRecord record) {
        return deleteById(record.getId().getValue())
            .onItem()
            .transform(v -> null);
    }

    @Override
    @WithTransaction
    public Uni<Void> deleteById(UsageRecordId id) {
        return deleteById(id.getValue())
            .onItem()
            .transform(v -> null);
    }

    @Override
    public Uni<List<UsageRecord>> findByCustomerId(String customerId) {
        return find("customerId = ?1 order by usageDate desc", customerId)
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(UsageRecordEntity::toDomain)
                .collect(Collectors.toList())
            );
    }

    @Override
    public Uni<List<UsageRecord>> findBySubscriptionId(String subscriptionId) {
        return find("subscriptionId = ?1 order by usageDate desc", subscriptionId)
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(UsageRecordEntity::toDomain)
                .collect(Collectors.toList())
            );
    }

    @Override
    public Uni<List<UsageRecord>> findUninvoicedUsage(String customerId) {
        return find("customerId = ?1 and invoiced = false order by usageDate asc", customerId)
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(UsageRecordEntity::toDomain)
                .collect(Collectors.toList())
            );
    }

    @Override
    public Uni<List<UsageRecord>> findByDateRange(Instant start, Instant end) {
        return find("usageDate between ?1 and ?2 order by usageDate asc", start, end)
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(UsageRecordEntity::toDomain)
                .collect(Collectors.toList())
            );
    }

    @Override
    public Uni<Double> getTotalUsage(String customerId, String meterId, Instant start, Instant end) {
        return find("customerId = ?1 and meterId = ?2 and usageDate between ?3 and ?4", 
                    customerId, meterId, start, end)
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .mapToDouble(UsageRecordEntity::getQuantity)
                .sum()
            );
    }
}
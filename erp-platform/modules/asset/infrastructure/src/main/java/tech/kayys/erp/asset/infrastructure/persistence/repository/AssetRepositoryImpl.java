package tech.kayys.erp.asset.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import tech.kayys.erp.asset.domain.identifier.AssetId;
import tech.kayys.erp.asset.domain.identifier.AssetCategoryId;
import tech.kayys.erp.asset.domain.model.Asset;
import tech.kayys.erp.asset.domain.repository.AssetRepository;
import tech.kayys.erp.asset.domain.valueobject.AssetStatus;
import tech.kayys.erp.asset.domain.valueobject.AssetType;
import tech.kayys.erp.asset.infrastructure.persistence.entity.AssetEntity;
import tech.kayys.erp.asset.infrastructure.persistence.mapper.AssetMapper;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Implementation of AssetRepository using Hibernate Reactive Panache.
 */
@ApplicationScoped
public class AssetRepositoryImpl implements AssetRepository {

    private final AssetMapper mapper;

    public AssetRepositoryImpl(AssetMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @WithTransaction
    public CompletionStage<Asset> save(Asset asset) {
        AssetEntity entity = mapper.toEntity(asset);
        
        if (entity.id != null) {
            return Panache.withTransaction(() -> entity.<AssetEntity>persist()
                .onItem()
                .transform(v -> {
                    asset.clearEvents();
                    return asset;
                })
                .subscribe()
                .asCompletionStage());
        } else {
            entity.id = UUID.randomUUID();
            return Panache.withTransaction(() -> entity.<AssetEntity>persist()
                .onItem()
                .transform(v -> {
                    asset.clearEvents();
                    return asset;
                })
                .subscribe()
                .asCompletionStage());
        }
    }

    @Override
    @WithSession
    public CompletionStage<Optional<Asset>> findById(AssetId id) {
        return AssetEntity.<AssetEntity>findById(id.getValue())
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
    public CompletionStage<Boolean> existsById(AssetId id) {
        return AssetEntity.<AssetEntity>findById(id.getValue())
            .onItem()
            .transform(entity -> entity != null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> delete(Asset asset) {
        return AssetEntity.deleteById(asset.getId().getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> deleteById(AssetId id) {
        return AssetEntity.deleteById(id.getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Asset>> findByStatus(AssetStatus status) {
        return AssetEntity.list("status = ?1", status)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Asset>> findByType(AssetType type) {
        return AssetEntity.list("assetType = ?1", type)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Asset>> findByCategory(AssetCategoryId categoryId) {
        return AssetEntity.list("categoryId = ?1", categoryId.getValue())
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Asset>> findByAssignedTo(String assignedTo) {
        return AssetEntity.list("assignedTo = ?1", UUID.fromString(assignedTo))
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Asset>> findByDepartment(String department) {
        return AssetEntity.list("department = ?1", department)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Asset>> findByLocation(String location) {
        return AssetEntity.list("location = ?1", location)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Asset>> findAcquiredBetween(LocalDate start, LocalDate end) {
        return AssetEntity.list("acquisitionDate between ?1 and ?2", start, end)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Asset>> findAssetsNeedingMaintenance() {
        // Find assets with status ACTIVE and no recent maintenance
        return AssetEntity.list("status = ?1", AssetStatus.ACTIVE)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .filter(asset -> asset.getMaintenanceRecords() == null || 
                    asset.getMaintenanceRecords().isEmpty() ||
                    asset.getMaintenanceRecords().stream()
                        .allMatch(r -> r.getCompletedDate() == null ||
                            r.getCompletedDate().isBefore(LocalDate.now().minusMonths(6))))
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Asset>> findFullyDepreciatedAssets() {
        return AssetEntity.list("currentValue <= salvageValue")
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Asset> findBySerialNumber(String serialNumber) {
        return AssetEntity.find("serialNumber = ?1", serialNumber)
            .firstResult()
            .onItem()
            .transform(entity -> entity != null ? mapper.toDomain(entity) : null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Asset> findByAssetNumber(String assetNumber) {
        return AssetEntity.find("assetNumber = ?1", assetNumber)
            .firstResult()
            .onItem()
            .transform(entity -> entity != null ? mapper.toDomain(entity) : null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Long> countByStatus(AssetStatus status) {
        return AssetEntity.count("status = ?1", status)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Long> countByType(AssetType type) {
        return AssetEntity.count("assetType = ?1", type)
            .subscribe()
            .asCompletionStage();
    }
}
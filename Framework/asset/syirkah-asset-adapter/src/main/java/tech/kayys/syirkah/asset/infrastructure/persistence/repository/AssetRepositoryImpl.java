package tech.kayys.syirkah.asset.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.asset.domain.identifier.AssetId;
import tech.kayys.syirkah.asset.domain.identifier.AssetCategoryId;
import tech.kayys.syirkah.asset.domain.model.Asset;
import tech.kayys.syirkah.asset.domain.repository.AssetRepository;
import tech.kayys.syirkah.asset.domain.valueobject.AssetStatus;
import tech.kayys.syirkah.asset.domain.valueobject.AssetType;
import tech.kayys.syirkah.asset.infrastructure.persistence.entity.AssetEntity;
import tech.kayys.syirkah.asset.infrastructure.persistence.mapper.AssetMapper;

import jakarta.enterprise.context.ApplicationScoped;
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
        if (entity.id == null) {
            entity.id = UUID.randomUUID();
        }
        return Panache.withTransaction(() -> entity.<AssetEntity>persist()
            .map(v -> {
                asset.clearEvents();
                return asset;
            })
        ).subscribe().asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Optional<Asset>> findById(AssetId id) {
        Uni<AssetEntity> uni = AssetEntity.findById(id.getValue());
        return uni
            .map(entity -> entity == null ? Optional.<Asset>empty() : Optional.of(mapper.toDomain(entity)))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Boolean> existsById(AssetId id) {
        Uni<AssetEntity> uni = AssetEntity.findById(id.getValue());
        return uni
            .map(entity -> entity != null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> delete(Asset asset) {
        return deleteById(asset.getId());
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> deleteById(AssetId id) {
        return AssetEntity.deleteById(id.getValue())
            .map(v -> (Void) null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Asset>> findByStatus(AssetStatus status) {
        Uni<List<AssetEntity>> uni = AssetEntity.list("status = ?1", status);
        return uni.map(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Asset>> findByType(AssetType type) {
        Uni<List<AssetEntity>> uni = AssetEntity.list("assetType = ?1", type);
        return uni.map(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Asset>> findByCategory(AssetCategoryId categoryId) {
        Uni<List<AssetEntity>> uni = AssetEntity.list("categoryId = ?1", categoryId.getValue());
        return uni.map(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Asset>> findByAssignedTo(String assignedTo) {
        Uni<List<AssetEntity>> uni = AssetEntity.list("assignedTo = ?1", UUID.fromString(assignedTo));
        return uni.map(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Asset>> findByDepartment(String department) {
        Uni<List<AssetEntity>> uni = AssetEntity.list("department = ?1", department);
        return uni.map(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Asset>> findByLocation(String location) {
        Uni<List<AssetEntity>> uni = AssetEntity.list("location = ?1", location);
        return uni.map(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Asset>> findAcquiredBetween(LocalDate start, LocalDate end) {
        Uni<List<AssetEntity>> uni = AssetEntity.list("acquisitionDate between ?1 and ?2", start, end);
        return uni.map(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Asset>> findAssetsNeedingMaintenance() {
        Uni<List<AssetEntity>> uni = AssetEntity.list("status = ?1", AssetStatus.ACTIVE);
        return uni.map(entities -> entities.stream()
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
        Uni<List<AssetEntity>> uni = AssetEntity.list("currentValue <= salvageValue");
        return uni.map(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Asset> findBySerialNumber(String serialNumber) {
        Uni<AssetEntity> uni = AssetEntity.find("serialNumber = ?1", serialNumber).firstResult();
        return uni.map(entity -> entity != null ? mapper.toDomain(entity) : null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Asset> findByAssetNumber(String assetNumber) {
        Uni<AssetEntity> uni = AssetEntity.find("assetNumber = ?1", assetNumber).firstResult();
        return uni.map(entity -> entity != null ? mapper.toDomain(entity) : null)
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
package tech.kayys.erp.warehouse.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import tech.kayys.erp.warehouse.domain.identifier.BinLocationId;
import tech.kayys.erp.warehouse.domain.identifier.WarehouseId;
import tech.kayys.erp.warehouse.domain.model.BinLocation;
import tech.kayys.erp.warehouse.domain.repository.BinLocationRepository;
import tech.kayys.erp.warehouse.domain.valueobject.BinStatus;
import tech.kayys.erp.warehouse.domain.valueobject.BinType;
import tech.kayys.erp.warehouse.infrastructure.persistence.entity.BinLocationEntity;
import tech.kayys.erp.warehouse.infrastructure.persistence.mapper.BinLocationMapper;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Implementation of BinLocationRepository using Hibernate Reactive Panache.
 */
@ApplicationScoped
public class BinLocationRepositoryImpl implements BinLocationRepository {

    private final BinLocationMapper mapper;

    public BinLocationRepositoryImpl(BinLocationMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @WithTransaction
    public CompletionStage<BinLocation> save(BinLocation binLocation) {
        BinLocationEntity entity = mapper.toEntity(binLocation);
        
        if (entity.id != null) {
            return Panache.withTransaction(() -> entity.<BinLocationEntity>persist()
                .onItem()
                .transform(v -> {
                    binLocation.clearEvents();
                    return binLocation;
                })
                .subscribe()
                .asCompletionStage());
        } else {
            entity.id = UUID.randomUUID();
            return Panache.withTransaction(() -> entity.<BinLocationEntity>persist()
                .onItem()
                .transform(v -> {
                    binLocation.clearEvents();
                    return binLocation;
                })
                .subscribe()
                .asCompletionStage());
        }
    }

    @Override
    @WithSession
    public CompletionStage<Optional<BinLocation>> findById(BinLocationId id) {
        return BinLocationEntity.<BinLocationEntity>findById(id.getValue())
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
    public CompletionStage<Boolean> existsById(BinLocationId id) {
        return BinLocationEntity.<BinLocationEntity>findById(id.getValue())
            .onItem()
            .transform(entity -> entity != null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> delete(BinLocation binLocation) {
        return BinLocationEntity.deleteById(binLocation.getId().getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> deleteById(BinLocationId id) {
        return BinLocationEntity.deleteById(id.getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<BinLocation>> findByWarehouse(WarehouseId warehouseId) {
        return BinLocationEntity.list("warehouseId = ?1", warehouseId.getValue())
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<BinLocation>> findByZone(String zone) {
        return BinLocationEntity.list("zone = ?1", zone)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<BinLocation>> findByType(BinType binType) {
        return BinLocationEntity.list("binType = ?1", binType)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<BinLocation>> findByStatus(BinStatus status) {
        return BinLocationEntity.list("status = ?1", status)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<BinLocation>> findAvailableBins() {
        return BinLocationEntity.list("status = ?1 and occupied < capacity", BinStatus.ACTIVE)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<BinLocation>> findByAssignedProduct(String productId) {
        UUID productUUID = UUID.fromString(productId);
        return BinLocationEntity.find("?1 member of assignedProductIds", productUUID)
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<BinLocation>> findByWarehouseAndZone(
            WarehouseId warehouseId, String zone) {
        return BinLocationEntity.list("warehouseId = ?1 and zone = ?2", 
                warehouseId.getValue(), zone)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<BinLocation>> findByCapacityGreaterThan(int capacity) {
        return BinLocationEntity.list("capacity > ?1", capacity)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Long> countByStatus(BinStatus status) {
        return BinLocationEntity.count("status = ?1", status)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Boolean> isCodeUniqueInWarehouse(String code, WarehouseId warehouseId) {
        return BinLocationEntity.count("code = ?1 and warehouseId = ?2", code, warehouseId.getValue())
            .onItem()
            .transform(count -> count == 0)
            .subscribe()
            .asCompletionStage();
    }
}
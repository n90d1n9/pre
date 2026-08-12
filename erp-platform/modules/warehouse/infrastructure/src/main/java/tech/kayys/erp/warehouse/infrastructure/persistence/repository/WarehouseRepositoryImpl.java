package tech.kayys.erp.warehouse.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import tech.kayys.erp.warehouse.domain.identifier.WarehouseId;
import tech.kayys.erp.warehouse.domain.model.Warehouse;
import tech.kayys.erp.warehouse.domain.repository.WarehouseRepository;
import tech.kayys.erp.warehouse.infrastructure.persistence.entity.WarehouseEntity;
import tech.kayys.erp.warehouse.infrastructure.persistence.mapper.WarehouseMapper;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Implementation of WarehouseRepository using Hibernate Reactive Panache.
 */
@ApplicationScoped
public class WarehouseRepositoryImpl implements WarehouseRepository {

    private final WarehouseMapper mapper;

    public WarehouseRepositoryImpl(WarehouseMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @WithTransaction
    public CompletionStage<Warehouse> save(Warehouse warehouse) {
        WarehouseEntity entity = mapper.toEntity(warehouse);
        
        if (entity.id != null) {
            return Panache.withTransaction(() -> entity.<WarehouseEntity>persist()
                .onItem()
                .transform(v -> {
                    warehouse.clearEvents();
                    return warehouse;
                })
                .subscribe()
                .asCompletionStage());
        } else {
            entity.id = UUID.randomUUID();
            return Panache.withTransaction(() -> entity.<WarehouseEntity>persist()
                .onItem()
                .transform(v -> {
                    warehouse.clearEvents();
                    return warehouse;
                })
                .subscribe()
                .asCompletionStage());
        }
    }

    @Override
    @WithSession
    public CompletionStage<Optional<Warehouse>> findById(WarehouseId id) {
        return WarehouseEntity.<WarehouseEntity>findById(id.getValue())
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
    public CompletionStage<Boolean> existsById(WarehouseId id) {
        return WarehouseEntity.<WarehouseEntity>findById(id.getValue())
            .onItem()
            .transform(entity -> entity != null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> delete(Warehouse warehouse) {
        return WarehouseEntity.deleteById(warehouse.getId().getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> deleteById(WarehouseId id) {
        return WarehouseEntity.deleteById(id.getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Warehouse>> findByNameContaining(String name) {
        return WarehouseEntity.list("name like ?1", "%" + name + "%")
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Warehouse>> findActiveWarehouses() {
        return WarehouseEntity.list("active = true")
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Warehouse> findDefaultWarehouse() {
        return WarehouseEntity.find("defaultWarehouse = true")
            .firstResult()
            .onItem()
            .transform(entity -> entity != null ? mapper.toDomain(entity) : null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Warehouse>> findByCountry(String country) {
        return WarehouseEntity.list("country = ?1", country)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Warehouse> findByCode(String code) {
        return WarehouseEntity.find("code = ?1", code)
            .firstResult()
            .onItem()
            .transform(entity -> entity != null ? mapper.toDomain(entity) : null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Warehouse>> findWarehousesWithCapacity() {
        return WarehouseEntity.list("active = true and capacity > current_stock_count")
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Long> countByCountry(String country) {
        return WarehouseEntity.count("country = ?1", country)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Boolean> isCodeUnique(String code) {
        return WarehouseEntity.count("code = ?1", code)
            .onItem()
            .transform(count -> count == 0)
            .subscribe()
            .asCompletionStage();
    }
}
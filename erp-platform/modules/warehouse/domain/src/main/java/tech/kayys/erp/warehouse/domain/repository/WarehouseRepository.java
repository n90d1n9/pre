package tech.kayys.erp.warehouse.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.warehouse.domain.identifier.WarehouseId;
import tech.kayys.erp.warehouse.domain.model.Warehouse;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for Warehouse aggregates.
 */
public interface WarehouseRepository extends Repository<Warehouse, WarehouseId> {

    /**
     * Finds warehouses by name containing text.
     */
    CompletionStage<List<Warehouse>> findByNameContaining(String name);

    /**
     * Finds active warehouses.
     */
    CompletionStage<List<Warehouse>> findActiveWarehouses();

    /**
     * Finds the default warehouse.
     */
    CompletionStage<Warehouse> findDefaultWarehouse();

    /**
     * Finds warehouses by country.
     */
    CompletionStage<List<Warehouse>> findByCountry(String country);

    /**
     * Finds warehouses by code.
     */
    CompletionStage<Warehouse> findByCode(String code);

    /**
     * Finds warehouses with capacity available.
     */
    CompletionStage<List<Warehouse>> findWarehousesWithCapacity();

    /**
     * Counts warehouses by country.
     */
    CompletionStage<Long> countByCountry(String country);

    /**
     * Checks if a warehouse code is unique.
     */
    CompletionStage<Boolean> isCodeUnique(String code);
}
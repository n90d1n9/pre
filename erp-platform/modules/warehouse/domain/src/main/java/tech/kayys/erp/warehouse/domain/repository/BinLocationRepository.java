package tech.kayys.erp.warehouse.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.warehouse.domain.identifier.BinLocationId;
import tech.kayys.erp.warehouse.domain.identifier.WarehouseId;
import tech.kayys.erp.warehouse.domain.model.BinLocation;
import tech.kayys.erp.warehouse.domain.valueobject.BinStatus;
import tech.kayys.erp.warehouse.domain.valueobject.BinType;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for BinLocation aggregates.
 */
public interface BinLocationRepository extends Repository<BinLocation, BinLocationId> {

    /**
     * Finds bin locations by warehouse.
     */
    CompletionStage<List<BinLocation>> findByWarehouse(WarehouseId warehouseId);

    /**
     * Finds bin locations by zone.
     */
    CompletionStage<List<BinLocation>> findByZone(String zone);

    /**
     * Finds bin locations by type.
     */
    CompletionStage<List<BinLocation>> findByType(BinType binType);

    /**
     * Finds bin locations by status.
     */
    CompletionStage<List<BinLocation>> findByStatus(BinStatus status);

    /**
     * Finds bin locations with available capacity.
     */
    CompletionStage<List<BinLocation>> findAvailableBins();

    /**
     * Finds bin locations by assigned product.
     */
    CompletionStage<List<BinLocation>> findByAssignedProduct(String productId);

    /**
     * Finds bin locations by warehouse and zone.
     */
    CompletionStage<List<BinLocation>> findByWarehouseAndZone(
        WarehouseId warehouseId, String zone
    );

    /**
     * Finds bin locations with capacity greater than a value.
     */
    CompletionStage<List<BinLocation>> findByCapacityGreaterThan(int capacity);

    /**
     * Counts bin locations by status.
     */
    CompletionStage<Long> countByStatus(BinStatus status);

    /**
     * Checks if a bin code is unique in a warehouse.
     */
    CompletionStage<Boolean> isCodeUniqueInWarehouse(String code, WarehouseId warehouseId);
}
package tech.kayys.erp.warehouse.application.api;

import tech.kayys.erp.warehouse.application.api.command.CreateWarehouseCommand;
import tech.kayys.erp.warehouse.application.api.query.WarehouseView;
import tech.kayys.erp.warehouse.domain.identifier.WarehouseId;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Public API for warehouse operations.
 */
public interface WarehouseService {

    /**
     * Creates a new warehouse.
     */
    CompletionStage<WarehouseId> createWarehouse(CreateWarehouseCommand command);

    /**
     * Gets a warehouse by ID.
     */
    CompletionStage<WarehouseView> getWarehouse(WarehouseId warehouseId);

    /**
     * Gets the default warehouse.
     */
    CompletionStage<WarehouseView> getDefaultWarehouse();

    /**
     * Gets all active warehouses.
     */
    CompletionStage<List<WarehouseView>> getActiveWarehouses();

    /**
     * Searches warehouses with filters.
     */
    CompletionStage<List<WarehouseView>> searchWarehouses(
        String name, String country, Boolean active, int page, int size
    );

    /**
     * Activates a warehouse.
     */
    CompletionStage<WarehouseId> activateWarehouse(WarehouseId warehouseId);

    /**
     * Deactivates a warehouse.
     */
    CompletionStage<WarehouseId> deactivateWarehouse(WarehouseId warehouseId);

    /**
     * Updates warehouse stock count.
     */
    CompletionStage<WarehouseId> updateStockCount(WarehouseId warehouseId, int change);

    /**
     * Gets warehouses with available capacity.
     */
    CompletionStage<List<WarehouseView>> getWarehousesWithCapacity();
}
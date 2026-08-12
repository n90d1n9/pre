package tech.kayys.erp.warehouse.application.api;

import tech.kayys.erp.warehouse.application.api.command.CreateInventoryMovementCommand;
import tech.kayys.erp.warehouse.application.api.command.CompleteInventoryMovementCommand;
import tech.kayys.erp.warehouse.application.api.query.InventoryMovementView;
import tech.kayys.erp.warehouse.domain.identifier.InventoryMovementId;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Public API for inventory movement operations.
 */
public interface WarehouseMovementService {

    /**
     * Creates a new inventory movement.
     */
    CompletionStage<InventoryMovementId> createInventoryMovement(CreateInventoryMovementCommand command);

    /**
     * Starts an inventory movement.
     */
    CompletionStage<InventoryMovementId> startMovement(InventoryMovementId movementId);

    /**
     * Completes an inventory movement.
     */
    CompletionStage<InventoryMovementId> completeMovement(CompleteInventoryMovementCommand command);

    /**
     * Cancels an inventory movement.
     */
    CompletionStage<InventoryMovementId> cancelMovement(InventoryMovementId movementId, String reason);

    /**
     * Gets an inventory movement by ID.
     */
    CompletionStage<InventoryMovementView> getInventoryMovement(InventoryMovementId movementId);

    /**
     * Gets movements for a warehouse.
     */
    CompletionStage<List<InventoryMovementView>> getMovementsByWarehouse(
        UUID warehouseId, String status, String type, int page, int size
    );

    /**
     * Gets movements by product.
     */
    CompletionStage<List<InventoryMovementView>> getMovementsByProduct(
        String productId, int page, int size
    );

    /**
     * Gets movements by status.
     */
    CompletionStage<List<InventoryMovementView>> getMovementsByStatus(String status);
}
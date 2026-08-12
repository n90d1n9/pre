package tech.kayys.erp.warehouse.application.api;

import tech.kayys.erp.warehouse.application.api.command.AssignCarrierCommand;
import tech.kayys.erp.warehouse.application.api.command.CreateShippingTaskCommand;
import tech.kayys.erp.warehouse.application.api.command.ShipItemCommand;
import tech.kayys.erp.warehouse.application.api.query.ShippingTaskView;
import tech.kayys.erp.warehouse.domain.identifier.ShippingTaskId;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Public API for warehouse shipping operations.
 */
public interface WarehouseShippingService {

    /**
     * Creates a new shipping task.
     */
    CompletionStage<ShippingTaskId> createShippingTask(CreateShippingTaskCommand command);

    /**
     * Starts packing for a shipping task.
     */
    CompletionStage<ShippingTaskId> startPacking(ShippingTaskId taskId);

    /**
     * Marks a shipping task as ready to ship.
     */
    CompletionStage<ShippingTaskId> readyToShip(ShippingTaskId taskId);

    /**
     * Assigns a carrier to a shipping task.
     */
    CompletionStage<ShippingTaskId> assignCarrier(AssignCarrierCommand command);

    /**
     * Ships an item.
     */
    CompletionStage<ShippingTaskId> shipItem(ShipItemCommand command);

    /**
     * Marks a shipping task as delivered.
     */
    CompletionStage<ShippingTaskId> markDelivered(ShippingTaskId taskId);

    /**
     * Cancels a shipping task.
     */
    CompletionStage<ShippingTaskId> cancelShippingTask(ShippingTaskId taskId, String reason);

    /**
     * Gets a shipping task by ID.
     */
    CompletionStage<ShippingTaskView> getShippingTask(ShippingTaskId taskId);

    /**
     * Gets shipping tasks for a warehouse.
     */
    CompletionStage<List<ShippingTaskView>> getShippingTasksByWarehouse(
        UUID warehouseId, String status, int page, int size
    );

    /**
     * Gets shipping tasks by order reference.
     */
    CompletionStage<List<ShippingTaskView>> getShippingTasksByOrder(String orderReference);

    /**
     * Gets shipping tasks by tracking number.
     */
    CompletionStage<ShippingTaskView> getShippingTaskByTrackingNumber(String trackingNumber);

    /**
     * Gets shipping tasks by carrier.
     */
    CompletionStage<List<ShippingTaskView>> getShippingTasksByCarrier(String carrier);
}
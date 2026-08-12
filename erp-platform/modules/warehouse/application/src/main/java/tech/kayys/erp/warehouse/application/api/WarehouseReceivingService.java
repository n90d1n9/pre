package tech.kayys.erp.warehouse.application.api;

import tech.kayys.erp.warehouse.application.api.command.CompleteQualityCheckCommand;
import tech.kayys.erp.warehouse.application.api.command.CreateReceivingTaskCommand;
import tech.kayys.erp.warehouse.application.api.command.ReceiveItemCommand;
import tech.kayys.erp.warehouse.application.api.query.ReceivingTaskView;
import tech.kayys.erp.warehouse.domain.identifier.ReceivingTaskId;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Public API for warehouse receiving operations.
 */
public interface WarehouseReceivingService {

    /**
     * Creates a new receiving task.
     */
    CompletionStage<ReceivingTaskId> createReceivingTask(CreateReceivingTaskCommand command);

    /**
     * Marks a receiving task as arrived.
     */
    CompletionStage<ReceivingTaskId> markArrived(ReceivingTaskId taskId);

    /**
     * Starts quality check for a receiving task.
     */
    CompletionStage<ReceivingTaskId> startQualityCheck(ReceivingTaskId taskId);

    /**
     * Completes quality check for a receiving task.
     */
    CompletionStage<ReceivingTaskId> completeQualityCheck(CompleteQualityCheckCommand command);

    /**
     * Receives an item.
     */
    CompletionStage<ReceivingTaskId> receiveItem(ReceiveItemCommand command);

    /**
     * Completes a receiving task.
     */
    CompletionStage<ReceivingTaskId> completeReceivingTask(ReceivingTaskId taskId);

    /**
     * Cancels a receiving task.
     */
    CompletionStage<ReceivingTaskId> cancelReceivingTask(ReceivingTaskId taskId, String reason);

    /**
     * Gets a receiving task by ID.
     */
    CompletionStage<ReceivingTaskView> getReceivingTask(ReceivingTaskId taskId);

    /**
     * Gets receiving tasks for a warehouse.
     */
    CompletionStage<List<ReceivingTaskView>> getReceivingTasksByWarehouse(
        UUID warehouseId, String status, int page, int size
    );

    /**
     * Gets receiving tasks by purchase order.
     */
    CompletionStage<List<ReceivingTaskView>> getReceivingTasksByPurchaseOrder(
        String purchaseOrderNumber
    );

    /**
     * Gets receiving tasks by supplier.
     */
    CompletionStage<List<ReceivingTaskView>> getReceivingTasksBySupplier(
        String supplierName
    );
}
package tech.kayys.erp.warehouse.application.api;

import tech.kayys.erp.warehouse.application.api.command.AssignPutawayTaskCommand;
import tech.kayys.erp.warehouse.application.api.command.CompletePutawayItemCommand;
import tech.kayys.erp.warehouse.application.api.command.CreatePutawayTaskCommand;
import tech.kayys.erp.warehouse.application.api.query.PutawayTaskView;
import tech.kayys.erp.warehouse.domain.identifier.PutawayTaskId;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Public API for warehouse putaway operations.
 */
public interface WarehousePutawayService {

    /**
     * Creates a new putaway task.
     */
    CompletionStage<PutawayTaskId> createPutawayTask(CreatePutawayTaskCommand command);

    /**
     * Assigns a worker to a putaway task.
     */
    CompletionStage<PutawayTaskId> assignPutawayTask(AssignPutawayTaskCommand command);

    /**
     * Starts a putaway task.
     */
    CompletionStage<PutawayTaskId> startPutawayTask(PutawayTaskId taskId);

    /**
     * Completes a putaway item.
     */
    CompletionStage<PutawayTaskId> completePutawayItem(CompletePutawayItemCommand command);

    /**
     * Completes a putaway task.
     */
    CompletionStage<PutawayTaskId> completePutawayTask(PutawayTaskId taskId);

    /**
     * Cancels a putaway task.
     */
    CompletionStage<PutawayTaskId> cancelPutawayTask(PutawayTaskId taskId, String reason);

    /**
     * Gets a putaway task by ID.
     */
    CompletionStage<PutawayTaskView> getPutawayTask(PutawayTaskId taskId);

    /**
     * Gets putaway tasks for a warehouse.
     */
    CompletionStage<List<PutawayTaskView>> getPutawayTasksByWarehouse(
        UUID warehouseId, String status, int page, int size
    );

    /**
     * Gets putaway tasks assigned to a worker.
     */
    CompletionStage<List<PutawayTaskView>> getPutawayTasksByWorker(String workerId);

    /**
     * Gets putaway tasks by receiving reference.
     */
    CompletionStage<List<PutawayTaskView>> getPutawayTasksByReceivingReference(
        String receivingReference
    );

    /**
     * Suggests optimal bin for putaway.
     */
    CompletionStage<SuggestedBin> suggestBinForPutaway(
        String productId, int quantity, UUID warehouseId
    );
}
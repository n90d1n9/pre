package tech.kayys.erp.warehouse.application.api;

import tech.kayys.erp.warehouse.application.api.command.CreatePickListCommand;
import tech.kayys.erp.warehouse.application.api.command.PickItemCommand;
import tech.kayys.erp.warehouse.application.api.command.AssignPickerCommand;
import tech.kayys.erp.warehouse.application.api.query.PickListView;
import tech.kayys.erp.warehouse.domain.identifier.PickListId;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Public API for warehouse picking operations.
 */
public interface WarehousePickingService {

    /**
     * Creates a new pick list.
     */
    CompletionStage<PickListId> createPickList(CreatePickListCommand command);

    /**
     * Assigns a picker to a pick list.
     */
    CompletionStage<PickListId> assignPicker(AssignPickerCommand command);

    /**
     * Starts a pick list.
     */
    CompletionStage<PickListId> startPickList(PickListId pickListId);

    /**
     * Picks an item.
     */
    CompletionStage<PickListId> pickItem(PickItemCommand command);

    /**
     * Completes a pick list.
     */
    CompletionStage<PickListId> completePickList(PickListId pickListId);

    /**
     * Cancels a pick list.
     */
    CompletionStage<PickListId> cancelPickList(PickListId pickListId, String reason);

    /**
     * Gets a pick list by ID.
     */
    CompletionStage<PickListView> getPickList(PickListId pickListId);

    /**
     * Gets pick lists for a warehouse.
     */
    CompletionStage<List<PickListView>> getPickListsByWarehouse(
        UUID warehouseId, String status, int page, int size
    );

    /**
     * Gets pick lists assigned to a picker.
     */
    CompletionStage<List<PickListView>> getPickListsByPicker(String pickerId);

    /**
     * Gets active pick lists (in progress).
     */
    CompletionStage<List<PickListView>> getActivePickLists();

    /**
     * Gets pick lists by source reference.
     */
    CompletionStage<List<PickListView>> getPickListsBySource(String sourceReference, String sourceType);
}
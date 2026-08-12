package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;

import java.util.List;

/**
 * Command to receive items for a purchase order.
 */
public record ReceivePurchaseOrderItemsCommand(
        PurchaseOrderId purchaseOrderId,
        List<ReceivedItemCommand> receivedItems,
        String receivedBy
) implements Command<PurchaseOrderId> {

    public ReceivePurchaseOrderItemsCommand {
        if (purchaseOrderId == null) {
            throw new IllegalArgumentException("Purchase Order ID cannot be null");
        }
        if (receivedItems == null || receivedItems.isEmpty()) {
            throw new IllegalArgumentException("At least one item must be received");
        }
        if (receivedBy == null || receivedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Received by is required");
        }
    }

    /**
     * Received item command.
     */
    public record ReceivedItemCommand(
            int itemIndex,
            int quantityReceived,
            String notes
    ) {
        public ReceivedItemCommand {
            if (quantityReceived <= 0) {
                throw new IllegalArgumentException("Received quantity must be positive");
            }
            if (itemIndex < 0) {
                throw new IllegalArgumentException("Item index must be non-negative");
            }
        }
    }
}
package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;

/**
 * Command to submit a purchase order.
 */
public record SubmitPurchaseOrderCommand(
        PurchaseOrderId purchaseOrderId
) implements Command<PurchaseOrderId> {

    public SubmitPurchaseOrderCommand {
        if (purchaseOrderId == null) {
            throw new IllegalArgumentException("Purchase Order ID cannot be null");
        }
    }
}
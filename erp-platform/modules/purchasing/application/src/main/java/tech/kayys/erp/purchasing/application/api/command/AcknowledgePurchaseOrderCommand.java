package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;

/**
 * Command to acknowledge a purchase order from vendor.
 */
public record AcknowledgePurchaseOrderCommand(
        PurchaseOrderId purchaseOrderId,
        String vendorReference
) implements Command<PurchaseOrderId> {

    public AcknowledgePurchaseOrderCommand {
        if (purchaseOrderId == null) {
            throw new IllegalArgumentException("Purchase Order ID cannot be null");
        }
    }
}
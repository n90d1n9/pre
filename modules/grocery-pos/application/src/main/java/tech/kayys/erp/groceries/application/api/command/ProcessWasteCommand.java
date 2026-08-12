package tech.kayys.erp.groceries.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.groceries.domain.identifier.GroceryProductId;

import java.time.Instant;

public record ProcessWasteCommand(
        GroceryProductId productId,
        String batchNumber,
        int quantity,
        String reason,
        Instant wasteDate
) implements Command<Void> {
    public ProcessWasteCommand {
        if (productId == null) throw new IllegalArgumentException("Product ID cannot be null");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
    }
}

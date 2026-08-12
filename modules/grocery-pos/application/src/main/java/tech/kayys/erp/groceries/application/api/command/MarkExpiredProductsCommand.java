package tech.kayys.erp.groceries.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.groceries.domain.identifier.GroceryProductId;

import java.util.List;

public record MarkExpiredProductsCommand(List<GroceryProductId> productIds) implements Command<Void> {
    public MarkExpiredProductsCommand {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Product IDs cannot be empty");
        }
    }
}

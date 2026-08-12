package tech.kayys.erp.groceries.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.sales.domain.identifier.CartId;

import java.util.UUID;

public record CompleteGroceryTransactionCommand(
        CartId cartId,
        UUID customerId,
        String paymentMethod,
        String cashierId
) implements Command<GroceryReceipt> {
    public CompleteGroceryTransactionCommand {
        if (cartId == null) throw new IllegalArgumentException("Cart ID cannot be null");
    }
}

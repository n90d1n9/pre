package tech.kayys.erp.groceries.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.groceries.domain.identifier.GroceryProductId;
import tech.kayys.erp.groceries.domain.identifier.ScaleId;
import tech.kayys.erp.groceries.domain.valueobject.Weight;
import tech.kayys.erp.sales.domain.identifier.CartId;

public record AddWeightedItemCommand(
        CartId cartId,
        GroceryProductId groceryProductId,
        ScaleId scaleId,
        Weight weight,
        Double unitPricePerKg
) implements Command<CartItemResult> {
    public AddWeightedItemCommand {
        if (cartId == null) throw new IllegalArgumentException("Cart ID cannot be null");
        if (groceryProductId == null) throw new IllegalArgumentException("Grocery product ID cannot be null");
        if (scaleId == null) throw new IllegalArgumentException("Scale ID cannot be null");
    }
}

package tech.kayys.erp.groceries.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.groceries.domain.identifier.GroceryProductId;

import java.time.Instant;

/**
 * Command to update shelf life of a grocery product.
 */
public record UpdateShelfLifeCommand(
        GroceryProductId groceryProductId,
        Instant productionDate,
        Instant expiryDate,
        int shelfLifeDays
) implements Command<GroceryProductId> {

    public UpdateShelfLifeCommand {
        if (groceryProductId == null) {
            throw new IllegalArgumentException("Grocery product ID cannot be null");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private GroceryProductId groceryProductId;
        private Instant productionDate;
        private Instant expiryDate;
        private int shelfLifeDays;

        public Builder groceryProductId(GroceryProductId groceryProductId) {
            this.groceryProductId = groceryProductId;
            return this;
        }

        public Builder productionDate(Instant productionDate) {
            this.productionDate = productionDate;
            return this;
        }

        public Builder expiryDate(Instant expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder shelfLifeDays(int shelfLifeDays) {
            this.shelfLifeDays = shelfLifeDays;
            return this;
        }

        public UpdateShelfLifeCommand build() {
            return new UpdateShelfLifeCommand(
                groceryProductId, productionDate, expiryDate, shelfLifeDays
            );
        }
    }
}

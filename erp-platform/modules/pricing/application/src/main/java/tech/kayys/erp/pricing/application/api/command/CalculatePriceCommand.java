package tech.kayys.erp.pricing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.pricing.application.api.MoneyCommand;

import java.util.UUID;

/**
 * Command to calculate the price of a product with all applicable rules.
 */
public record CalculatePriceCommand(
        UUID productId,
        int quantity,
        MoneyCommand basePrice,
        String couponCode,
        String jurisdiction
) implements Command<PriceCalculationView> {

    public CalculatePriceCommand {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (basePrice == null) {
            throw new IllegalArgumentException("Base price is required");
        }
        if (jurisdiction == null || jurisdiction.trim().isEmpty()) {
            throw new IllegalArgumentException("Jurisdiction is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID productId;
        private int quantity = 1;
        private MoneyCommand basePrice;
        private String couponCode;
        private String jurisdiction;

        public Builder productId(UUID productId) {
            this.productId = productId;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder basePrice(MoneyCommand basePrice) {
            this.basePrice = basePrice;
            return this;
        }

        public Builder basePrice(String amount, String currencyCode) {
            this.basePrice = new MoneyCommand(amount, currencyCode);
            return this;
        }

        public Builder couponCode(String couponCode) {
            this.couponCode = couponCode;
            return this;
        }

        public Builder jurisdiction(String jurisdiction) {
            this.jurisdiction = jurisdiction;
            return this;
        }

        public CalculatePriceCommand build() {
            return new CalculatePriceCommand(productId, quantity, basePrice, couponCode, jurisdiction);
        }
    }
}
package tech.kayys.erp.promotion.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.promotion.domain.identifier.PromotionId;

import java.util.UUID;

/**
 * Command to redeem a promotion.
 */
public record RedeemPromotionCommand(
        PromotionId promotionId,
        UUID customerId,
        UUID orderId,
        String orderAmount,
        String currencyCode
) implements Command<PromotionId> {

    public RedeemPromotionCommand {
        if (promotionId == null) {
            throw new IllegalArgumentException("Promotion ID cannot be null");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (orderAmount == null || orderAmount.trim().isEmpty()) {
            throw new IllegalArgumentException("Order amount is required");
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PromotionId promotionId;
        private UUID customerId;
        private UUID orderId;
        private String orderAmount;
        private String currencyCode = "USD";

        public Builder promotionId(PromotionId promotionId) {
            this.promotionId = promotionId;
            return this;
        }

        public Builder customerId(UUID customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder orderId(UUID orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder orderAmount(String orderAmount) {
            this.orderAmount = orderAmount;
            return this;
        }

        public Builder currencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public RedeemPromotionCommand build() {
            return new RedeemPromotionCommand(
                promotionId, customerId, orderId, orderAmount, currencyCode
            );
        }
    }
}
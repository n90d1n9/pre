package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Command to create a new purchase order.
 */
public record CreatePurchaseOrderCommand(
        PurchaseOrderId purchaseOrderId,
        UUID vendorId,
        String vendorName,
        Instant requiredDate,
        List<PurchaseOrderItemCommand> items,
        String shippingAddress,
        String billingAddress,
        String paymentTerms,
        String shippingTerms,
        String currencyCode,
        String notes,
        String createdBy
) implements Command<PurchaseOrderId> {

    public CreatePurchaseOrderCommand {
        if (vendorId == null) {
            throw new IllegalArgumentException("Vendor ID cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("PO must have at least one item");
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PurchaseOrderId purchaseOrderId;
        private UUID vendorId;
        private String vendorName;
        private Instant requiredDate;
        private List<PurchaseOrderItemCommand> items;
        private String shippingAddress;
        private String billingAddress;
        private String paymentTerms;
        private String shippingTerms;
        private String currencyCode = "USD";
        private String notes;
        private String createdBy;

        public Builder purchaseOrderId(PurchaseOrderId purchaseOrderId) {
            this.purchaseOrderId = purchaseOrderId;
            return this;
        }

        public Builder vendorId(UUID vendorId) {
            this.vendorId = vendorId;
            return this;
        }

        public Builder vendorName(String vendorName) {
            this.vendorName = vendorName;
            return this;
        }

        public Builder requiredDate(Instant requiredDate) {
            this.requiredDate = requiredDate;
            return this;
        }

        public Builder items(List<PurchaseOrderItemCommand> items) {
            this.items = items;
            return this;
        }

        public Builder shippingAddress(String shippingAddress) {
            this.shippingAddress = shippingAddress;
            return this;
        }

        public Builder billingAddress(String billingAddress) {
            this.billingAddress = billingAddress;
            return this;
        }

        public Builder paymentTerms(String paymentTerms) {
            this.paymentTerms = paymentTerms;
            return this;
        }

        public Builder shippingTerms(String shippingTerms) {
            this.shippingTerms = shippingTerms;
            return this;
        }

        public Builder currencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public CreatePurchaseOrderCommand build() {
            if (purchaseOrderId == null) {
                purchaseOrderId = PurchaseOrderId.generate();
            }
            if (requiredDate == null) {
                requiredDate = Instant.now().plusSeconds(14L * 24L * 60L * 60L); // 14 days
            }
            return new CreatePurchaseOrderCommand(
                purchaseOrderId, vendorId, vendorName, requiredDate,
                items, shippingAddress, billingAddress, paymentTerms,
                shippingTerms, currencyCode, notes, createdBy
            );
        }
    }

    /**
     * Purchase Order Item Command.
     */
    public record PurchaseOrderItemCommand(
            UUID productId,
            String productName,
            String sku,
            int quantity,
            String unitPrice,
            String uom
    ) {
        public PurchaseOrderItemCommand {
            if (productName == null || productName.trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be empty");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (unitPrice == null || unitPrice.trim().isEmpty()) {
                throw new IllegalArgumentException("Unit price is required");
            }
        }
    }
}
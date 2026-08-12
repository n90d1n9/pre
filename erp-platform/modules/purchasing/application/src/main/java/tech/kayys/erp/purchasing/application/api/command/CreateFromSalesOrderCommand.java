package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;

import java.util.UUID;

/**
 * Command to create a purchase order from a sales order.
 * This handles the procurement workflow for items that need to be purchased.
 */
public record CreateFromSalesOrderCommand(
        UUID salesOrderId,
        UUID vendorId,
        String vendorName,
        String notes,
        String createdBy
) implements Command<PurchaseOrderId> {

    public CreateFromSalesOrderCommand {
        if (salesOrderId == null) {
            throw new IllegalArgumentException("Sales Order ID cannot be null");
        }
        if (vendorId == null) {
            throw new IllegalArgumentException("Vendor ID cannot be null");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID salesOrderId;
        private UUID vendorId;
        private String vendorName;
        private String notes;
        private String createdBy;

        public Builder salesOrderId(UUID salesOrderId) {
            this.salesOrderId = salesOrderId;
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

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public CreateFromSalesOrderCommand build() {
            return new CreateFromSalesOrderCommand(
                salesOrderId, vendorId, vendorName, notes, createdBy
            );
        }
    }
}
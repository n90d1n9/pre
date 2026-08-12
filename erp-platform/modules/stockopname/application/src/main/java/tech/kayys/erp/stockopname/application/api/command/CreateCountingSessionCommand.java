package tech.kayys.erp.stockopname.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.stockopname.domain.identifier.CountingSessionId;
import tech.kayys.erp.stockopname.domain.valueobject.CountingMethod;
import tech.kayys.erp.stockopname.domain.valueobject.CountingType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Command to create a new counting session.
 */
public record CreateCountingSessionCommand(
        CountingSessionId sessionId,
        UUID warehouseId,
        String warehouseName,
        CountingType countingType,
        CountingMethod countingMethod,
        Instant scheduledDate,
        String zone,
        List<String> categories,
        List<CountingItemCommand> items,
        String notes,
        String createdBy
) implements Command<CountingSessionId> {

    public CreateCountingSessionCommand {
        if (warehouseId == null) {
            throw new IllegalArgumentException("Warehouse ID cannot be null");
        }
        if (countingType == null) {
            throw new IllegalArgumentException("Counting type is required");
        }
        if (countingMethod == null) {
            throw new IllegalArgumentException("Counting method is required");
        }
        if (scheduledDate == null) {
            throw new IllegalArgumentException("Scheduled date is required");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("At least one item must be specified");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CountingSessionId sessionId;
        private UUID warehouseId;
        private String warehouseName;
        private CountingType countingType;
        private CountingMethod countingMethod;
        private Instant scheduledDate;
        private String zone;
        private List<String> categories;
        private List<CountingItemCommand> items;
        private String notes;
        private String createdBy;

        public Builder sessionId(CountingSessionId sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder warehouseId(UUID warehouseId) {
            this.warehouseId = warehouseId;
            return this;
        }

        public Builder warehouseName(String warehouseName) {
            this.warehouseName = warehouseName;
            return this;
        }

        public Builder countingType(CountingType countingType) {
            this.countingType = countingType;
            return this;
        }

        public Builder countingMethod(CountingMethod countingMethod) {
            this.countingMethod = countingMethod;
            return this;
        }

        public Builder scheduledDate(Instant scheduledDate) {
            this.scheduledDate = scheduledDate;
            return this;
        }

        public Builder zone(String zone) {
            this.zone = zone;
            return this;
        }

        public Builder categories(List<String> categories) {
            this.categories = categories;
            return this;
        }

        public Builder items(List<CountingItemCommand> items) {
            this.items = items;
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

        public CreateCountingSessionCommand build() {
            if (sessionId == null) {
                sessionId = CountingSessionId.generate();
            }
            if (scheduledDate == null) {
                scheduledDate = Instant.now().plusSeconds(86400); // Tomorrow
            }
            return new CreateCountingSessionCommand(
                sessionId, warehouseId, warehouseName, countingType,
                countingMethod, scheduledDate, zone, categories,
                items, notes, createdBy
            );
        }
    }

    /**
     * Counting item command.
     */
    public record CountingItemCommand(
            String id,
            String productId,
            String sku,
            String productName,
            String binLocation,
            int systemQuantity
    ) {
        public CountingItemCommand {
            if (productId == null || productId.trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be empty");
            }
            if (systemQuantity < 0) {
                throw new IllegalArgumentException("System quantity cannot be negative");
            }
        }
    }
}
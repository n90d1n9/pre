package tech.kayys.erp.warehouse.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.warehouse.domain.identifier.BinLocationId;
import tech.kayys.erp.warehouse.domain.valueobject.BinType;

import java.util.UUID;

/**
 * Command to create a new bin location.
 */
public record CreateBinLocationCommand(
        BinLocationId binLocationId,
        UUID warehouseId,
        String code,
        String name,
        String description,
        BinType binType,
        String zone,
        String aisle,
        String level,
        String position,
        int capacity,
        Integer maxWeight,
        Integer maxLength,
        Integer maxWidth,
        Integer maxHeight,
        String notes
) implements Command<BinLocationId> {

    public CreateBinLocationCommand {
        if (warehouseId == null) {
            throw new IllegalArgumentException("Warehouse ID cannot be null");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Bin code cannot be empty");
        }
        if (binType == null) {
            throw new IllegalArgumentException("Bin type cannot be null");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BinLocationId binLocationId;
        private UUID warehouseId;
        private String code;
        private String name;
        private String description;
        private BinType binType;
        private String zone;
        private String aisle;
        private String level;
        private String position;
        private int capacity = 1;
        private Integer maxWeight;
        private Integer maxLength;
        private Integer maxWidth;
        private Integer maxHeight;
        private String notes;

        public Builder binLocationId(BinLocationId binLocationId) {
            this.binLocationId = binLocationId;
            return this;
        }

        public Builder warehouseId(UUID warehouseId) {
            this.warehouseId = warehouseId;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder binType(BinType binType) {
            this.binType = binType;
            return this;
        }

        public Builder zone(String zone) {
            this.zone = zone;
            return this;
        }

        public Builder aisle(String aisle) {
            this.aisle = aisle;
            return this;
        }

        public Builder level(String level) {
            this.level = level;
            return this;
        }

        public Builder position(String position) {
            this.position = position;
            return this;
        }

        public Builder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder maxWeight(Integer maxWeight) {
            this.maxWeight = maxWeight;
            return this;
        }

        public Builder maxLength(Integer maxLength) {
            this.maxLength = maxLength;
            return this;
        }

        public Builder maxWidth(Integer maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        public Builder maxHeight(Integer maxHeight) {
            this.maxHeight = maxHeight;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public CreateBinLocationCommand build() {
            if (binLocationId == null) {
                binLocationId = BinLocationId.generate();
            }
            return new CreateBinLocationCommand(
                binLocationId, warehouseId, code, name, description,
                binType, zone, aisle, level, position, capacity,
                maxWeight, maxLength, maxWidth, maxHeight, notes
            );
        }
    }
}
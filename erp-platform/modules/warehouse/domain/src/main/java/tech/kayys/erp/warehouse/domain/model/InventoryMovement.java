package tech.kayys.erp.warehouse.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.warehouse.domain.identifier.InventoryMovementId;
import tech.kayys.erp.warehouse.domain.identifier.WarehouseId;
import tech.kayys.erp.warehouse.domain.valueobject.MovementStatus;
import tech.kayys.erp.warehouse.domain.valueobject.MovementType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Inventory movement aggregate root.
 * Represents movement of inventory between warehouses or locations.
 */
public final class InventoryMovement extends AggregateRoot<InventoryMovementId> {
    
    private static final long serialVersionUID = 1L;
    
    private String movementNumber;
    private WarehouseId sourceWarehouseId;
    private WarehouseId destinationWarehouseId;
    private MovementType movementType;
    private MovementStatus status;
    private List<MovementItem> items;
    private String referenceNumber;
    private String reason;
    private String notes;
    private String createdBy;
    private Instant createdAt;
    private String completedBy;
    private Instant completedAt;
    private boolean active;

    private InventoryMovement(InventoryMovementId id) {
        super(id);
        this.items = new ArrayList<>();
        this.status = MovementStatus.CREATED;
        this.active = true;
        this.createdAt = Instant.now();
    }

    private InventoryMovement() {
        super();
    }

    /**
     * Factory method to create a new inventory movement.
     */
    public static InventoryMovement create(
            InventoryMovementId id,
            String movementNumber,
            WarehouseId sourceWarehouseId,
            WarehouseId destinationWarehouseId,
            MovementType movementType,
            String createdBy,
            String reason) {
        InventoryMovement movement = new InventoryMovement(id);
        movement.movementNumber = movementNumber;
        movement.sourceWarehouseId = sourceWarehouseId;
        movement.destinationWarehouseId = destinationWarehouseId;
        movement.movementType = movementType;
        movement.createdBy = createdBy;
        movement.reason = reason;
        return movement;
    }

    /**
     * Adds an item to the movement.
     */
    public void addItem(MovementItem item) {
        if (status != MovementStatus.CREATED) {
            throw new IllegalStateException("Cannot add items in status: " + status);
        }
        items.add(item);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes an item from the movement.
     */
    public void removeItem(String itemId) {
        if (status != MovementStatus.CREATED) {
            throw new IllegalStateException("Cannot remove items in status: " + status);
        }
        items.removeIf(i -> i.getId().equals(itemId));
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Starts the movement (in transit).
     */
    public void startMovement() {
        if (status != MovementStatus.CREATED) {
            throw new IllegalStateException("Cannot start movement in status: " + status);
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Movement has no items");
        }
        this.status = MovementStatus.IN_TRANSIT;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Completes the movement.
     */
    public void complete(String completedBy) {
        if (status != MovementStatus.IN_TRANSIT) {
            throw new IllegalStateException("Cannot complete movement in status: " + status);
        }
        this.status = MovementStatus.COMPLETED;
        this.completedBy = completedBy;
        this.completedAt = Instant.now();
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Puts the movement on hold.
     */
    public void putOnHold(String reason) {
        if (status == MovementStatus.COMPLETED || status == MovementStatus.CANCELLED || status == MovementStatus.FAILED) {
            throw new IllegalStateException("Cannot hold movement in status: " + status);
        }
        this.status = MovementStatus.ON_HOLD;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Releases the movement from hold.
     */
    public void release() {
        if (status != MovementStatus.ON_HOLD) {
            throw new IllegalStateException("Cannot release movement in status: " + status);
        }
        this.status = MovementStatus.IN_TRANSIT;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Fails the movement.
     */
    public void fail(String reason) {
        if (status == MovementStatus.COMPLETED) {
            throw new IllegalStateException("Cannot fail completed movement");
        }
        this.status = MovementStatus.FAILED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the movement.
     */
    public void cancel(String reason) {
        if (status == MovementStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed movement");
        }
        this.status = MovementStatus.CANCELLED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the total quantity of items in the movement.
     */
    public int getTotalQuantity() {
        return items.stream()
            .mapToInt(MovementItem::getQuantity)
            .sum();
    }

    // Getters
    public String getMovementNumber() { return movementNumber; }
    public WarehouseId getSourceWarehouseId() { return sourceWarehouseId; }
    public WarehouseId getDestinationWarehouseId() { return destinationWarehouseId; }
    public MovementType getMovementType() { return movementType; }
    public MovementStatus getStatus() { return status; }
    public List<MovementItem> getItems() { return Collections.unmodifiableList(items); }
    public String getReferenceNumber() { return referenceNumber; }
    public String getReason() { return reason; }
    public String getNotes() { return notes; }
    public String getCreatedBy() { return createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public String getCompletedBy() { return completedBy; }
    public Instant getCompletedAt() { return completedAt; }
    public boolean isActive() { return active; }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "InventoryMovement{" +
                "id=" + getId() +
                ", movementNumber='" + movementNumber + '\'' +
                ", source=" + sourceWarehouseId +
                ", destination=" + destinationWarehouseId +
                ", type=" + movementType +
                ", status=" + status +
                ", items=" + items.size() +
                '}';
    }

    /**
     * Movement item value object.
     */
    public static final class MovementItem implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String productId;
        private final String productName;
        private final String sku;
        private final int quantity;
        private String binLocationId;
        private String batchNumber;
        private String serialNumber;
        private String notes;

        public MovementItem(
                String id,
                String productId,
                String productName,
                String sku,
                int quantity) {
            this.id = id;
            this.productId = productId;
            this.productName = productName;
            this.sku = sku;
            this.quantity = quantity;
            validate();
        }

        @Override
        public void validate() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Item ID cannot be empty");
            }
            if (productId == null || productId.trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be empty");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
        }

        public String getId() { return id; }
        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getSku() { return sku; }
        public int getQuantity() { return quantity; }
        public String getBinLocationId() { return binLocationId; }
        public String getBatchNumber() { return batchNumber; }
        public String getSerialNumber() { return serialNumber; }
        public String getNotes() { return notes; }

        public void setBinLocationId(String binLocationId) {
            this.binLocationId = binLocationId;
        }

        public void setBatchNumber(String batchNumber) {
            this.batchNumber = batchNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        @Override
        public String toString() {
            return "MovementItem{" +
                    "id='" + id + '\'' +
                    ", sku='" + sku + '\'' +
                    ", quantity=" + quantity +
                    '}';
        }
    }
}
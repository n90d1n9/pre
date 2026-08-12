package tech.kayys.erp.warehouse.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.warehouse.domain.identifier.PutawayTaskId;
import tech.kayys.erp.warehouse.domain.identifier.WarehouseId;
import tech.kayys.erp.warehouse.domain.valueobject.PutawayStatus;
import tech.kayys.erp.warehouse.domain.valueobject.PutawayStrategy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Putaway task aggregate root.
 * Represents the task of putting away received inventory.
 */
public final class PutawayTask extends AggregateRoot<PutawayTaskId> {
    
    private static final long serialVersionUID = 1L;
    
    private String taskNumber;
    private WarehouseId warehouseId;
    private String receivingReference;
    private String receivingType; // PURCHASE_ORDER, TRANSFER, RETURN
    private PutawayStatus status;
    private PutawayStrategy strategy;
    private List<PutawayItem> items;
    private String assignedTo;
    private Instant assignedAt;
    private Instant startedAt;
    private Instant completedAt;
    private String zone;
    private String notes;
    private boolean active;

    private PutawayTask(PutawayTaskId id) {
        super(id);
        this.items = new ArrayList<>();
        this.status = PutawayStatus.CREATED;
        this.active = true;
        this.strategy = PutawayStrategy.NEAREST;
    }

    private PutawayTask() {
        super();
    }

    /**
     * Factory method to create a new putaway task.
     */
    public static PutawayTask create(
            PutawayTaskId id,
            String taskNumber,
            WarehouseId warehouseId,
            String receivingReference,
            String receivingType,
            PutawayStrategy strategy) {
        PutawayTask task = new PutawayTask(id);
        task.taskNumber = taskNumber;
        task.warehouseId = warehouseId;
        task.receivingReference = receivingReference;
        task.receivingType = receivingType;
        task.strategy = strategy;
        return task;
    }

    /**
     * Adds an item to the putaway task.
     */
    public void addItem(PutawayItem item) {
        if (status != PutawayStatus.CREATED) {
            throw new IllegalStateException("Cannot add items in status: " + status);
        }
        items.add(item);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes an item from the putaway task.
     */
    public void removeItem(String itemId) {
        if (status != PutawayStatus.CREATED) {
            throw new IllegalStateException("Cannot remove items in status: " + status);
        }
        items.removeIf(i -> i.getId().equals(itemId));
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Assigns the putaway task to a worker.
     */
    public void assign(String assignedTo) {
        if (status != PutawayStatus.CREATED) {
            throw new IllegalStateException("Cannot assign putaway in status: " + status);
        }
        this.assignedTo = assignedTo;
        this.assignedAt = Instant.now();
        this.status = PutawayStatus.ASSIGNED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Starts the putaway process.
     */
    public void start() {
        if (status != PutawayStatus.ASSIGNED && status != PutawayStatus.CREATED) {
            throw new IllegalStateException("Cannot start putaway in status: " + status);
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Putaway task has no items");
        }
        this.status = PutawayStatus.IN_PROGRESS;
        this.startedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Completes putaway for an item.
     */
    public void completeItem(String itemId, String binLocationId, String completedBy) {
        if (status != PutawayStatus.IN_PROGRESS && status != PutawayStatus.PARTIALLY_COMPLETED) {
            throw new IllegalStateException("Cannot complete item in status: " + status);
        }
        
        PutawayItem item = items.stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        if (item.isCompleted()) {
            throw new IllegalStateException("Item already completed: " + itemId);
        }

        item.complete(binLocationId, completedBy);
        
        // Update status based on progress
        boolean allCompleted = items.stream().allMatch(PutawayItem::isCompleted);
        boolean anyCompleted = items.stream().anyMatch(PutawayItem::isCompleted);
        
        if (allCompleted) {
            this.status = PutawayStatus.COMPLETED;
            this.completedAt = Instant.now();
        } else if (anyCompleted) {
            this.status = PutawayStatus.PARTIALLY_COMPLETED;
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Puts the putaway task on hold.
     */
    public void putOnHold(String reason) {
        if (status == PutawayStatus.COMPLETED || status == PutawayStatus.CANCELLED || status == PutawayStatus.FAILED) {
            throw new IllegalStateException("Cannot hold putaway in status: " + status);
        }
        this.status = PutawayStatus.ON_HOLD;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Releases the putaway task from hold.
     */
    public void release() {
        if (status != PutawayStatus.ON_HOLD) {
            throw new IllegalStateException("Cannot release putaway in status: " + status);
        }
        this.status = PutawayStatus.IN_PROGRESS;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the putaway task as failed.
     */
    public void fail(String reason) {
        if (status == PutawayStatus.COMPLETED) {
            throw new IllegalStateException("Cannot fail completed putaway");
        }
        this.status = PutawayStatus.FAILED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the putaway task.
     */
    public void cancel(String reason) {
        if (status == PutawayStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed putaway");
        }
        this.status = PutawayStatus.CANCELLED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the completion percentage.
     */
    public double getProgress() {
        if (items.isEmpty()) {
            return 0.0;
        }
        long completed = items.stream().filter(PutawayItem::isCompleted).count();
        return (double) completed / items.size() * 100.0;
    }

    /**
     * Gets the total quantity of items in the task.
     */
    public int getTotalQuantity() {
        return items.stream()
            .mapToInt(PutawayItem::getQuantity)
            .sum();
    }

    /**
     * Gets items that are not yet completed.
     */
    public List<PutawayItem> getRemainingItems() {
        return items.stream()
            .filter(i -> !i.isCompleted())
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Gets items that are completed.
     */
    public List<PutawayItem> getCompletedItems() {
        return items.stream()
            .filter(PutawayItem::isCompleted)
            .collect(java.util.stream.Collectors.toList());
    }

    // Getters
    public String getTaskNumber() { return taskNumber; }
    public WarehouseId getWarehouseId() { return warehouseId; }
    public String getReceivingReference() { return receivingReference; }
    public String getReceivingType() { return receivingType; }
    public PutawayStatus getStatus() { return status; }
    public PutawayStrategy getStrategy() { return strategy; }
    public List<PutawayItem> getItems() { return Collections.unmodifiableList(items); }
    public String getAssignedTo() { return assignedTo; }
    public Instant getAssignedAt() { return assignedAt; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getZone() { return zone; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setZone(String zone) {
        this.zone = zone;
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
        return "PutawayTask{" +
                "id=" + getId() +
                ", taskNumber='" + taskNumber + '\'' +
                ", status=" + status +
                ", items=" + items.size() +
                ", progress=" + getProgress() + "%" +
                '}';
    }

    /**
     * Putaway item value object.
     */
    public static final class PutawayItem implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String productId;
        private final String productName;
        private final String sku;
        private final int quantity;
        private String binLocationId;
        private boolean completed;
        private String completedBy;
        private Instant completedAt;
        private String notes;

        public PutawayItem(
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
            this.completed = false;
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
        public boolean isCompleted() { return completed; }
        public String getCompletedBy() { return completedBy; }
        public Instant getCompletedAt() { return completedAt; }
        public String getNotes() { return notes; }

        public void complete(String binLocationId, String completedBy) {
            this.binLocationId = binLocationId;
            this.completed = true;
            this.completedBy = completedBy;
            this.completedAt = Instant.now();
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        @Override
        public String toString() {
            return "PutawayItem{" +
                    "id='" + id + '\'' +
                    ", sku='" + sku + '\'' +
                    ", quantity=" + quantity +
                    ", completed=" + completed +
                    ", binLocationId='" + binLocationId + '\'' +
                    '}';
        }
    }
}
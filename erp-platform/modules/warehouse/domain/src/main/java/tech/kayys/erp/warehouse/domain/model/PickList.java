package tech.kayys.erp.warehouse.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.warehouse.domain.identifier.PickListId;
import tech.kayys.erp.warehouse.domain.identifier.WarehouseId;
import tech.kayys.erp.warehouse.domain.valueobject.PickStatus;
import tech.kayys.erp.warehouse.domain.valueobject.PickStrategy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pick list aggregate root.
 * Represents a list of items to be picked from warehouse.
 */
public final class PickList extends AggregateRoot<PickListId> {
    
    private static final long serialVersionUID = 1L;
    
    private String pickListNumber;
    private WarehouseId warehouseId;
    private String sourceReference;
    private String sourceType; // ORDER, TRANSFER, etc.
    private PickStatus status;
    private PickStrategy strategy;
    private List<PickItem> items;
    private String assignedTo;
    private Instant assignedAt;
    private Instant startedAt;
    private Instant completedAt;
    private String priority;
    private String waveNumber;
    private String zone;
    private String notes;
    private boolean active;

    private PickList(PickListId id) {
        super(id);
        this.items = new ArrayList<>();
        this.status = PickStatus.CREATED;
        this.active = true;
        this.strategy = PickStrategy.FIFO;
    }

    private PickList() {
        super();
    }

    /**
     * Factory method to create a new pick list.
     */
    public static PickList create(
            PickListId id,
            String pickListNumber,
            WarehouseId warehouseId,
            String sourceReference,
            String sourceType,
            PickStrategy strategy) {
        PickList pickList = new PickList(id);
        pickList.pickListNumber = pickListNumber;
        pickList.warehouseId = warehouseId;
        pickList.sourceReference = sourceReference;
        pickList.sourceType = sourceType;
        pickList.strategy = strategy;
        return pickList;
    }

    /**
     * Adds an item to the pick list.
     */
    public void addItem(PickItem item) {
        if (status != PickStatus.CREATED) {
            throw new IllegalStateException("Cannot add items in status: " + status);
        }
        items.add(item);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes an item from the pick list.
     */
    public void removeItem(String itemId) {
        if (status != PickStatus.CREATED) {
            throw new IllegalStateException("Cannot remove items in status: " + status);
        }
        items.removeIf(i -> i.getId().equals(itemId));
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Assigns the pick list to a picker.
     */
    public void assign(String assignedTo) {
        if (status != PickStatus.CREATED) {
            throw new IllegalStateException("Cannot assign pick list in status: " + status);
        }
        this.assignedTo = assignedTo;
        this.assignedAt = Instant.now();
        this.status = PickStatus.ASSIGNED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Starts the picking process.
     */
    public void start() {
        if (status != PickStatus.ASSIGNED && status != PickStatus.CREATED) {
            throw new IllegalStateException("Cannot start picking in status: " + status);
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Pick list has no items");
        }
        this.status = PickStatus.IN_PROGRESS;
        this.startedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Picks an item.
     */
    public void pickItem(String itemId, int quantity, String pickedBy, String binLocation) {
        if (status != PickStatus.IN_PROGRESS && status != PickStatus.PARTIALLY_PICKED) {
            throw new IllegalStateException("Cannot pick in status: " + status);
        }
        
        PickItem item = items.stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        if (item.isCompleted()) {
            throw new IllegalStateException("Item already fully picked: " + itemId);
        }

        item.pick(quantity, pickedBy, binLocation);
        
        // Update status based on progress
        boolean allPicked = items.stream().allMatch(PickItem::isCompleted);
        boolean anyPicked = items.stream().anyMatch(i -> i.getPickedQuantity() > 0);
        
        if (allPicked) {
            this.status = PickStatus.COMPLETED;
            this.completedAt = Instant.now();
        } else if (anyPicked) {
            this.status = PickStatus.PARTIALLY_PICKED;
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Puts the pick list on hold.
     */
    public void putOnHold(String reason) {
        if (status == PickStatus.COMPLETED || status == PickStatus.CANCELLED) {
            throw new IllegalStateException("Cannot hold pick list in status: " + status);
        }
        this.status = PickStatus.ON_HOLD;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Releases the pick list from hold.
     */
    public void release() {
        if (status != PickStatus.ON_HOLD) {
            throw new IllegalStateException("Cannot release pick list in status: " + status);
        }
        this.status = PickStatus.IN_PROGRESS;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the pick list.
     */
    public void cancel(String reason) {
        if (status == PickStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed pick list");
        }
        this.status = PickStatus.CANCELLED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the picking progress percentage.
     */
    public double getProgress() {
        if (items.isEmpty()) {
            return 0.0;
        }
        long completed = items.stream().filter(PickItem::isCompleted).count();
        return (double) completed / items.size() * 100.0;
    }

    /**
     * Gets the total picked quantity.
     */
    public int getTotalPickedQuantity() {
        return items.stream()
            .mapToInt(PickItem::getPickedQuantity)
            .sum();
    }

    /**
     * Gets the total requested quantity.
     */
    public int getTotalRequestedQuantity() {
        return items.stream()
            .mapToInt(PickItem::getRequestedQuantity)
            .sum();
    }

    /**
     * Gets items that are not yet fully picked.
     */
    public List<PickItem> getRemainingItems() {
        return items.stream()
            .filter(i -> !i.isCompleted())
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Gets items that are fully picked.
     */
    public List<PickItem> getCompletedItems() {
        return items.stream()
            .filter(PickItem::isCompleted)
            .collect(java.util.stream.Collectors.toList());
    }

    // Getters
    public String getPickListNumber() { return pickListNumber; }
    public WarehouseId getWarehouseId() { return warehouseId; }
    public String getSourceReference() { return sourceReference; }
    public String getSourceType() { return sourceType; }
    public PickStatus getStatus() { return status; }
    public PickStrategy getStrategy() { return strategy; }
    public List<PickItem> getItems() { return Collections.unmodifiableList(items); }
    public String getAssignedTo() { return assignedTo; }
    public Instant getAssignedAt() { return assignedAt; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getPriority() { return priority; }
    public String getWaveNumber() { return waveNumber; }
    public String getZone() { return zone; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setPriority(String priority) {
        this.priority = priority;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setWaveNumber(String waveNumber) {
        this.waveNumber = waveNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

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
        return "PickList{" +
                "id=" + getId() +
                ", pickListNumber='" + pickListNumber + '\'' +
                ", status=" + status +
                ", items=" + items.size() +
                ", progress=" + getProgress() + "%" +
                '}';
    }

    /**
     * Pick item value object.
     */
    public static final class PickItem implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String productId;
        private final String productName;
        private final String sku;
        private final int requestedQuantity;
        private int pickedQuantity;
        private String binLocation;
        private String pickSequence;
        private boolean completed;
        private String pickedBy;
        private Instant pickedAt;
        private String notes;

        public PickItem(
                String id,
                String productId,
                String productName,
                String sku,
                int requestedQuantity,
                String binLocation) {
            this.id = id;
            this.productId = productId;
            this.productName = productName;
            this.sku = sku;
            this.requestedQuantity = requestedQuantity;
            this.binLocation = binLocation;
            this.pickedQuantity = 0;
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
            if (requestedQuantity <= 0) {
                throw new IllegalArgumentException("Requested quantity must be positive");
            }
        }

        public String getId() { return id; }
        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getSku() { return sku; }
        public int getRequestedQuantity() { return requestedQuantity; }
        public int getPickedQuantity() { return pickedQuantity; }
        public int getRemainingQuantity() { return requestedQuantity - pickedQuantity; }
        public String getBinLocation() { return binLocation; }
        public String getPickSequence() { return pickSequence; }
        public boolean isCompleted() { return completed; }
        public String getPickedBy() { return pickedBy; }
        public Instant getPickedAt() { return pickedAt; }
        public String getNotes() { return notes; }

        public void pick(int quantity, String pickedBy, String binLocation) {
            if (completed) {
                throw new IllegalStateException("Item already fully picked");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (quantity > getRemainingQuantity()) {
                throw new IllegalArgumentException("Quantity exceeds remaining: " + getRemainingQuantity());
            }
            this.pickedQuantity += quantity;
            this.pickedBy = pickedBy;
            this.pickedAt = Instant.now();
            if (binLocation != null) {
                this.binLocation = binLocation;
            }
            if (this.pickedQuantity >= this.requestedQuantity) {
                this.completed = true;
            }
        }

        public void setBinLocation(String binLocation) {
            this.binLocation = binLocation;
        }

        public void setPickSequence(String pickSequence) {
            this.pickSequence = pickSequence;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        @Override
        public String toString() {
            return "PickItem{" +
                    "id='" + id + '\'' +
                    ", sku='" + sku + '\'' +
                    ", requested=" + requestedQuantity +
                    ", picked=" + pickedQuantity +
                    ", completed=" + completed +
                    '}';
        }
    }
}
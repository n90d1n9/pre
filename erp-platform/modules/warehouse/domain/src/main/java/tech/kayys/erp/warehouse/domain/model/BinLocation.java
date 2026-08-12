package tech.kayys.erp.warehouse.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.warehouse.domain.identifier.BinLocationId;
import tech.kayys.erp.warehouse.domain.identifier.WarehouseId;
import tech.kayys.erp.warehouse.domain.valueobject.BinStatus;
import tech.kayys.erp.warehouse.domain.valueobject.BinType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bin location aggregate root.
 * Represents a specific storage location within a warehouse.
 */
public final class BinLocation extends AggregateRoot<BinLocationId> {
    
    private static final long serialVersionUID = 1L;
    
    private WarehouseId warehouseId;
    private String code;
    private String name;
    private String description;
    private BinType binType;
    private BinStatus status;
    private String zone;
    private String aisle;
    private String level;
    private String position;
    private int capacity;
    private int occupied;
    private int maxWeight;
    private int maxLength;
    private int maxWidth;
    private int maxHeight;
    private boolean active;
    private List<String> assignedProductIds;
    private String notes;

    private BinLocation(BinLocationId id) {
        super(id);
        this.status = BinStatus.ACTIVE;
        this.active = true;
        this.occupied = 0;
        this.capacity = 1;
        this.assignedProductIds = new ArrayList<>();
    }

    private BinLocation() {
        super();
    }

    /**
     * Factory method to create a new bin location.
     */
    public static BinLocation create(
            BinLocationId id,
            WarehouseId warehouseId,
            String code,
            String zone,
            BinType binType,
            int capacity) {
        BinLocation bin = new BinLocation(id);
        bin.warehouseId = warehouseId;
        bin.code = code;
        bin.zone = zone;
        bin.binType = binType;
        bin.capacity = capacity;
        bin.status = BinStatus.ACTIVE;
        return bin;
    }

    /**
     * Updates the bin location details.
     */
    public void update(String name, String description, String aisle, String level, String position) {
        this.name = name;
        this.description = description;
        this.aisle = aisle;
        this.level = level;
        this.position = position;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the capacity.
     */
    public void setCapacity(int capacity) {
        if (capacity < occupied) {
            throw new IllegalArgumentException("Capacity cannot be less than occupied count");
        }
        this.capacity = capacity;
        if (capacity == occupied) {
            this.status = BinStatus.FULL;
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Occupies space in the bin.
     */
    public void occupy(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (occupied + quantity > capacity) {
            throw new IllegalArgumentException("Insufficient capacity: " + occupied + "/" + capacity);
        }
        this.occupied += quantity;
        if (occupied == capacity) {
            this.status = BinStatus.FULL;
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Frees space in the bin.
     */
    public void free(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > occupied) {
            throw new IllegalArgumentException("Cannot free more than occupied: " + occupied);
        }
        this.occupied -= quantity;
        if (this.occupied < capacity) {
            this.status = BinStatus.ACTIVE;
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Assigns a product to this bin.
     */
    public void assignProduct(String productId) {
        if (!assignedProductIds.contains(productId)) {
            assignedProductIds.add(productId);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a product assignment.
     */
    public void unassignProduct(String productId) {
        assignedProductIds.remove(productId);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the utilization percentage.
     */
    public double getUtilization() {
        if (capacity == 0) {
            return 0.0;
        }
        return (double) occupied / capacity * 100.0;
    }

    /**
     * Checks if the bin has available space.
     */
    public boolean hasSpace(int quantity) {
        return occupied + quantity <= capacity;
    }

    /**
     * Activates the bin location.
     */
    public void activate() {
        this.active = true;
        this.status = BinStatus.ACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the bin location.
     */
    public void deactivate() {
        if (occupied > 0) {
            throw new IllegalStateException("Cannot deactivate bin with inventory: " + occupied);
        }
        this.active = false;
        this.status = BinStatus.INACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Puts the bin in maintenance.
     */
    public void putInMaintenance() {
        if (occupied > 0) {
            throw new IllegalStateException("Cannot put bin in maintenance with inventory: " + occupied);
        }
        this.status = BinStatus.MAINTENANCE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Returns the bin to active.
     */
    public void returnToActive() {
        this.status = occupied == capacity ? BinStatus.FULL : BinStatus.ACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if the bin is available for storage.
     */
    public boolean isAvailableForStorage() {
        return active && status.canStore();
    }

    /**
     * Gets the available capacity.
     */
    public int getAvailableCapacity() {
        return capacity - occupied;
    }

    /**
     * Gets the bin location code with zone.
     */
    public String getFullCode() {
        return zone + "-" + code;
    }

    // Getters
    public WarehouseId getWarehouseId() { return warehouseId; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BinType getBinType() { return binType; }
    public BinStatus getStatus() { return status; }
    public String getZone() { return zone; }
    public String getAisle() { return aisle; }
    public String getLevel() { return level; }
    public String getPosition() { return position; }
    public int getCapacity() { return capacity; }
    public int getOccupied() { return occupied; }
    public int getAvailableCapacity() { return capacity - occupied; }
    public int getMaxWeight() { return maxWeight; }
    public int getMaxLength() { return maxLength; }
    public int getMaxWidth() { return maxWidth; }
    public int getMaxHeight() { return maxHeight; }
    public boolean isActive() { return active; }
    public List<String> getAssignedProductIds() { return Collections.unmodifiableList(assignedProductIds); }
    public String getNotes() { return notes; }

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMaxDimensions(int maxLength, int maxWidth, int maxHeight) {
        this.maxLength = maxLength;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
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
        return "BinLocation{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", zone='" + zone + '\'' +
                ", binType=" + binType +
                ", status=" + status +
                ", capacity=" + capacity +
                ", occupied=" + occupied +
                ", utilization=" + getUtilization() + "%" +
                '}';
    }
}
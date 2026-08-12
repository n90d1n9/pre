package tech.kayys.erp.warehouse.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.warehouse.domain.identifier.ShippingTaskId;
import tech.kayys.erp.warehouse.domain.identifier.WarehouseId;
import tech.kayys.erp.warehouse.domain.valueobject.Carrier;
import tech.kayys.erp.warehouse.domain.valueobject.ShippingMethod;
import tech.kayys.erp.warehouse.domain.valueobject.ShippingStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shipping task aggregate root.
 * Represents the task of shipping inventory from the warehouse.
 */
public final class ShippingTask extends AggregateRoot<ShippingTaskId> {
    
    private static final long serialVersionUID = 1L;
    
    private String taskNumber;
    private WarehouseId warehouseId;
    private String orderReference;
    private String orderType; // SALES_ORDER, TRANSFER, etc.
    private ShippingStatus status;
    private List<ShippingItem> items;
    private String assignedTo;
    private Instant assignedAt;
    private Instant startedAt;
    private Instant completedAt;
    private String customerName;
    private String shippingAddress;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;
    private String email;
    private String shippingNotes;
    private Carrier carrier;
    private String carrierAccount;
    private ShippingMethod shippingMethod;
    private String trackingNumber;
    private String shipmentNumber;
    private double weight;
    private double length;
    private double width;
    private double height;
    private String notes;
    private boolean active;

    private ShippingTask(ShippingTaskId id) {
        super(id);
        this.items = new ArrayList<>();
        this.status = ShippingStatus.CREATED;
        this.active = true;
    }

    private ShippingTask() {
        super();
    }

    /**
     * Factory method to create a new shipping task.
     */
    public static ShippingTask create(
            ShippingTaskId id,
            String taskNumber,
            WarehouseId warehouseId,
            String orderReference,
            String orderType,
            String customerName,
            String shippingAddress,
            String city,
            String state,
            String postalCode,
            String country) {
        ShippingTask task = new ShippingTask(id);
        task.taskNumber = taskNumber;
        task.warehouseId = warehouseId;
        task.orderReference = orderReference;
        task.orderType = orderType;
        task.customerName = customerName;
        task.shippingAddress = shippingAddress;
        task.city = city;
        task.state = state;
        task.postalCode = postalCode;
        task.country = country;
        return task;
    }

    /**
     * Adds an item to the shipping task.
     */
    public void addItem(ShippingItem item) {
        if (status != ShippingStatus.CREATED) {
            throw new IllegalStateException("Cannot add items in status: " + status);
        }
        items.add(item);
        updateWeightAndDimensions();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes an item from the shipping task.
     */
    public void removeItem(String itemId) {
        if (status != ShippingStatus.CREATED) {
            throw new IllegalStateException("Cannot remove items in status: " + status);
        }
        items.removeIf(i -> i.getId().equals(itemId));
        updateWeightAndDimensions();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Starts packing.
     */
    public void startPacking() {
        if (status != ShippingStatus.CREATED) {
            throw new IllegalStateException("Cannot start packing in status: " + status);
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Shipping task has no items");
        }
        this.status = ShippingStatus.PACKING;
        this.startedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks as ready to ship.
     */
    public void readyToShip() {
        if (status != ShippingStatus.PACKING) {
            throw new IllegalStateException("Cannot mark ready to ship in status: " + status);
        }
        this.status = ShippingStatus.READY_TO_SHIP;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Assigns a carrier.
     */
    public void assignCarrier(Carrier carrier, String carrierAccount, ShippingMethod shippingMethod) {
        if (status != ShippingStatus.READY_TO_SHIP) {
            throw new IllegalStateException("Cannot assign carrier in status: " + status);
        }
        this.carrier = carrier;
        this.carrierAccount = carrierAccount;
        this.shippingMethod = shippingMethod;
        this.status = ShippingStatus.ASSIGNED;
        this.assignedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Ships the items.
     */
    public void ship(String trackingNumber, String shipmentNumber) {
        if (status != ShippingStatus.ASSIGNED) {
            throw new IllegalStateException("Cannot ship in status: " + status);
        }
        this.trackingNumber = trackingNumber;
        this.shipmentNumber = shipmentNumber;
        this.status = ShippingStatus.IN_TRANSIT;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks as delivered.
     */
    public void markDelivered() {
        if (status != ShippingStatus.IN_TRANSIT && status != ShippingStatus.PARTIALLY_SHIPPED) {
            throw new IllegalStateException("Cannot mark delivered in status: " + status);
        }
        this.status = ShippingStatus.DELIVERED;
        this.completedAt = Instant.now();
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Ships an item partially.
     */
    public void shipItemPartially(String itemId, int quantity, String trackingNumber) {
        if (status != ShippingStatus.IN_TRANSIT && status != ShippingStatus.PARTIALLY_SHIPPED) {
            throw new IllegalStateException("Cannot partially ship in status: " + status);
        }
        
        ShippingItem item = items.stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        if (item.isFullyShipped()) {
            throw new IllegalStateException("Item already fully shipped: " + itemId);
        }

        item.shipPartial(quantity, trackingNumber);
        
        boolean allShipped = items.stream().allMatch(ShippingItem::isFullyShipped);
        boolean anyShipped = items.stream().anyMatch(i -> i.getShippedQuantity() > 0);
        
        if (allShipped) {
            this.status = ShippingStatus.IN_TRANSIT;
            this.trackingNumber = trackingNumber;
        } else if (anyShipped) {
            this.status = ShippingStatus.PARTIALLY_SHIPPED;
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Puts the shipping task on hold.
     */
    public void putOnHold(String reason) {
        if (status == ShippingStatus.DELIVERED || status == ShippingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot hold shipping in status: " + status);
        }
        this.status = ShippingStatus.ON_HOLD;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Releases the shipping task from hold.
     */
    public void release() {
        if (status != ShippingStatus.ON_HOLD) {
            throw new IllegalStateException("Cannot release shipping in status: " + status);
        }
        this.status = ShippingStatus.READY_TO_SHIP;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the shipping task.
     */
    public void cancel(String reason) {
        if (status == ShippingStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel delivered shipping");
        }
        this.status = ShippingStatus.CANCELLED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates weight and dimensions.
     */
    private void updateWeightAndDimensions() {
        this.weight = items.stream()
            .mapToDouble(i -> i.getWeight() * i.getQuantity())
            .sum();
        // For dimensions, we'd calculate based on the largest item
        // Simplified version
    }

    /**
     * Gets the completion percentage.
     */
    public double getProgress() {
        if (items.isEmpty()) {
            return 0.0;
        }
        long shipped = items.stream().filter(ShippingItem::isFullyShipped).count();
        return (double) shipped / items.size() * 100.0;
    }

    /**
     * Gets the total shipped quantity.
     */
    public int getTotalShippedQuantity() {
        return items.stream()
            .mapToInt(ShippingItem::getShippedQuantity)
            .sum();
    }

    /**
     * Gets the total requested quantity.
     */
    public int getTotalRequestedQuantity() {
        return items.stream()
            .mapToInt(ShippingItem::getQuantity)
            .sum();
    }

    // Getters
    public String getTaskNumber() { return taskNumber; }
    public WarehouseId getWarehouseId() { return warehouseId; }
    public String getOrderReference() { return orderReference; }
    public String getOrderType() { return orderType; }
    public ShippingStatus getStatus() { return status; }
    public List<ShippingItem> getItems() { return Collections.unmodifiableList(items); }
    public String getAssignedTo() { return assignedTo; }
    public Instant getAssignedAt() { return assignedAt; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getCustomerName() { return customerName; }
    public String getShippingAddress() { return shippingAddress; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getShippingNotes() { return shippingNotes; }
    public Carrier getCarrier() { return carrier; }
    public String getCarrierAccount() { return carrierAccount; }
    public ShippingMethod getShippingMethod() { return shippingMethod; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getShipmentNumber() { return shipmentNumber; }
    public double getWeight() { return weight; }
    public double getLength() { return length; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
        this.assignedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setShippingNotes(String shippingNotes) {
        this.shippingNotes = shippingNotes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPhone(String phone) {
        this.phone = phone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setEmail(String email) {
        this.email = email;
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
        return "ShippingTask{" +
                "id=" + getId() +
                ", taskNumber='" + taskNumber + '\'' +
                ", orderReference='" + orderReference + '\'' +
                ", customerName='" + customerName + '\'' +
                ", status=" + status +
                ", items=" + items.size() +
                ", progress=" + getProgress() + "%" +
                '}';
    }

    /**
     * Shipping item value object.
     */
    public static final class ShippingItem implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String productId;
        private final String productName;
        private final String sku;
        private final int quantity;
        private int shippedQuantity;
        private double weight;
        private double length;
        private double width;
        private double height;
        private String binLocationId;
        private String trackingNumber;
        private boolean shipped;
        private String shippedBy;
        private Instant shippedAt;

        public ShippingItem(
                String id,
                String productId,
                String productName,
                String sku,
                int quantity,
                double weight,
                double length,
                double width,
                double height) {
            this.id = id;
            this.productId = productId;
            this.productName = productName;
            this.sku = sku;
            this.quantity = quantity;
            this.weight = weight;
            this.length = length;
            this.width = width;
            this.height = height;
            this.shippedQuantity = 0;
            this.shipped = false;
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
        public int getShippedQuantity() { return shippedQuantity; }
        public int getRemainingQuantity() { return quantity - shippedQuantity; }
        public double getWeight() { return weight; }
        public double getLength() { return length; }
        public double getWidth() { return width; }
        public double getHeight() { return height; }
        public String getBinLocationId() { return binLocationId; }
        public String getTrackingNumber() { return trackingNumber; }
        public boolean isFullyShipped() { return shippedQuantity >= quantity; }
        public String getShippedBy() { return shippedBy; }
        public Instant getShippedAt() { return shippedAt; }

        public void ship(int quantity, String shippedBy, String trackingNumber) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (quantity > getRemainingQuantity()) {
                throw new IllegalArgumentException("Quantity exceeds remaining: " + getRemainingQuantity());
            }
            this.shippedQuantity += quantity;
            this.shippedBy = shippedBy;
            this.shippedAt = Instant.now();
            this.shipped = true;
            if (trackingNumber != null) {
                this.trackingNumber = trackingNumber;
            }
        }

        public void shipPartial(int quantity, String trackingNumber) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (quantity > getRemainingQuantity()) {
                throw new IllegalArgumentException("Quantity exceeds remaining: " + getRemainingQuantity());
            }
            this.shippedQuantity += quantity;
            if (trackingNumber != null) {
                this.trackingNumber = trackingNumber;
            }
            if (this.shippedQuantity >= this.quantity) {
                this.shipped = true;
                this.shippedAt = Instant.now();
            }
        }

        public void setBinLocationId(String binLocationId) {
            this.binLocationId = binLocationId;
        }

        @Override
        public String toString() {
            return "ShippingItem{" +
                    "id='" + id + '\'' +
                    ", sku='" + sku + '\'' +
                    ", quantity=" + quantity +
                    ", shipped=" + shippedQuantity +
                    '}';
        }
    }
}
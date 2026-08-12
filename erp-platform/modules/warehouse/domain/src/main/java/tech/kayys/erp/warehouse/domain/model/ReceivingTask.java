package tech.kayys.erp.warehouse.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.warehouse.domain.identifier.ReceivingTaskId;
import tech.kayys.erp.warehouse.domain.identifier.WarehouseId;
import tech.kayys.erp.warehouse.domain.valueobject.QualityCheckResult;
import tech.kayys.erp.warehouse.domain.valueobject.ReceivingStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Receiving task aggregate root.
 * Represents the task of receiving inventory into the warehouse.
 */
public final class ReceivingTask extends AggregateRoot<ReceivingTaskId> {
    
    private static final long serialVersionUID = 1L;
    
    private String taskNumber;
    private WarehouseId warehouseId;
    private String purchaseOrderNumber;
    private String shipmentNumber;
    private String supplierName;
    private ReceivingStatus status;
    private List<ReceivedItem> items;
    private String assignedTo;
    private Instant assignedAt;
    private Instant expectedDate;
    private Instant arrivalDate;
    private Instant completedAt;
    private String carrierName;
    private String trackingNumber;
    private String receivingLocation;
    private List<QualityCheck> qualityChecks;
    private String notes;
    private boolean active;

    private ReceivingTask(ReceivingTaskId id) {
        super(id);
        this.items = new ArrayList<>();
        this.qualityChecks = new ArrayList<>();
        this.status = ReceivingStatus.EXPECTED;
        this.active = true;
    }

    private ReceivingTask() {
        super();
    }

    /**
     * Factory method to create a new receiving task.
     */
    public static ReceivingTask create(
            ReceivingTaskId id,
            String taskNumber,
            WarehouseId warehouseId,
            String purchaseOrderNumber,
            String supplierName,
            Instant expectedDate,
            String carrierName,
            String trackingNumber) {
        ReceivingTask task = new ReceivingTask(id);
        task.taskNumber = taskNumber;
        task.warehouseId = warehouseId;
        task.purchaseOrderNumber = purchaseOrderNumber;
        task.supplierName = supplierName;
        task.expectedDate = expectedDate;
        task.carrierName = carrierName;
        task.trackingNumber = trackingNumber;
        return task;
    }

    /**
     * Adds an item to the receiving task.
     */
    public void addItem(ReceivedItem item) {
        if (status != ReceivingStatus.EXPECTED && status != ReceivingStatus.ARRIVED) {
            throw new IllegalStateException("Cannot add items in status: " + status);
        }
        items.add(item);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the shipment as arrived.
     */
    public void markArrived() {
        if (status != ReceivingStatus.EXPECTED) {
            throw new IllegalStateException("Cannot mark arrived in status: " + status);
        }
        this.status = ReceivingStatus.ARRIVED;
        this.arrivalDate = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Starts quality check.
     */
    public void startQualityCheck() {
        if (status != ReceivingStatus.ARRIVED) {
            throw new IllegalStateException("Cannot start quality check in status: " + status);
        }
        this.status = ReceivingStatus.IN_QUALITY_CHECK;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Completes quality check.
     */
    public void completeQualityCheck(QualityCheckResult result, String checkedBy) {
        if (status != ReceivingStatus.IN_QUALITY_CHECK) {
            throw new IllegalStateException("Cannot complete quality check in status: " + status);
        }
        
        QualityCheck check = new QualityCheck(
            QualityCheckId.generate().toString(),
            checkedBy,
            result,
            Instant.now()
        );
        qualityChecks.add(check);
        
        if (result.isAccepted()) {
            this.status = ReceivingStatus.PASSED_QUALITY;
        } else {
            this.status = ReceivingStatus.FAILED_QUALITY;
            this.active = false;
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Receives an item.
     */
    public void receiveItem(String itemId, int quantity, String receivedBy) {
        if (status != ReceivingStatus.PASSED_QUALITY && status != ReceivingStatus.PARTIALLY_RECEIVED) {
            throw new IllegalStateException("Cannot receive item in status: " + status);
        }
        
        ReceivedItem item = items.stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        if (item.isFullyReceived()) {
            throw new IllegalStateException("Item already fully received: " + itemId);
        }

        item.receive(quantity, receivedBy);
        
        // Update status based on progress
        boolean allReceived = items.stream().allMatch(ReceivedItem::isFullyReceived);
        boolean anyReceived = items.stream().anyMatch(i -> i.getReceivedQuantity() > 0);
        
        if (allReceived) {
            this.status = ReceivingStatus.COMPLETED;
            this.completedAt = Instant.now();
        } else if (anyReceived) {
            this.status = ReceivingStatus.PARTIALLY_RECEIVED;
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Puts the receiving task on hold.
     */
    public void putOnHold(String reason) {
        if (status == ReceivingStatus.COMPLETED || status == ReceivingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot hold receiving in status: " + status);
        }
        this.status = ReceivingStatus.ON_HOLD;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Releases the receiving task from hold.
     */
    public void release() {
        if (status != ReceivingStatus.ON_HOLD) {
            throw new IllegalStateException("Cannot release receiving in status: " + status);
        }
        this.status = ReceivingStatus.ARRIVED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the receiving task.
     */
    public void cancel(String reason) {
        if (status == ReceivingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed receiving");
        }
        this.status = ReceivingStatus.CANCELLED;
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
        long received = items.stream().filter(ReceivedItem::isFullyReceived).count();
        return (double) received / items.size() * 100.0;
    }

    /**
     * Gets the total received quantity.
     */
    public int getTotalReceivedQuantity() {
        return items.stream()
            .mapToInt(ReceivedItem::getReceivedQuantity)
            .sum();
    }

    /**
     * Gets the total expected quantity.
     */
    public int getTotalExpectedQuantity() {
        return items.stream()
            .mapToInt(ReceivedItem::getExpectedQuantity)
            .sum();
    }

    /**
     * Gets items with quality issues.
     */
    public List<ReceivedItem> getItemsWithQualityIssues() {
        return items.stream()
            .filter(i -> !i.isQualityPassed())
            .collect(java.util.stream.Collectors.toList());
    }

    // Getters
    public String getTaskNumber() { return taskNumber; }
    public WarehouseId getWarehouseId() { return warehouseId; }
    public String getPurchaseOrderNumber() { return purchaseOrderNumber; }
    public String getShipmentNumber() { return shipmentNumber; }
    public String getSupplierName() { return supplierName; }
    public ReceivingStatus getStatus() { return status; }
    public List<ReceivedItem> getItems() { return Collections.unmodifiableList(items); }
    public String getAssignedTo() { return assignedTo; }
    public Instant getAssignedAt() { return assignedAt; }
    public Instant getExpectedDate() { return expectedDate; }
    public Instant getArrivalDate() { return arrivalDate; }
    public Instant getCompletedAt() { return completedAt; }
    public String getCarrierName() { return carrierName; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getReceivingLocation() { return receivingLocation; }
    public List<QualityCheck> getQualityChecks() { return Collections.unmodifiableList(qualityChecks); }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
        this.assignedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setReceivingLocation(String receivingLocation) {
        this.receivingLocation = receivingLocation;
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
        return "ReceivingTask{" +
                "id=" + getId() +
                ", taskNumber='" + taskNumber + '\'' +
                ", purchaseOrderNumber='" + purchaseOrderNumber + '\'' +
                ", supplierName='" + supplierName + '\'' +
                ", status=" + status +
                ", items=" + items.size() +
                ", progress=" + getProgress() + "%" +
                '}';
    }

    /**
     * Received item value object.
     */
    public static final class ReceivedItem implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String productId;
        private final String productName;
        private final String sku;
        private final int expectedQuantity;
        private int receivedQuantity;
        private String binLocationId;
        private boolean qualityPassed;
        private String qualityNotes;
        private boolean received;
        private String receivedBy;
        private Instant receivedAt;

        public ReceivedItem(
                String id,
                String productId,
                String productName,
                String sku,
                int expectedQuantity) {
            this.id = id;
            this.productId = productId;
            this.productName = productName;
            this.sku = sku;
            this.expectedQuantity = expectedQuantity;
            this.receivedQuantity = 0;
            this.qualityPassed = true;
            this.received = false;
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
            if (expectedQuantity <= 0) {
                throw new IllegalArgumentException("Expected quantity must be positive");
            }
        }

        public String getId() { return id; }
        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getSku() { return sku; }
        public int getExpectedQuantity() { return expectedQuantity; }
        public int getReceivedQuantity() { return receivedQuantity; }
        public int getRemainingQuantity() { return expectedQuantity - receivedQuantity; }
        public String getBinLocationId() { return binLocationId; }
        public boolean isQualityPassed() { return qualityPassed; }
        public String getQualityNotes() { return qualityNotes; }
        public boolean isFullyReceived() { return receivedQuantity >= expectedQuantity; }
        public String getReceivedBy() { return receivedBy; }
        public Instant getReceivedAt() { return receivedAt; }

        public void receive(int quantity, String receivedBy) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (quantity > getRemainingQuantity()) {
                throw new IllegalArgumentException("Quantity exceeds remaining: " + getRemainingQuantity());
            }
            this.receivedQuantity += quantity;
            this.receivedBy = receivedBy;
            this.receivedAt = Instant.now();
            this.received = true;
        }

        public void setQualityPassed(boolean qualityPassed) {
            this.qualityPassed = qualityPassed;
        }

        public void setQualityNotes(String qualityNotes) {
            this.qualityNotes = qualityNotes;
        }

        public void setBinLocationId(String binLocationId) {
            this.binLocationId = binLocationId;
        }

        @Override
        public String toString() {
            return "ReceivedItem{" +
                    "id='" + id + '\'' +
                    ", sku='" + sku + '\'' +
                    ", expected=" + expectedQuantity +
                    ", received=" + receivedQuantity +
                    ", qualityPassed=" + qualityPassed +
                    '}';
        }
    }

    /**
     * Quality check value object.
     */
    public static final class QualityCheck implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String checkedBy;
        private final QualityCheckResult result;
        private final Instant checkedAt;
        private String notes;

        public QualityCheck(String id, String checkedBy, QualityCheckResult result, Instant checkedAt) {
            this.id = id;
            this.checkedBy = checkedBy;
            this.result = result;
            this.checkedAt = checkedAt != null ? checkedAt : Instant.now();
            validate();
        }

        @Override
        public void validate() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Quality check ID cannot be empty");
            }
            if (checkedBy == null || checkedBy.trim().isEmpty()) {
                throw new IllegalArgumentException("Checked by cannot be empty");
            }
            if (result == null) {
                throw new IllegalArgumentException("Quality check result cannot be null");
            }
        }

        public String getId() { return id; }
        public String getCheckedBy() { return checkedBy; }
        public QualityCheckResult getResult() { return result; }
        public Instant getCheckedAt() { return checkedAt; }
        public String getNotes() { return notes; }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        @Override
        public String toString() {
            return "QualityCheck{" +
                    "id='" + id + '\'' +
                    ", checkedBy='" + checkedBy + '\'' +
                    ", result=" + result +
                    ", checkedAt=" + checkedAt +
                    '}';
        }
    }
}
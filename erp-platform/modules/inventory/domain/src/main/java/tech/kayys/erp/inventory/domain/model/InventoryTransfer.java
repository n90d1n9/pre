package tech.kayys.erp.inventory.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.inventory.domain.identifier.InventoryTransactionId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Inventory transfer aggregate root.
 * Represents transfer of stock between warehouses.
 */
public final class InventoryTransfer extends AggregateRoot<InventoryTransactionId> {
    
    private static final long serialVersionUID = 1L;
    
    private String transferNumber;
    private WarehouseId sourceWarehouseId;
    private WarehouseId destinationWarehouseId;
    private List<TransferItem> items;
    private String status;
    private Instant transferDate;
    private String createdBy;
    private String approvedBy;
    private Instant approvedAt;
    private String notes;
    private boolean active;

    private InventoryTransfer(InventoryTransactionId id) {
        super(id);
        this.items = new ArrayList<>();
        this.status = "DRAFT";
        this.active = true;
        this.transferDate = Instant.now();
    }

    private InventoryTransfer() {
        super();
    }

    /**
     * Factory method to create a new inventory transfer.
     */
    public static InventoryTransfer create(
            InventoryTransactionId id,
            WarehouseId sourceWarehouseId,
            WarehouseId destinationWarehouseId,
            String createdBy) {
        InventoryTransfer transfer = new InventoryTransfer(id);
        transfer.sourceWarehouseId = sourceWarehouseId;
        transfer.destinationWarehouseId = destinationWarehouseId;
        transfer.createdBy = createdBy;
        transfer.transferNumber = "TRF-" + System.currentTimeMillis();
        return transfer;
    }

    /**
     * Adds an item to the transfer.
     */
    public void addItem(TransferItem item) {
        if (!"DRAFT".equals(status)) {
            throw new IllegalStateException("Cannot modify transfer in status: " + status);
        }
        items.add(item);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Submits the transfer for approval.
     */
    public void submitForApproval() {
        if (!"DRAFT".equals(status)) {
            throw new IllegalStateException("Cannot submit transfer in status: " + status);
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Transfer must have at least one item");
        }
        this.status = "PENDING_APPROVAL";
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Approves the transfer.
     */
    public void approve(String approvedBy) {
        if (!"PENDING_APPROVAL".equals(status)) {
            throw new IllegalStateException("Cannot approve transfer in status: " + status);
        }
        this.status = "APPROVED";
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Completes the transfer.
     */
    public void complete() {
        if (!"APPROVED".equals(status)) {
            throw new IllegalStateException("Cannot complete transfer in status: " + status);
        }
        this.status = "COMPLETED";
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the transfer.
     */
    public void cancel(String reason) {
        if ("COMPLETED".equals(status)) {
            throw new IllegalStateException("Cannot cancel completed transfer");
        }
        this.status = "CANCELLED";
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the total quantity of items in the transfer.
     */
    public int getTotalQuantity() {
        return items.stream()
            .mapToInt(TransferItem::getQuantity)
            .sum();
    }

    // Getters
    public String getTransferNumber() { return transferNumber; }
    public WarehouseId getSourceWarehouseId() { return sourceWarehouseId; }
    public WarehouseId getDestinationWarehouseId() { return destinationWarehouseId; }
    public List<TransferItem> getItems() { return Collections.unmodifiableList(items); }
    public String getStatus() { return status; }
    public Instant getTransferDate() { return transferDate; }
    public String getCreatedBy() { return createdBy; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "InventoryTransfer{" +
                "id=" + getId() +
                ", transferNumber='" + transferNumber + '\'' +
                ", source=" + sourceWarehouseId +
                ", destination=" + destinationWarehouseId +
                ", status=" + status +
                ", items=" + items.size() +
                '}';
    }

    /**
     * Transfer item value object.
     */
    public static final class TransferItem implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String productId;
        private final String sku;
        private final String productName;
        private final int quantity;
        private final String uom;

        public TransferItem(String productId, String sku, String productName, int quantity, String uom) {
            this.productId = productId;
            this.sku = sku;
            this.productName = productName;
            this.quantity = quantity;
            this.uom = uom;
            validate();
        }

        @Override
        public void validate() {
            if (productId == null || productId.trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be empty");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
        }

        public String getProductId() { return productId; }
        public String getSku() { return sku; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public String getUom() { return uom; }

        @Override
        public String toString() {
            return "TransferItem{" +
                    "productId='" + productId + '\'' +
                    ", quantity=" + quantity +
                    '}';
        }
    }
}
package tech.kayys.erp.inventory.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.inventory.domain.identifier.ProductId;
import tech.kayys.erp.inventory.domain.identifier.WarehouseId;
import tech.kayys.erp.inventory.domain.valueobject.InventoryStatus;
import tech.kayys.erp.inventory.domain.valueobject.ReorderLevel;

import java.time.Instant;

/**
 * Inventory item aggregate root.
 * Represents stock for a specific product in a specific warehouse.
 */
public final class InventoryItem extends AggregateRoot<ProductId> {
    
    private static final long serialVersionUID = 1L;
    
    private ProductId productId;
    private WarehouseId warehouseId;
    private String sku;
    private String productName;
    private int quantityOnHand;
    private int reservedQuantity;
    private int availableQuantity;
    private int reorderPoint;
    private int reorderQuantity;
    private int maximumStock;
    private int minimumStock;
    private InventoryStatus status;
    private ReorderLevel reorderLevel;
    private String binLocation;
    private String serialNumber;
    private String lotNumber;
    private Instant expiryDate;
    private boolean active;
    private String currencyCode;
    private double unitCost;
    private double averageCost;

    private InventoryItem(ProductId id) {
        super(id);
        this.status = InventoryStatus.AVAILABLE;
        this.active = true;
        this.availableQuantity = 0;
        this.reservedQuantity = 0;
        this.quantityOnHand = 0;
    }

    private InventoryItem() {
        super();
    }

    /**
     * Factory method to create a new inventory item.
     */
    public static InventoryItem create(
            ProductId productId,
            WarehouseId warehouseId,
            String sku,
            String productName,
            String currencyCode) {
        InventoryItem item = new InventoryItem(productId);
        item.productId = productId;
        item.warehouseId = warehouseId;
        item.sku = sku;
        item.productName = productName;
        item.currencyCode = currencyCode;
        return item;
    }

    /**
     * Receives stock into inventory.
     */
    public void receive(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantityOnHand += quantity;
        this.availableQuantity = this.quantityOnHand - this.reservedQuantity;
        if (this.availableQuantity < 0) {
            this.availableQuantity = 0;
        }
        this.status = InventoryStatus.AVAILABLE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Issues stock from inventory.
     */
    public void issue(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > this.availableQuantity) {
            throw new IllegalArgumentException("Insufficient stock: available=" + availableQuantity + ", requested=" + quantity);
        }
        this.quantityOnHand -= quantity;
        this.availableQuantity = this.quantityOnHand - this.reservedQuantity;
        if (this.availableQuantity < 0) {
            this.availableQuantity = 0;
        }
        if (this.quantityOnHand == 0) {
            this.status = InventoryStatus.OUT_OF_STOCK;
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Reserves stock for an order.
     */
    public void reserve(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        int available = this.quantityOnHand - this.reservedQuantity;
        if (quantity > available) {
            throw new IllegalArgumentException("Insufficient stock: available=" + available + ", requested=" + quantity);
        }
        this.reservedQuantity += quantity;
        this.availableQuantity = this.quantityOnHand - this.reservedQuantity;
        if (this.reservedQuantity > 0) {
            this.status = InventoryStatus.RESERVED;
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Releases reserved stock.
     */
    public void releaseReservation(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > this.reservedQuantity) {
            throw new IllegalArgumentException("Cannot release more than reserved: reserved=" + reservedQuantity);
        }
        this.reservedQuantity -= quantity;
        this.availableQuantity = this.quantityOnHand - this.reservedQuantity;
        if (this.reservedQuantity == 0 && this.quantityOnHand > 0) {
            this.status = InventoryStatus.AVAILABLE;
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adjusts inventory quantity.
     */
    public void adjust(int quantity, String reason) {
        if (quantity == 0) {
            return;
        }
        if (quantity > 0) {
            this.quantityOnHand += quantity;
        } else {
            int removal = Math.abs(quantity);
            if (removal > this.quantityOnHand) {
                throw new IllegalArgumentException("Cannot remove more than on hand: onHand=" + quantityOnHand);
            }
            this.quantityOnHand -= removal;
            if (this.quantityOnHand < 0) {
                this.quantityOnHand = 0;
            }
        }
        this.availableQuantity = this.quantityOnHand - this.reservedQuantity;
        if (this.quantityOnHand == 0) {
            this.status = InventoryStatus.OUT_OF_STOCK;
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if reorder is needed.
     */
    public boolean needsReorder() {
        if (reorderLevel == null) {
            return false;
        }
        return reorderLevel.needsReorder(availableQuantity);
    }

    /**
     * Gets the reorder amount.
     */
    public int getReorderAmount() {
        if (reorderLevel == null) {
            return 0;
        }
        return reorderLevel.getReorderAmount(availableQuantity);
    }

    /**
     * Transfers stock to another warehouse.
     */
    public void transfer(int quantity) {
        issue(quantity);
        // The receiving warehouse will be handled separately
    }

    // Getters
    public ProductId getProductId() { return productId; }
    public WarehouseId getWarehouseId() { return warehouseId; }
    public String getSku() { return sku; }
    public String getProductName() { return productName; }
    public int getQuantityOnHand() { return quantityOnHand; }
    public int getReservedQuantity() { return reservedQuantity; }
    public int getAvailableQuantity() { return availableQuantity; }
    public int getReorderPoint() { return reorderPoint; }
    public int getReorderQuantity() { return reorderQuantity; }
    public int getMaximumStock() { return maximumStock; }
    public int getMinimumStock() { return minimumStock; }
    public InventoryStatus getStatus() { return status; }
    public ReorderLevel getReorderLevel() { return reorderLevel; }
    public String getBinLocation() { return binLocation; }
    public String getSerialNumber() { return serialNumber; }
    public String getLotNumber() { return lotNumber; }
    public Instant getExpiryDate() { return expiryDate; }
    public boolean isActive() { return active; }
    public String getCurrencyCode() { return currencyCode; }
    public double getUnitCost() { return unitCost; }
    public double getAverageCost() { return averageCost; }

    public void setReorderLevel(ReorderLevel reorderLevel) {
        this.reorderLevel = reorderLevel;
        this.reorderPoint = reorderLevel.getReorderPoint();
        this.reorderQuantity = reorderLevel.getReorderQuantity();
        this.maximumStock = reorderLevel.getMaximumStock();
        this.minimumStock = reorderLevel.getMinimumStock();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setBinLocation(String binLocation) {
        this.binLocation = binLocation;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
        // Update average cost
        this.averageCost = (this.averageCost + unitCost) / 2;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "productId=" + productId +
                ", sku='" + sku + '\'' +
                ", warehouseId=" + warehouseId +
                ", available=" + availableQuantity +
                ", reserved=" + reservedQuantity +
                ", status=" + status +
                '}';
    }
}
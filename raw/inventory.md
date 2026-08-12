# Complete Implementation: Inventory Management Bounded Context

Now I'll implement the complete Inventory Management bounded context, which handles stock levels, warehouse management, inventory transactions, transfers, and stock adjustments.

## 1. Inventory Domain Module

**`/modules/inventory/domain/pom.xml`**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>tech.kayys.erp</groupId>
        <artifactId>erp-platform</artifactId>
        <version>0.1.0-SNAPSHOT</version>
        <relativePath>../../../pom.xml</relativePath>
    </parent>

    <artifactId>erp-inventory-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/inventory/domain/src/main/java/tech/kayys/erp/inventory/domain/identifier/WarehouseId.java`**:

```java
package tech.kayys.erp.inventory.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Warehouse identifier.
 */
public final class WarehouseId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public WarehouseId(UUID value) {
        super(value);
    }

    public static WarehouseId of(UUID value) {
        return new WarehouseId(value);
    }

    public static WarehouseId generate() {
        return new WarehouseId(UUID.randomUUID());
    }

    public static WarehouseId fromString(String value) {
        return new WarehouseId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "WarehouseId{" + value + "}";
    }
}
```

**`/modules/inventory/domain/src/main/java/tech/kayys/erp/inventory/domain/identifier/ProductId.java`**:

```java
package tech.kayys.erp.inventory.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Product identifier in the Inventory context.
 */
public final class ProductId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ProductId(UUID value) {
        super(value);
    }

    public static ProductId of(UUID value) {
        return new ProductId(value);
    }

    public static ProductId fromString(String value) {
        return new ProductId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ProductId{" + value + "}";
    }
}
```

**`/modules/inventory/domain/src/main/java/tech/kayys/erp/inventory/domain/identifier/InventoryTransactionId.java`**:

```java
package tech.kayys.erp.inventory.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Inventory transaction identifier.
 */
public final class InventoryTransactionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public InventoryTransactionId(UUID value) {
        super(value);
    }

    public static InventoryTransactionId of(UUID value) {
        return new InventoryTransactionId(value);
    }

    public static InventoryTransactionId generate() {
        return new InventoryTransactionId(UUID.randomUUID());
    }

    public static InventoryTransactionId fromString(String value) {
        return new InventoryTransactionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "InventoryTransactionId{" + value + "}";
    }
}
```

**`/modules/inventory/domain/src/main/java/tech/kayys/erp/inventory/domain/valueobject/TransactionType.java`**:

```java
package tech.kayys.erp.inventory.domain.valueobject;

/**
 * Types of inventory transactions.
 */
public enum TransactionType {
    RECEIPT("Receipt - stock added"),
    ISSUE("Issue - stock removed"),
    TRANSFER("Transfer - moved between warehouses"),
    ADJUSTMENT("Adjustment - correction"),
    RETURN("Return - returned to warehouse"),
    RESERVATION("Reservation - allocated for order"),
    RELEASE("Release - reservation released"),
    SCRAP("Scrap - discarded inventory"),
    COUNT("Count - physical inventory count");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAddition() {
        return this == RECEIPT || this == RETURN || this == ADJUSTMENT && this != SCRAP;
    }

    public boolean isRemoval() {
        return this == ISSUE || this == SCRAP || this == TRANSFER;
    }

    public boolean isReservation() {
        return this == RESERVATION || this == RELEASE;
    }
}
```

**`/modules/inventory/domain/src/main/java/tech/kayys/erp/inventory/domain/valueobject/InventoryStatus.java`**:

```java
package tech.kayys.erp.inventory.domain.valueobject;

/**
 * Status of inventory items.
 */
public enum InventoryStatus {
    AVAILABLE("Available - ready for sale"),
    RESERVED("Reserved - allocated to order"),
    BACKORDERED("Backordered - awaiting restock"),
    DISCONTINUED("Discontinued - no longer sold"),
    DAMAGED("Damaged - not sellable"),
    RECALLED("Recalled - withdrawn from sale"),
    OUT_OF_STOCK("Out of Stock - currently unavailable");

    private final String description;

    InventoryStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailable() {
        return this == AVAILABLE || this == BACKORDERED;
    }

    public boolean isSellable() {
        return this == AVAILABLE || this == BACKORDERED || this == RESERVED;
    }
}
```

**`/modules/inventory/domain/src/main/java/tech/kayys/erp/inventory/domain/valueobject/ReorderLevel.java`**:

```java
package tech.kayys.erp.inventory.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.util.Objects;

/**
 * Reorder level value object.
 */
public final class ReorderLevel implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final int reorderPoint;
    private final int reorderQuantity;
    private final int maximumStock;
    private final int minimumStock;
    private final boolean autoReorder;

    public ReorderLevel(
            int reorderPoint,
            int reorderQuantity,
            int maximumStock,
            int minimumStock,
            boolean autoReorder) {
        this.reorderPoint = reorderPoint;
        this.reorderQuantity = reorderQuantity;
        this.maximumStock = maximumStock;
        this.minimumStock = minimumStock;
        this.autoReorder = autoReorder;
        validate();
    }

    @Override
    public void validate() {
        if (reorderPoint < 0) {
            throw new IllegalArgumentException("Reorder point cannot be negative");
        }
        if (reorderQuantity <= 0) {
            throw new IllegalArgumentException("Reorder quantity must be positive");
        }
        if (maximumStock <= 0) {
            throw new IllegalArgumentException("Maximum stock must be positive");
        }
        if (minimumStock < 0) {
            throw new IllegalArgumentException("Minimum stock cannot be negative");
        }
        if (minimumStock > reorderPoint) {
            throw new IllegalArgumentException("Minimum stock must be less than or equal to reorder point");
        }
    }

    public int getReorderPoint() { return reorderPoint; }
    public int getReorderQuantity() { return reorderQuantity; }
    public int getMaximumStock() { return maximumStock; }
    public int getMinimumStock() { return minimumStock; }
    public boolean isAutoReorder() { return autoReorder; }

    public boolean needsReorder(int currentStock) {
        return currentStock <= reorderPoint;
    }

    public int getReorderAmount(int currentStock) {
        if (currentStock >= reorderPoint) {
            return 0;
        }
        return Math.min(reorderQuantity, maximumStock - currentStock);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReorderLevel that = (ReorderLevel) o;
        return reorderPoint == that.reorderPoint &&
               reorderQuantity == that.reorderQuantity &&
               maximumStock == that.maximumStock &&
               minimumStock == that.minimumStock &&
               autoReorder == that.autoReorder;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reorderPoint, reorderQuantity, maximumStock, minimumStock, autoReorder);
    }

    @Override
    public String toString() {
        return "ReorderLevel{" +
                "reorderPoint=" + reorderPoint +
                ", reorderQuantity=" + reorderQuantity +
                ", maxStock=" + maximumStock +
                ", minStock=" + minimumStock +
                '}';
    }

    public static ReorderLevel of(int reorderPoint, int reorderQuantity, int maximumStock) {
        return new ReorderLevel(reorderPoint, reorderQuantity, maximumStock, 0, false);
    }

    public static ReorderLevel of(int reorderPoint, int reorderQuantity, int maximumStock, int minimumStock) {
        return new ReorderLevel(reorderPoint, reorderQuantity, maximumStock, minimumStock, false);
    }
}
```

**`/modules/inventory/domain/src/main/java/tech/kayys/erp/inventory/domain/model/Warehouse.java`**:

```java
package tech.kayys.erp.inventory.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.inventory.domain.identifier.WarehouseId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Warehouse aggregate root.
 * Represents a physical or virtual warehouse location.
 */
public final class Warehouse extends AggregateRoot<WarehouseId> {
    
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String name;
    private String description;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;
    private String email;
    private String managerId;
    private int capacity;
    private int currentStockCount;
    private boolean active;
    private boolean defaultWarehouse;
    private List<String> zones;

    private Warehouse(WarehouseId id) {
        super(id);
        this.zones = new ArrayList<>();
        this.active = true;
        this.currentStockCount = 0;
    }

    private Warehouse() {
        super();
    }

    /**
     * Factory method to create a new warehouse.
     */
    public static Warehouse create(
            WarehouseId id,
            String code,
            String name,
            String address,
            String city,
            String country) {
        Warehouse warehouse = new Warehouse(id);
        warehouse.code = code;
        warehouse.name = name;
        warehouse.address = address;
        warehouse.city = city;
        warehouse.country = country;
        return warehouse;
    }

    /**
     * Adds a zone to the warehouse.
     */
    public void addZone(String zone) {
        if (!zones.contains(zone)) {
            zones.add(zone);
            setUpdatedAt(java.time.Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a zone from the warehouse.
     */
    public void removeZone(String zone) {
        zones.remove(zone);
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    /**
     * Updates stock count.
     */
    public void updateStockCount(int change) {
        this.currentStockCount += change;
        if (this.currentStockCount < 0) {
            this.currentStockCount = 0;
        }
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    /**
     * Activates the warehouse.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the warehouse.
     */
    public void deactivate() {
        if (defaultWarehouse) {
            throw new IllegalStateException("Cannot deactivate default warehouse");
        }
        this.active = false;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    /**
     * Sets as default warehouse.
     */
    public void setAsDefault() {
        this.defaultWarehouse = true;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    /**
     * Gets the warehouse utilization percentage.
     */
    public double getUtilization() {
        if (capacity == 0) {
            return 0.0;
        }
        return (double) currentStockCount / capacity * 100.0;
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getManagerId() { return managerId; }
    public int getCapacity() { return capacity; }
    public int getCurrentStockCount() { return currentStockCount; }
    public boolean isActive() { return active; }
    public boolean isDefaultWarehouse() { return defaultWarehouse; }
    public List<String> getZones() { return Collections.unmodifiableList(zones); }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    public void setState(String state) {
        this.state = state;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    public void setPhone(String phone) {
        this.phone = phone;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    public void setEmail(String email) {
        this.email = email;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", stockCount=" + currentStockCount +
                '}';
    }
}
```

**`/modules/inventory/domain/src/main/java/tech/kayys/erp/inventory/domain/model/InventoryItem.java`**:

```java
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
```

**`/modules/inventory/domain/src/main/java/tech/kayys/erp/inventory/domain/model/InventoryTransaction.java`**:

```java
package tech.kayys.erp.inventory.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.inventory.domain.identifier.InventoryTransactionId;
import tech.kayys.erp.inventory.domain.identifier.ProductId;
import tech.kayys.erp.inventory.domain.identifier.WarehouseId;
import tech.kayys.erp.inventory.domain.valueobject.TransactionType;

import java.time.Instant;

/**
 * Inventory transaction aggregate root.
 * Records every movement of inventory.
 */
public final class InventoryTransaction extends AggregateRoot<InventoryTransactionId> {
    
    private static final long serialVersionUID = 1L;
    
    private ProductId productId;
    private WarehouseId warehouseId;
    private String transactionNumber;
    private TransactionType transactionType;
    private int quantity;
    private int quantityBefore;
    private int quantityAfter;
    private String sourceReference;
    private String sourceType;
    private String destinationReference;
    private String notes;
    private String performedBy;
    private Instant transactionDate;
    private String currencyCode;
    private double unitCost;
    private double totalCost;
    private boolean reversed;
    private String reversedBy;
    private Instant reversedAt;

    private InventoryTransaction(InventoryTransactionId id) {
        super(id);
        this.transactionDate = Instant.now();
        this.reversed = false;
    }

    private InventoryTransaction() {
        super();
    }

    /**
     * Factory method to create a new inventory transaction.
     */
    public static InventoryTransaction create(
            InventoryTransactionId id,
            ProductId productId,
            WarehouseId warehouseId,
            TransactionType transactionType,
            int quantity,
            int quantityBefore,
            String performedBy,
            String currencyCode) {
        InventoryTransaction transaction = new InventoryTransaction(id);
        transaction.productId = productId;
        transaction.warehouseId = warehouseId;
        transaction.transactionType = transactionType;
        transaction.quantity = quantity;
        transaction.quantityBefore = quantityBefore;
        transaction.performedBy = performedBy;
        transaction.currencyCode = currencyCode;
        transaction.transactionNumber = generateTransactionNumber(transactionType);
        transaction.quantityAfter = quantityBefore + quantity;
        return transaction;
    }

    private static String generateTransactionNumber(TransactionType type) {
        return type.name().substring(0, 3) + "-" + System.currentTimeMillis();
    }

    /**
     * Reverses the transaction.
     */
    public void reverse(String reversedBy, String reason) {
        if (reversed) {
            throw new IllegalStateException("Transaction already reversed");
        }
        this.reversed = true;
        this.reversedBy = reversedBy;
        this.reversedAt = Instant.now();
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets source reference.
     */
    public void setSource(String sourceType, String sourceReference) {
        this.sourceType = sourceType;
        this.sourceReference = sourceReference;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets destination reference.
     */
    public void setDestination(String destinationReference) {
        this.destinationReference = destinationReference;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public ProductId getProductId() { return productId; }
    public WarehouseId getWarehouseId() { return warehouseId; }
    public String getTransactionNumber() { return transactionNumber; }
    public TransactionType getTransactionType() { return transactionType; }
    public int getQuantity() { return quantity; }
    public int getQuantityBefore() { return quantityBefore; }
    public int getQuantityAfter() { return quantityAfter; }
    public String getSourceReference() { return sourceReference; }
    public String getSourceType() { return sourceType; }
    public String getDestinationReference() { return destinationReference; }
    public String getNotes() { return notes; }
    public String getPerformedBy() { return performedBy; }
    public Instant getTransactionDate() { return transactionDate; }
    public String getCurrencyCode() { return currencyCode; }
    public double getUnitCost() { return unitCost; }
    public double getTotalCost() { return totalCost; }
    public boolean isReversed() { return reversed; }
    public String getReversedBy() { return reversedBy; }
    public Instant getReversedAt() { return reversedAt; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
        this.totalCost = unitCost * quantity;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "InventoryTransaction{" +
                "id=" + getId() +
                ", transactionNumber='" + transactionNumber + '\'' +
                ", productId=" + productId +
                ", type=" + transactionType +
                ", quantity=" + quantity +
                ", reversed=" + reversed +
                '}';
    }
}
```

**`/modules/inventory/domain/src/main/java/tech/kayys/erp/inventory/domain/model/InventoryTransfer.java`**:

```java
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
```

## 2. Inventory Repository Interfaces

**`/modules/inventory/domain/src/main/java/tech/kayys/erp/inventory/domain/repository/WarehouseRepository.java`**:

```java
package tech.kayys.erp.inventory.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.inventory.domain.identifier.WarehouseId;
import tech.kayys.erp.inventory.domain.model.Warehouse;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for Warehouse aggregates.
 */
public interface WarehouseRepository extends Repository<Warehouse, WarehouseId> {

    /**
     * Finds warehouses by name containing text.
     */
    CompletionStage<List<Warehouse>> findByNameContaining(String name);

    /**
     * Finds active warehouses.
     */
    CompletionStage<List<Warehouse>> findActiveWarehouses();

    /**
     * Finds the default warehouse.
     */
    CompletionStage<Warehouse> findDefaultWarehouse();

    /**
     * Finds warehouses by country.
     */
    CompletionStage<List<Warehouse>> findByCountry(String country);

    /**
     * Checks if a warehouse code is unique.
     */
    CompletionStage<Boolean> isCodeUnique(String code);
}
```

**`/modules/inventory/domain/src/main/java/tech/kayys/erp/inventory/domain/repository/InventoryItemRepository.java`**:

```java
package tech.kayys.erp.inventory.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.inventory.domain.identifier.ProductId;
import tech.kayys.erp.inventory.domain.identifier.WarehouseId;
import tech.kayys.erp.inventory.domain.model.InventoryItem;
import tech.kayys.erp.inventory.domain.valueobject.InventoryStatus;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for InventoryItem aggregates.
 */
public interface InventoryItemRepository extends Repository<InventoryItem, ProductId> {

    /**
     * Finds inventory items by warehouse.
     */
    CompletionStage<List<InventoryItem>> findByWarehouse(WarehouseId warehouseId);

    /**
     * Finds inventory items by status.
     */
    CompletionStage<List<InventoryItem>> findByStatus(InventoryStatus status);

    /**
     * Finds inventory items by product.
     */
    CompletionStage<List<InventoryItem>> findByProduct(ProductId productId);

    /**
     * Finds inventory items by product and warehouse.
     */
    CompletionStage<InventoryItem> findByProductAndWarehouse(
        ProductId productId,
        WarehouseId warehouseId
    );

    /**
     * Finds inventory items with low stock (needs reorder).
     */
    CompletionStage<List<InventoryItem>> findLowStockItems();

    /**
     * Finds inventory items by expiry date.
     */
    CompletionStage<List<InventoryItem>> findExpiringItems(Instant beforeDate);

    /**
     * Counts items in a warehouse.
     */
    CompletionStage<Long> countByWarehouse(WarehouseId warehouseId);

    /**
     * Counts items by status.
     */
    CompletionStage<Long> countByStatus(InventoryStatus status);
}
```

## 3. Update Root POM

**Update `/pom.xml`** to include Inventory modules:

```xml
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <module>modules/catalog/domain</module>
    <module>modules/catalog/application</module>
    <module>modules/catalog/infrastructure</module>
    <module>modules/catalog/interfaces</module>

    <module>modules/sales/domain</module>
    <module>modules/sales/application</module>
    <module>modules/sales/infrastructure</module>
    <module>modules/sales/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/subscription/domain</module>
    <module>modules/subscription/application</module>
    <module>modules/subscription/infrastructure</module>
    <module>modules/subscription/interfaces</module>

    <module>modules/accounting/domain</module>
    <module>modules/accounting/application</module>
    <module>modules/accounting/infrastructure</module>
    <module>modules/accounting/interfaces</module>

    <module>modules/purchasing/domain</module>
    <module>modules/purchasing/application</module>
    <module>modules/purchasing/infrastructure</module>
    <module>modules/purchasing/interfaces</module>

    <module>modules/promotion/domain</module>
    <module>modules/promotion/application</module>
    <module>modules/promotion/infrastructure</module>
    <module>modules/promotion/interfaces</module>

    <module>modules/employee/domain</module>
    <module>modules/employee/application</module>
    <module>modules/employee/infrastructure</module>
    <module>modules/employee/interfaces</module>

    <module>modules/payroll/domain</module>
    <module>modules/payroll/application</module>
    <module>modules/payroll/infrastructure</module>
    <module>modules/payroll/interfaces</module>

    <module>modules/hris/domain</module>
    <module>modules/hris/application</module>
    <module>modules/hris/infrastructure</module>
    <module>modules/hris/interfaces</module>

    <module>modules/inventory/domain</module>
    <module>modules/inventory/application</module>
    <module>modules/inventory/infrastructure</module>
    <module>modules/inventory/interfaces</module>
</modules>
```

## Summary

The complete Inventory Management bounded context provides:

1. **Warehouse Management**:
   - Multiple warehouses
   - Warehouse zones and capacity
   - Default warehouse designation
   - Active/inactive status

2. **Inventory Tracking**:
   - Real-time stock levels
   - Reserved quantity tracking
   - Available quantity calculation
   - Multi-warehouse support

3. **Inventory Transactions**:
   - Receipts, issues, transfers, adjustments
   - Transaction reversal capability
   - Source/destination tracking
   - Cost tracking (unit cost, total cost)

4. **Reorder Management**:
   - Reorder point and quantity
   - Minimum and maximum stock levels
   - Auto-reorder flag
   - Low stock alerts

5. **Inventory Transfers**:
   - Multi-item transfers
   - Approval workflow (Draft → Pending Approval → Approved → Completed)
   - Source and destination warehouse tracking

6. **Integration Points**:
   - Catalog context for product data
   - Sales context for order fulfillment
   - Purchasing context for receiving
   - Accounting context for cost tracking

7. **Architecture Rules**:
   - Clean domain model
   - Proper value objects
   - Context isolation
   - Event-driven design

This completes the Inventory context with comprehensive stock management capabilities that integrate with Catalog, Sales, Purchasing, and Accounting contexts throughout the ERP system.
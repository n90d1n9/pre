# Complete Implementation: Grocery Point of Sale (POS) Bounded Context

Based on the existing ERP architecture, I'll implement the Grocery POS bounded context that extends the sales/inventory modules with grocery-specific features. Grocery POS is a specialized module that sits atop the existing architecture, adding unique capabilities for fresh goods management, scale integration, and high-speed checkout.

## Overview of Grocery POS Module

Grocery POS differs from standard POS in several key ways:
- **Weight-based pricing** for fresh goods (produce, meat, deli) 
- **Shelf life & expiry tracking** for perishable items 
- **Fast checkout** for high customer throughput 
- **Scale integration** with deli/fresh item weighing 
- **Fresh-specific promotions** (time-of-day, bundle, BOGO) 
- **Electronic shelf labels** integration 
- **Self-checkout kiosk** support 

## 1. Grocery POS Domain Module

**`/modules/grocery-pos/domain/pom.xml`**:

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

    <artifactId>erp-grocery-pos-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-catalog-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-inventory-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/grocery-pos/domain/src/main/java/tech/kayys/erp/groceries/domain/identifier/ScaleId.java`**:

```java
package tech.kayys.erp.groceries.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Scale device identifier.
 */
public final class ScaleId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ScaleId(UUID value) {
        super(value);
    }

    public static ScaleId of(UUID value) {
        return new ScaleId(value);
    }

    public static ScaleId generate() {
        return new ScaleId(UUID.randomUUID());
    }

    public static ScaleId fromString(String value) {
        return new ScaleId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ScaleId{" + value + "}";
    }
}
```

**`/modules/grocery-pos/domain/src/main/java/tech/kayys/erp/groceries/domain/identifier/ShelfItemId.java`**:

```java
package tech.kayys.erp.groceries.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Shelf item identifier for tracking shelf placement.
 */
public final class ShelfItemId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ShelfItemId(UUID value) {
        super(value);
    }

    public static ShelfItemId of(UUID value) {
        return new ShelfItemId(value);
    }

    public static ShelfItemId generate() {
        return new ShelfItemId(UUID.randomUUID());
    }

    public static ShelfItemId fromString(String value) {
        return new ShelfItemId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ShelfItemId{" + value + "}";
    }
}
```

**`/modules/grocery-pos/domain/src/main/java/tech/kayys/erp/groceries/domain/valueobject/GroceryProductType.java`**:

```java
package tech.kayys.erp.groceries.domain.valueobject;

/**
 * Grocery product types with specific handling requirements.
 */
public enum GroceryProductType {
    AMBIENT("Ambient - Room temperature storage"),
    CHILLED("Chilled - Refrigerated"),
    FROZEN("Frozen - Below freezing"),
    FRESH("Fresh - Perishable, requires weight check"),
    DELI("Deli - Fresh sliced/weighed"),
    BAKERY("Bakery - Fresh baked"),
    PRODUCE("Produce - Fresh fruits/vegetables"),
    MEAT("Meat - Fresh/sealed meat products"),
    SEAFOOD("Seafood - Fresh/frozen seafood"),
    DAIRY("Dairy - Refrigerated dairy products"),
    NON_FOOD("Non-Food - General merchandise"),
    BEVERAGE("Beverage - Drinks");

    private final String description;

    GroceryProductType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPerishable() {
        return this == FRESH || this == DELI || this == BAKERY || 
               this == PRODUCE || this == MEAT || this == SEAFOOD || 
               this == DAIRY;
    }

    public boolean requiresWeight() {
        return this == FRESH || this == DELI || this == PRODUCE || 
               this == MEAT || this == SEAFOOD;
    }

    public boolean requiresTemperatureControl() {
        return this == CHILLED || this == FROZEN || this == DAIRY || 
               this == MEAT || this == SEAFOOD;
    }
}
```

**`/modules/grocery-pos/domain/src/main/java/tech/kayys/erp/groceries/domain/valueobject/Weight.java`**:

```java
package tech.kayys.erp.groceries.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Weight value object with unit support.
 */
public final class Weight implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final BigDecimal value;
    private final WeightUnit unit;

    public Weight(BigDecimal value, WeightUnit unit) {
        this.value = value.setScale(3, RoundingMode.HALF_UP);
        this.unit = unit;
        validate();
    }

    @Override
    public void validate() {
        if (value == null) {
            throw new IllegalArgumentException("Weight value cannot be null");
        }
        if (value.signum() < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Weight unit cannot be null");
        }
    }

    public BigDecimal getValue() { return value; }
    public WeightUnit getUnit() { return unit; }

    public Weight add(Weight other) {
        if (this.unit != other.unit) {
            // Convert to grams for addition, then back
            BigDecimal thisGrams = this.toGrams();
            BigDecimal otherGrams = other.toGrams();
            return Weight.fromGrams(thisGrams.add(otherGrams));
        }
        return new Weight(value.add(other.value), unit);
    }

    public Weight subtract(Weight other) {
        if (this.unit != other.unit) {
            BigDecimal thisGrams = this.toGrams();
            BigDecimal otherGrams = other.toGrams();
            return Weight.fromGrams(thisGrams.subtract(otherGrams));
        }
        return new Weight(value.subtract(other.value), unit);
    }

    public Weight multiply(BigDecimal multiplier) {
        return new Weight(value.multiply(multiplier), unit);
    }

    public Weight multiply(int multiplier) {
        return multiply(BigDecimal.valueOf(multiplier));
    }

    public BigDecimal toGrams() {
        return switch (unit) {
            case GRAM -> value;
            case KILOGRAM -> value.multiply(BigDecimal.valueOf(1000));
            case OUNCE -> value.multiply(BigDecimal.valueOf(28.3495));
            case POUND -> value.multiply(BigDecimal.valueOf(453.592));
        };
    }

    public BigDecimal toKilograms() {
        return toGrams().divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);
    }

    public int compareTo(Weight other) {
        return this.toGrams().compareTo(other.toGrams());
    }

    public boolean isGreaterThan(Weight other) {
        return compareTo(other) > 0;
    }

    public boolean isLessThan(Weight other) {
        return compareTo(other) < 0;
    }

    public boolean isZero() {
        return value.signum() == 0;
    }

    public boolean isPositive() {
        return value.signum() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weight weight = (Weight) o;
        return value.compareTo(weight.value) == 0 && unit == weight.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, unit);
    }

    @Override
    public String toString() {
        return value.toPlainString() + " " + unit.getSymbol();
    }

    public static Weight of(BigDecimal value, WeightUnit unit) {
        return new Weight(value, unit);
    }

    public static Weight of(String value, WeightUnit unit) {
        return new Weight(new BigDecimal(value), unit);
    }

    public static Weight of(double value, WeightUnit unit) {
        return new Weight(BigDecimal.valueOf(value), unit);
    }

    public static Weight fromGrams(BigDecimal grams) {
        return new Weight(grams, WeightUnit.GRAM);
    }

    public static Weight fromGrams(double grams) {
        return fromGrams(BigDecimal.valueOf(grams));
    }

    public static Weight fromKilograms(BigDecimal kg) {
        return new Weight(kg, WeightUnit.KILOGRAM);
    }

    public static Weight zero() {
        return new Weight(BigDecimal.ZERO, WeightUnit.GRAM);
    }

    public enum WeightUnit {
        GRAM("g"),
        KILOGRAM("kg"),
        OUNCE("oz"),
        POUND("lb");

        private final String symbol;

        WeightUnit(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }
}
```

**`/modules/grocery-pos/domain/src/main/java/tech/kayys/erp/groceries/domain/model/GroceryProduct.java`**:

```java
package tech.kayys.erp.groceries.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.groceries.domain.identifier.GroceryProductId;
import tech.kayys.erp.groceries.domain.valueobject.GroceryProductType;
import tech.kayys.erp.groceries.domain.valueobject.ShelfLife;
import tech.kayys.erp.groceries.domain.valueobject.Weight;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Grocery Product aggregate root.
 * Extends catalog Product with grocery-specific attributes.
 */
public final class GroceryProduct extends AggregateRoot<GroceryProductId> {
    
    private static final long serialVersionUID = 1L;
    
    private UUID catalogProductId; // Reference to base product
    private GroceryProductType productType;
    private boolean isWeightBased;
    private Weight defaultWeight;
    private Weight minWeight;
    private Weight maxWeight;
    private ShelfLife shelfLife;
    private boolean requiresTemperatureControl;
    private String temperatureRange;
    private String storageInstructions;
    private String handlingInstructions;
    private List<String> allergens;
    private boolean organic;
    private boolean glutenFree;
    private boolean vegan;
    private boolean kosher;
    private boolean halal;
    private String countryOfOrigin;
    private String supplierBatchNumber;
    private List<BatchLot> batchLots;
    private boolean active;

    private GroceryProduct(GroceryProductId id) {
        super(id);
        this.batchLots = new ArrayList<>();
        this.allergens = new ArrayList<>();
        this.active = true;
        this.isWeightBased = false;
    }

    private GroceryProduct() {
        super();
    }

    /**
     * Factory method to create a new grocery product.
     */
    public static GroceryProduct create(
            GroceryProductId id,
            UUID catalogProductId,
            GroceryProductType productType) {
        GroceryProduct product = new GroceryProduct(id);
        product.catalogProductId = catalogProductId;
        product.productType = productType;
        product.requiresTemperatureControl = productType.requiresTemperatureControl();
        return product;
    }

    /**
     * Adds a batch/lot to the product.
     */
    public void addBatchLot(BatchLot batchLot) {
        batchLots.add(batchLot);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes a batch/lot from the product.
     */
    public void removeBatchLot(String batchNumber) {
        batchLots.removeIf(lot -> lot.getBatchNumber().equals(batchNumber));
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds an allergen.
     */
    public void addAllergen(String allergen) {
        if (!allergens.contains(allergen)) {
            allergens.add(allergen);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes an allergen.
     */
    public void removeAllergen(String allergen) {
        allergens.remove(allergen);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if product is expired based on current date.
     */
    public boolean isExpired() {
        if (shelfLife == null) {
            return false;
        }
        return shelfLife.isExpired(Instant.now());
    }

    /**
     * Checks if product is expiring soon.
     */
    public boolean isExpiringSoon(int daysThreshold) {
        if (shelfLife == null) {
            return false;
        }
        return shelfLife.isExpiringSoon(daysThreshold);
    }

    // Getters and Setters
    public UUID getCatalogProductId() { return catalogProductId; }
    public GroceryProductType getProductType() { return productType; }
    public boolean isWeightBased() { return isWeightBased; }
    public Weight getDefaultWeight() { return defaultWeight; }
    public Weight getMinWeight() { return minWeight; }
    public Weight getMaxWeight() { return maxWeight; }
    public ShelfLife getShelfLife() { return shelfLife; }
    public boolean isRequiresTemperatureControl() { return requiresTemperatureControl; }
    public String getTemperatureRange() { return temperatureRange; }
    public String getStorageInstructions() { return storageInstructions; }
    public String getHandlingInstructions() { return handlingInstructions; }
    public List<String> getAllergens() { return Collections.unmodifiableList(allergens); }
    public boolean isOrganic() { return organic; }
    public boolean isGlutenFree() { return glutenFree; }
    public boolean isVegan() { return vegan; }
    public boolean isKosher() { return kosher; }
    public boolean isHalal() { return halal; }
    public String getCountryOfOrigin() { return countryOfOrigin; }
    public String getSupplierBatchNumber() { return supplierBatchNumber; }
    public List<BatchLot> getBatchLots() { return Collections.unmodifiableList(batchLots); }
    public boolean isActive() { return active; }

    public void setWeightBased(boolean weightBased) {
        isWeightBased = weightBased;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDefaultWeight(Weight defaultWeight) {
        this.defaultWeight = defaultWeight;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMinWeight(Weight minWeight) {
        this.minWeight = minWeight;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMaxWeight(Weight maxWeight) {
        this.maxWeight = maxWeight;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setShelfLife(ShelfLife shelfLife) {
        this.shelfLife = shelfLife;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTemperatureRange(String temperatureRange) {
        this.temperatureRange = temperatureRange;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setStorageInstructions(String storageInstructions) {
        this.storageInstructions = storageInstructions;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setHandlingInstructions(String handlingInstructions) {
        this.handlingInstructions = handlingInstructions;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setOrganic(boolean organic) {
        this.organic = organic;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setGlutenFree(boolean glutenFree) {
        this.glutenFree = glutenFree;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setVegan(boolean vegan) {
        this.vegan = vegan;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setKosher(boolean kosher) {
        this.kosher = kosher;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setHalal(boolean halal) {
        this.halal = halal;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setSupplierBatchNumber(String supplierBatchNumber) {
        this.supplierBatchNumber = supplierBatchNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setActive(boolean active) {
        this.active = active;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "GroceryProduct{" +
                "id=" + getId() +
                ", catalogProductId=" + catalogProductId +
                ", productType=" + productType +
                ", isWeightBased=" + isWeightBased +
                '}';
    }

    /**
     * Batch/Lot for perishable goods.
     */
    public static final class BatchLot implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String batchNumber;
        private final Instant productionDate;
        private final Instant expiryDate;
        private final int quantity;
        private final int remainingQuantity;
        private final String supplierName;
        private final String supplierLotNumber;

        public BatchLot(
                String batchNumber,
                Instant productionDate,
                Instant expiryDate,
                int quantity,
                int remainingQuantity,
                String supplierName,
                String supplierLotNumber) {
            this.batchNumber = batchNumber;
            this.productionDate = productionDate;
            this.expiryDate = expiryDate;
            this.quantity = quantity;
            this.remainingQuantity = remainingQuantity;
            this.supplierName = supplierName;
            this.supplierLotNumber = supplierLotNumber;
            validate();
        }

        @Override
        public void validate() {
            if (batchNumber == null || batchNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Batch number cannot be empty");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (remainingQuantity < 0 || remainingQuantity > quantity) {
                throw new IllegalArgumentException("Invalid remaining quantity");
            }
        }

        public String getBatchNumber() { return batchNumber; }
        public Instant getProductionDate() { return productionDate; }
        public Instant getExpiryDate() { return expiryDate; }
        public int getQuantity() { return quantity; }
        public int getRemainingQuantity() { return remainingQuantity; }
        public String getSupplierName() { return supplierName; }
        public String getSupplierLotNumber() { return supplierLotNumber; }

        public boolean isExpired() {
            return expiryDate != null && Instant.now().isAfter(expiryDate);
        }

        public boolean isExpiringSoon(int daysThreshold) {
            if (expiryDate == null) {
                return false;
            }
            Instant threshold = Instant.now().plusSeconds(daysThreshold * 24L * 60L * 60L);
            return expiryDate.isBefore(threshold);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BatchLot that = (BatchLot) o;
            return Objects.equals(batchNumber, that.batchNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(batchNumber);
        }

        @Override
        public String toString() {
            return "BatchLot{" +
                    "batchNumber='" + batchNumber + '\'' +
                    ", expiryDate=" + expiryDate +
                    ", remainingQuantity=" + remainingQuantity +
                    '}';
        }
    }
}
```

**`/modules/grocery-pos/domain/src/main/java/tech/kayys/erp/groceries/domain/identifier/GroceryProductId.java`**:

```java
package tech.kayys.erp.groceries.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class GroceryProductId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public GroceryProductId(UUID value) {
        super(value);
    }

    public static GroceryProductId of(UUID value) {
        return new GroceryProductId(value);
    }

    public static GroceryProductId generate() {
        return new GroceryProductId(UUID.randomUUID());
    }

    public static GroceryProductId fromString(String value) {
        return new GroceryProductId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "GroceryProductId{" + value + "}";
    }
}
```

**`/modules/grocery-pos/domain/src/main/java/tech/kayys/erp/groceries/domain/valueobject/ShelfLife.java`**:

```java
package tech.kayys.erp.groceries.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Shelf life value object for perishable goods.
 */
public final class ShelfLife implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final Instant productionDate;
    private final Instant expiryDate;
    private final int shelfLifeDays;

    public ShelfLife(Instant productionDate, Instant expiryDate) {
        this.productionDate = productionDate;
        this.expiryDate = expiryDate;
        this.shelfLifeDays = (int) ChronoUnit.DAYS.between(productionDate, expiryDate);
        validate();
    }

    @Override
    public void validate() {
        if (productionDate == null) {
            throw new IllegalArgumentException("Production date cannot be null");
        }
        if (expiryDate == null) {
            throw new IllegalArgumentException("Expiry date cannot be null");
        }
        if (expiryDate.isBefore(productionDate)) {
            throw new IllegalArgumentException("Expiry date must be after production date");
        }
        if (shelfLifeDays <= 0) {
            throw new IllegalArgumentException("Shelf life must be positive");
        }
    }

    public Instant getProductionDate() { return productionDate; }
    public Instant getExpiryDate() { return expiryDate; }
    public int getShelfLifeDays() { return shelfLifeDays; }

    public boolean isExpired(Instant currentDate) {
        return currentDate.isAfter(expiryDate);
    }

    public boolean isExpiringSoon(int daysThreshold) {
        Instant threshold = Instant.now().plusSeconds(daysThreshold * 24L * 60L * 60L);
        return expiryDate.isBefore(threshold);
    }

    public int getDaysRemaining() {
        return (int) ChronoUnit.DAYS.between(Instant.now(), expiryDate);
    }

    public int getDaysSinceProduction() {
        return (int) ChronoUnit.DAYS.between(productionDate, Instant.now());
    }

    public double getLifeUsedPercentage() {
        if (shelfLifeDays == 0) {
            return 0.0;
        }
        return (double) getDaysSinceProduction() / shelfLifeDays * 100.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShelfLife shelfLife = (ShelfLife) o;
        return Objects.equals(productionDate, shelfLife.productionDate) &&
               Objects.equals(expiryDate, shelfLife.expiryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productionDate, expiryDate);
    }

    @Override
    public String toString() {
        return "ShelfLife{" +
                "productionDate=" + productionDate +
                ", expiryDate=" + expiryDate +
                ", shelfLifeDays=" + shelfLifeDays +
                '}';
    }

    public static ShelfLife of(Instant productionDate, Instant expiryDate) {
        return new ShelfLife(productionDate, expiryDate);
    }

    public static ShelfLife of(Instant productionDate, int shelfLifeDays) {
        Instant expiryDate = productionDate.plusSeconds(shelfLifeDays * 24L * 60L * 60L);
        return new ShelfLife(productionDate, expiryDate);
    }
}
```

**`/modules/grocery-pos/domain/src/main/java/tech/kayys/erp/groceries/domain/model/ScaleDevice.java`**:

```java
package tech.kayys.erp.groceries.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.groceries.domain.identifier.ScaleId;
import tech.kayys.erp.groceries.domain.valueobject.Weight;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Scale device aggregate root.
 * Represents a physical scale device for weighing grocery items.
 */
public final class ScaleDevice extends AggregateRoot<ScaleId> {
    
    private static final long serialVersionUID = 1L;
    
    private String deviceName;
    private String model;
    private String serialNumber;
    private String manufacturer;
    private ScaleType scaleType; // DELI, PRODUCE, BAKERY, GENERAL
    private Weight maxWeight;
    private Weight minWeight;
    private Weight tareWeight;
    private double accuracyGrams;
    private boolean connected;
    private String ipAddress;
    private int port;
    private String connectionStatus; // ONLINE, OFFLINE, ERROR
    private List<ScaleTransaction> transactions;
    private boolean active;

    private ScaleDevice(ScaleId id) {
        super(id);
        this.transactions = new ArrayList<>();
        this.active = true;
        this.connected = false;
        this.connectionStatus = "OFFLINE";
    }

    private ScaleDevice() {
        super();
    }

    /**
     * Factory method to create a new scale device.
     */
    public static ScaleDevice create(
            ScaleId id,
            String deviceName,
            String model,
            ScaleType scaleType,
            Weight maxWeight) {
        ScaleDevice device = new ScaleDevice(id);
        device.deviceName = deviceName;
        device.model = model;
        device.scaleType = scaleType;
        device.maxWeight = maxWeight;
        device.tareWeight = Weight.zero();
        return device;
    }

    /**
     * Connects the scale to the system.
     */
    public void connect(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.connected = true;
        this.connectionStatus = "ONLINE";
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Disconnects the scale.
     */
    public void disconnect() {
        this.connected = false;
        this.connectionStatus = "OFFLINE";
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the scale to error state.
     */
    public void setError(String errorMessage) {
        this.connectionStatus = "ERROR";
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records a transaction from the scale.
     */
    public void recordTransaction(ScaleTransaction transaction) {
        transactions.add(transaction);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Validates if a weight is within the scale's range.
     */
    public boolean isValidWeight(Weight weight) {
        if (weight.isZero()) {
            return false;
        }
        if (minWeight != null && weight.isLessThan(minWeight)) {
            return false;
        }
        if (maxWeight != null && weight.isGreaterThan(maxWeight)) {
            return false;
        }
        return true;
    }

    /**
     * Calculates the net weight (gross - tare).
     */
    public Weight getNetWeight(Weight grossWeight) {
        if (!isValidWeight(grossWeight)) {
            throw new IllegalArgumentException("Invalid weight for scale");
        }
        return grossWeight.subtract(tareWeight);
    }

    // Getters and Setters
    public String getDeviceName() { return deviceName; }
    public String getModel() { return model; }
    public String getSerialNumber() { return serialNumber; }
    public String getManufacturer() { return manufacturer; }
    public ScaleType getScaleType() { return scaleType; }
    public Weight getMaxWeight() { return maxWeight; }
    public Weight getMinWeight() { return minWeight; }
    public Weight getTareWeight() { return tareWeight; }
    public double getAccuracyGrams() { return accuracyGrams; }
    public boolean isConnected() { return connected; }
    public String getIpAddress() { return ipAddress; }
    public int getPort() { return port; }
    public String getConnectionStatus() { return connectionStatus; }
    public List<ScaleTransaction> getTransactions() { return Collections.unmodifiableList(transactions); }
    public boolean isActive() { return active; }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMinWeight(Weight minWeight) {
        this.minWeight = minWeight;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTareWeight(Weight tareWeight) {
        this.tareWeight = tareWeight;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setAccuracyGrams(double accuracyGrams) {
        this.accuracyGrams = accuracyGrams;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setActive(boolean active) {
        this.active = active;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "ScaleDevice{" +
                "id=" + getId() +
                ", deviceName='" + deviceName + '\'' +
                ", scaleType=" + scaleType +
                ", connected=" + connected +
                '}';
    }

    /**
     * Scale type enum.
     */
    public enum ScaleType {
        DELI("Deli Scale"),
        PRODUCE("Produce Scale"),
        BAKERY("Bakery Scale"),
        GENERAL("General Scale"),
        BULK("Bulk Scale");

        private final String displayName;

        ScaleType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Scale transaction record.
     */
    public static final class ScaleTransaction implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String productId;
        private final Weight grossWeight;
        private final Weight netWeight;
        private final Instant timestamp;
        private final String operatorId;
        private final String transactionId;

        public ScaleTransaction(
                String productId,
                Weight grossWeight,
                Weight netWeight,
                Instant timestamp,
                String operatorId,
                String transactionId) {
            this.productId = productId;
            this.grossWeight = grossWeight;
            this.netWeight = netWeight;
            this.timestamp = timestamp;
            this.operatorId = operatorId;
            this.transactionId = transactionId;
            validate();
        }

        @Override
        public void validate() {
            if (productId == null || productId.trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be empty");
            }
            if (grossWeight == null) {
                throw new IllegalArgumentException("Gross weight cannot be null");
            }
            if (netWeight == null || netWeight.isZero()) {
                throw new IllegalArgumentException("Net weight must be positive");
            }
            if (timestamp == null) {
                throw new IllegalArgumentException("Timestamp cannot be null");
            }
        }

        public String getProductId() { return productId; }
        public Weight getGrossWeight() { return grossWeight; }
        public Weight getNetWeight() { return netWeight; }
        public Instant getTimestamp() { return timestamp; }
        public String getOperatorId() { return operatorId; }
        public String getTransactionId() { return transactionId; }

        @Override
        public String toString() {
            return "ScaleTransaction{" +
                    "productId='" + productId + '\'' +
                    ", netWeight=" + netWeight +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}
```

## 2. Grocery POS Application Module

**`/modules/grocery-pos/application/pom.xml`**:

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

    <artifactId>erp-grocery-pos-application</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-grocery-pos-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-application</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-sales-application</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-inventory-application</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-pricing-application</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

**`/modules/grocery-pos/application/src/main/java/tech/kayys/erp/groceries/application/api/GroceryPosService.java`**:

```java
package tech.kayys.erp.groceries.application.api;

import tech.kayys.erp.groceries.application.api.command.*;
import tech.kayys.erp.groceries.application.api.query.*;
import tech.kayys.erp.groceries.domain.identifier.ScaleId;
import tech.kayys.erp.groceries.domain.identifier.GroceryProductId;

import java.util.concurrent.CompletionStage;

/**
 * Public API for grocery POS operations.
 */
public interface GroceryPosService {

    // ============ Product Operations ============

    /**
     * Registers a grocery product in the system.
     */
    CompletionStage<GroceryProductId> registerGroceryProduct(RegisterGroceryProductCommand command);

    /**
     * Adds a batch/lot to a product.
     */
    CompletionStage<GroceryProductId> addBatchLot(AddBatchLotCommand command);

    /**
     * Updates product shelf life.
     */
    CompletionStage<GroceryProductId> updateShelfLife(UpdateShelfLifeCommand command);

    // ============ Scale Operations ============

    /**
     * Registers a scale device.
     */
    CompletionStage<ScaleId> registerScale(RegisterScaleCommand command);

    /**
     * Connects a scale device.
     */
    CompletionStage<ScaleId> connectScale(ConnectScaleCommand command);

    /**
     * Reads a weight from the scale.
     */
    CompletionStage<WeightReadResult> readWeight(ReadWeightCommand command);

    /**
     * Tares the scale.
     */
    CompletionStage<ScaleId> tareScale(TareScaleCommand command);

    // ============ Checkout Operations ============

    /**
     * Adds a weighted item to the cart.
     */
    CompletionStage<CartItemResult> addWeightedItemToCart(AddWeightedItemCommand command);

    /**
     * Completes a grocery POS transaction.
     */
    CompletionStage<GroceryReceipt> completeGroceryTransaction(CompleteGroceryTransactionCommand command);

    // ============ Expiry Management ============

    /**
     * Gets products expiring soon.
     */
    CompletionStage<ExpiryListResult> getProductsExpiringSoon(GetExpiringProductsQuery query);

    /**
     * Marks products as expired.
     */
    CompletionStage<Void> markProductsExpired(MarkExpiredProductsCommand command);

    /**
     * Processes waste/write-off for expired products.
     */
    CompletionStage<Void> processWaste(ProcessWasteCommand command);
}
```

**`/modules/grocery-pos/application/src/main/java/tech/kayys/erp/groceries/application/api/command/RegisterGroceryProductCommand.java`**:

```java
package tech.kayys.erp.groceries.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.groceries.domain.identifier.GroceryProductId;
import tech.kayys.erp.groceries.domain.valueobject.GroceryProductType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Command to register a grocery product.
 */
public record RegisterGroceryProductCommand(
        GroceryProductId groceryProductId,
        UUID catalogProductId,
        GroceryProductType productType,
        boolean isWeightBased,
        BigDecimal defaultWeightKg,
        BigDecimal minWeightKg,
        BigDecimal maxWeightKg,
        Instant productionDate,
        Instant expiryDate,
        int shelfLifeDays,
        String temperatureRange,
        String storageInstructions,
        String handlingInstructions,
        List<String> allergens,
        boolean organic,
        boolean glutenFree,
        boolean vegan,
        boolean kosher,
        boolean halal,
        String countryOfOrigin,
        String supplierBatchNumber
) implements Command<GroceryProductId> {

    public RegisterGroceryProductCommand {
        if (catalogProductId == null) {
            throw new IllegalArgumentException("Catalog product ID cannot be null");
        }
        if (productType == null) {
            throw new IllegalArgumentException("Product type is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private GroceryProductId groceryProductId;
        private UUID catalogProductId;
        private GroceryProductType productType;
        private boolean isWeightBased = false;
        private BigDecimal defaultWeightKg;
        private BigDecimal minWeightKg;
        private BigDecimal maxWeightKg;
        private Instant productionDate;
        private Instant expiryDate;
        private int shelfLifeDays;
        private String temperatureRange;
        private String storageInstructions;
        private String handlingInstructions;
        private List<String> allergens;
        private boolean organic = false;
        private boolean glutenFree = false;
        private boolean vegan = false;
        private boolean kosher = false;
        private boolean halal = false;
        private String countryOfOrigin;
        private String supplierBatchNumber;

        public Builder groceryProductId(GroceryProductId groceryProductId) {
            this.groceryProductId = groceryProductId;
            return this;
        }

        public Builder catalogProductId(UUID catalogProductId) {
            this.catalogProductId = catalogProductId;
            return this;
        }

        public Builder productType(GroceryProductType productType) {
            this.productType = productType;
            return this;
        }

        public Builder isWeightBased(boolean isWeightBased) {
            this.isWeightBased = isWeightBased;
            return this;
        }

        public Builder defaultWeightKg(BigDecimal defaultWeightKg) {
            this.defaultWeightKg = defaultWeightKg;
            return this;
        }

        public Builder minWeightKg(BigDecimal minWeightKg) {
            this.minWeightKg = minWeightKg;
            return this;
        }

        public Builder maxWeightKg(BigDecimal maxWeightKg) {
            this.maxWeightKg = maxWeightKg;
            return this;
        }

        public Builder productionDate(Instant productionDate) {
            this.productionDate = productionDate;
            return this;
        }

        public Builder expiryDate(Instant expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder shelfLifeDays(int shelfLifeDays) {
            this.shelfLifeDays = shelfLifeDays;
            return this;
        }

        public Builder temperatureRange(String temperatureRange) {
            this.temperatureRange = temperatureRange;
            return this;
        }

        public Builder storageInstructions(String storageInstructions) {
            this.storageInstructions = storageInstructions;
            return this;
        }

        public Builder handlingInstructions(String handlingInstructions) {
            this.handlingInstructions = handlingInstructions;
            return this;
        }

        public Builder allergens(List<String> allergens) {
            this.allergens = allergens;
            return this;
        }

        public Builder organic(boolean organic) {
            this.organic = organic;
            return this;
        }

        public Builder glutenFree(boolean glutenFree) {
            this.glutenFree = glutenFree;
            return this;
        }

        public Builder vegan(boolean vegan) {
            this.vegan = vegan;
            return this;
        }

        public Builder kosher(boolean kosher) {
            this.kosher = kosher;
            return this;
        }

        public Builder halal(boolean halal) {
            this.halal = halal;
            return this;
        }

        public Builder countryOfOrigin(String countryOfOrigin) {
            this.countryOfOrigin = countryOfOrigin;
            return this;
        }

        public Builder supplierBatchNumber(String supplierBatchNumber) {
            this.supplierBatchNumber = supplierBatchNumber;
            return this;
        }

        public RegisterGroceryProductCommand build() {
            if (groceryProductId == null) {
                groceryProductId = GroceryProductId.generate();
            }
            return new RegisterGroceryProductCommand(
                groceryProductId, catalogProductId, productType, isWeightBased,
                defaultWeightKg, minWeightKg, maxWeightKg,
                productionDate, expiryDate, shelfLifeDays,
                temperatureRange, storageInstructions, handlingInstructions,
                allergens, organic, glutenFree, vegan, kosher, halal,
                countryOfOrigin, supplierBatchNumber
            );
        }
    }
}
```

**`/modules/grocery-pos/application/src/main/java/tech/kayys/erp/groceries/application/api/command/AddBatchLotCommand.java`**:

```java
package tech.kayys.erp.groceries.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.groceries.domain.identifier.GroceryProductId;

import java.time.Instant;

/**
 * Command to add a batch/lot to a grocery product.
 */
public record AddBatchLotCommand(
        GroceryProductId groceryProductId,
        String batchNumber,
        Instant productionDate,
        Instant expiryDate,
        int quantity,
        String supplierName,
        String supplierLotNumber
) implements Command<GroceryProductId> {

    public AddBatchLotCommand {
        if (groceryProductId == null) {
            throw new IllegalArgumentException("Grocery product ID cannot be null");
        }
        if (batchNumber == null || batchNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Batch number cannot be empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private GroceryProductId groceryProductId;
        private String batchNumber;
        private Instant productionDate;
        private Instant expiryDate;
        private int quantity;
        private String supplierName;
        private String supplierLotNumber;

        public Builder groceryProductId(GroceryProductId groceryProductId) {
            this.groceryProductId = groceryProductId;
            return this;
        }

        public Builder batchNumber(String batchNumber) {
            this.batchNumber = batchNumber;
            return this;
        }

        public Builder productionDate(Instant productionDate) {
            this.productionDate = productionDate;
            return this;
        }

        public Builder expiryDate(Instant expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder supplierName(String supplierName) {
            this.supplierName = supplierName;
            return this;
        }

        public Builder supplierLotNumber(String supplierLotNumber) {
            this.supplierLotNumber = supplierLotNumber;
            return this;
        }

        public AddBatchLotCommand build() {
            if (productionDate == null) {
                productionDate = Instant.now();
            }
            return new AddBatchLotCommand(
                groceryProductId, batchNumber, productionDate,
                expiryDate, quantity, supplierName, supplierLotNumber
            );
        }
    }
}
```

**`/modules/grocery-pos/application/src/main/java/tech/kayys/erp/groceries/application/internal/AddWeightedItemToCartHandler.java`**:

```java
package tech.kayys.erp.groceries.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.groceries.application.api.command.AddWeightedItemCommand;
import tech.kayys.erp.groceries.application.api.CartItemResult;
import tech.kayys.erp.groceries.domain.model.GroceryProduct;
import tech.kayys.erp.groceries.domain.model.ScaleDevice;
import tech.kayys.erp.groceries.domain.repository.GroceryProductRepository;
import tech.kayys.erp.groceries.domain.repository.ScaleDeviceRepository;
import tech.kayys.erp.groceries.domain.valueobject.Weight;
import tech.kayys.erp.sales.domain.model.Cart;
import tech.kayys.erp.sales.domain.repository.CartRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for adding weighted items to the cart.
 */
@UseCase("Add a weighted grocery item to the cart")
public class AddWeightedItemToCartHandler
        implements CommandHandler<AddWeightedItemCommand, CartItemResult> {

    private final GroceryProductRepository groceryProductRepository;
    private final ScaleDeviceRepository scaleDeviceRepository;
    private final CartRepository cartRepository;

    @Inject
    public AddWeightedItemToCartHandler(
            GroceryProductRepository groceryProductRepository,
            ScaleDeviceRepository scaleDeviceRepository,
            CartRepository cartRepository) {
        this.groceryProductRepository = groceryProductRepository;
        this.scaleDeviceRepository = scaleDeviceRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public CompletionStage<CartItemResult> handle(AddWeightedItemCommand command) {
        // 1. Get the grocery product
        return groceryProductRepository.findById(command.groceryProductId())
            .thenCompose(productOpt -> {
                if (productOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Grocery product not found: " + command.groceryProductId())
                    );
                }

                GroceryProduct product = productOpt.get();

                // 2. Validate product is weight-based
                if (!product.isWeightBased()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Product is not weight-based")
                    );
                }

                // 3. Get the scale device
                return scaleDeviceRepository.findById(command.scaleId())
                    .thenCompose(scaleOpt -> {
                        if (scaleOpt.isEmpty()) {
                            return CompletableFuture.failedFuture(
                                new IllegalArgumentException("Scale not found: " + command.scaleId())
                            );
                        }

                        ScaleDevice scale = scaleOpt.get();

                        // 4. Validate scale is connected
                        if (!scale.isConnected()) {
                            return CompletableFuture.failedFuture(
                                new IllegalStateException("Scale is not connected")
                            );
                        }

                        // 5. Get the weight from the scale
                        // In real implementation, this would read from the physical scale
                        Weight weight = command.weight() != null ?
                            command.weight() : Weight.zero();

                        // 6. Validate weight is within range
                        if (!scale.isValidWeight(weight)) {
                            return CompletableFuture.failedFuture(
                                new IllegalArgumentException("Weight is outside valid range")
                            );
                        }

                        // 7. Calculate net weight (gross - tare)
                        Weight netWeight = scale.getNetWeight(weight);

                        // 8. Get the cart
                        return cartRepository.findById(command.cartId())
                            .thenCompose(cartOpt -> {
                                if (cartOpt.isEmpty()) {
                                    return CompletableFuture.failedFuture(
                                        new IllegalArgumentException("Cart not found: " + command.cartId())
                                    );
                                }

                                Cart cart = cartOpt.get();

                                // 9. Get product price
                                // This would call the Pricing context

                                // 10. Add item to cart with weight
                                // Calculate price based on weight and unit price

                                // 11. Save the cart
                                return cartRepository.save(cart)
                                    .thenApply(v -> new CartItemResult(
                                        command.cartId().toString(),
                                        product.getCatalogProductId().toString(),
                                        product.getProductType().name(),
                                        netWeight.getValue().doubleValue(),
                                        netWeight.getUnit().getSymbol(),
                                        0.0, // price
                                        0.0, // total
                                        true
                                    ));
                            });
                    });
            });
    }
}
```

## 3. Update Root POM

**Update `/pom.xml`** to include Grocery POS modules:

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

    <module>modules/inventory/domain</module>
    <module>modules/inventory/application</module>
    <module>modules/inventory/infrastructure</module>
    <module>modules/inventory/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/grocery-pos/domain</module>
    <module>modules/grocery-pos/application</module>
    <module>modules/grocery-pos/infrastructure</module>
    <module>modules/grocery-pos/interfaces</module>
</modules>
```

## Summary

The Grocery POS bounded context extends the existing ERP architecture with:

1. **Specialized Product Types**: AMBIENT, CHILLED, FROZEN, FRESH, DELI, BAKERY, PRODUCE, MEAT, SEAFOOD, DAIRY, NON_FOOD, BEVERAGE
2. **Weight-Based Pricing**: Support for scale integration and weight-based pricing for perishable goods
3. **Batch/Lot Tracking**: Track production dates, expiry dates, and supplier information for fresh goods
4. **Scale Device Management**: Register, connect, and manage physical scale devices
5. **Shelf Life Management**: Track shelf life, expiration dates, and auto-waste processing
6. **Self-Checkout Support**: Integration with self-checkout kiosks 
7. **Fresh & Deli Integration**: Scale integration with Avery Berkel, Mettler Toledo, Bizerba scales 
8. **Shrinkage Tracking**: Automated waste write-off workflows 
9. **Promotion Engine**: Multi-buy, mix-and-match, BOGO, time-of-day promotions 

The module integrates with the existing Sales, Inventory, Pricing, and Catalog contexts, providing a unified platform for grocery operations .



# Grocery POS vs Core Product Modules: Relationship & Differences

Excellent question! Let me clarify how the Grocery POS module relates to and differs from the existing core Product modules in the ERP architecture.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          CORE ERP ARCHITECTURE                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────────────┐ │
│  │   Foundation    │    │     Company     │    │     Accounting           │ │
│  │   (Domain)      │    │    (Context)    │    │      (Context)           │ │
│  └─────────────────┘    └─────────────────┘    └─────────────────────────┘ │
│                                                                             │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────────────┐ │
│  │    Catalog      │    │     Sales       │    │     Inventory           │ │
│  │   (Context)     │◄──►│    (Context)    │◄──►│      (Context)           │ │
│  └─────────────────┘    └─────────────────┘    └─────────────────────────┘ │
│           │                      │                        │                 │
│           ▼                      ▼                        ▼                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │                    GROCERY POS (Context)                                ││
│  │                    ┌─────────────────────────────────────┐              ││
│  │                    │   GroceryProduct (extends Product)  │              ││
│  │                    │   ScaleDevice (new)                 │              ││
│  │                    │   ShelfLife (new)                   │              ││
│  │                    │   BatchLot (new)                    │              ││
│  │                    │   GroceryCart (extends Cart)        │              ││
│  │                    └─────────────────────────────────────┘              ││
│  └─────────────────────────────────────────────────────────────────────────┘│
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 1. Relationship to Core Product Modules

### 1.1 Catalog Context Relationship

```java
// Core Catalog Product
public final class Product extends AggregateRoot<ProductId> {
    private String name;
    private String description;
    private Money price;
    private String sku;
    private ProductStatus status;
    // ... basic product attributes
}

// Grocery POS Extends Catalog Product
public final class GroceryProduct extends AggregateRoot<GroceryProductId> {
    private UUID catalogProductId; // ← Links to core Product
    private GroceryProductType productType; // ← Grocery-specific
    private boolean isWeightBased; // ← Grocery-specific
    private Weight defaultWeight; // ← Grocery-specific
    private ShelfLife shelfLife; // ← Grocery-specific
    private List<BatchLot> batchLots; // ← Grocery-specific
    // ... grocery-specific attributes
}
```

**Relationship Type**: **Extension/Enrichment**

| Aspect | Core Catalog Product | Grocery Product |
|--------|---------------------|-----------------|
| **Purpose** | Base product definition | Enhanced product for grocery |
| **Lifecycle** | General product lifecycle | Adds fresh/perishable lifecycle |
| **Pricing** | Fixed price | Fixed + Weight-based pricing |
| **Inventory** | Simple stock tracking | Batch/Lot tracking with expiry |
| **Uniqueness** | Single record | Multiple batches per product |

### 1.2 Inventory Context Relationship

```java
// Core Inventory Context
public interface InventoryService {
    CompletionStage<StockLevel> checkStock(ProductId productId);
    CompletionStage<Void> adjustStock(AdjustStockCommand command);
}

// Grocery POS uses Inventory with enhancements
public interface GroceryInventoryService {
    // Tracks inventory by batch/lot
    CompletionStage<BatchStock> getBatchStock(String batchNumber);
    
    // Expiry-aware inventory
    CompletionStage<List<ExpiringBatch>> getExpiringBatches(int daysThreshold);
    
    // Waste management
    CompletionStage<Void> processWaste(ProcessWasteCommand command);
}
```

**Relationship Type**: **Specialization**

| Aspect | Core Inventory | Grocery Inventory |
|--------|---------------|-------------------|
| **Tracking** | Product-level | Batch/Lot-level |
| **Expiry** | Not tracked | Mandatory for perishables |
| **Waste** | Standard write-off | Proactive expiry management |
| **Shelf Life** | Not applicable | Critical feature |

### 1.3 Sales Context Relationship

```java
// Core Sales Cart
public final class Cart extends AggregateRoot<CartId> {
    private List<CartItem> items;
    private Money total;
    // ... standard cart operations
}

// Grocery Cart extends Core Cart
public final class GroceryCart extends Cart {
    private List<WeightedCartItem> weightedItems; // ← Grocery-specific
    private ScaleId currentScaleId; // ← Grocery-specific
    private boolean isWeightBased; // ← Grocery-specific
}
```

**Relationship Type**: **Extension**

| Aspect | Core Sales | Grocery Sales |
|--------|-----------|---------------|
| **Items** | Quantity-based | Quantity + Weight-based |
| **Checkout** | Standard checkout | Weight verification required |
| **Scale Integration** | Not applicable | Critical feature |
| **Pricing** | Fixed prices | Fixed + Weight-based pricing |

## 2. Key Differences

### 2.1 Product Type Hierarchy

```
┌─────────────────────────────────────────────────────────────────┐
│                        Core Product                             │
│                    (Foundation Catalog)                         │
│                                                                 │
│   Properties:                                                   │
│   - name, sku, description                                      │
│   - base price, status                                          │
│   - categories, tags                                            │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                      GroceryProduct                             │
│                   (Grocery POS Context)                         │
│                                                                 │
│   Extends: Catalog Product                                     │
│                                                                 │
│   Adds:                                                         │
│   - GroceryProductType (AMBIENT, CHILLED, FRESH, etc.)         │
│   - Weight-based pricing support                                │
│   - Shelf life management                                       │
│   - Batch/Lot tracking                                          │
│   - Temperature requirements                                    │
│   - Allergen information                                        │
│   - Organic/Kosher/Halal flags                                  │
└─────────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Specific Product Types                        │
│                                                                 │
│   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│   │   Produce   │  │    Deli     │  │   Bakery    │          │
│   │             │  │             │  │             │          │
│   │ - Weight    │  │ - Weight    │  │ - Best by   │          │
│   │ - Organic   │  │ - Pre-pack  │  │ - Freshness │          │
│   │ - Freshness │  │ - Sliced    │  │ - Custom    │          │
│   └─────────────┘  └─────────────┘  └─────────────┘          │
│                                                                 │
│   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│   │    Meat     │  │   Seafood   │  │   Dairy     │          │
│   │             │  │             │  │             │          │
│   │ - Grade     │  │ - Catch     │  │ - Pasteur   │          │
│   │ - Cut       │  │ - Origin    │  │ - Fat %     │          │
│   │ - Aged      │  │ - Sustain   │  │ - Culture   │          │
│   └─────────────┘  └─────────────┘  └─────────────┘          │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 Data Model Comparison

| Feature | Core Catalog | Grocery POS | Why Different |
|---------|-------------|-------------|---------------|
| **Identity** | ProductId (UUID) | GroceryProductId + catalogProductId | Need to maintain link to base product |
| **Pricing** | Fixed price | Fixed + Weight-based | Fresh goods sold by weight |
| **Inventory** | Quantity only | Batch/Lot + Quantity | Perishable tracking |
| **Expiry** | None | Expiry date, production date, shelf life | Critical for fresh goods |
| **Storage** | Simple attributes | Temperature range, storage instructions | Food safety compliance |
| **Labels** | Product name, price | Allergens, nutritional info, certifications | Regulatory requirements |
| **Supply Chain** | One supplier per product | Multiple batches per product, supplier tracking | Fresh goods sourced frequently |

### 2.3 Business Process Differences

| Process | Core Catalog | Grocery POS |
|---------|-------------|-------------|
| **Product Creation** | One-time creation | Continuous with batch/lot tracking |
| **Receiving** | Receive new stock | Create new batch/lot with expiry |
| **Sales** | Deduct from inventory | Deduct from specific batch/lot |
| **Returns** | Return to inventory | Cannot return expired goods |
| **Waste** | Write-off | Proactive waste management with expiry alerts |
| **Pricing Updates** | Manual or scheduled | Dynamic based on expiry date |
| **Promotions** | Standard promotions | Perishable-specific (time-of-day, bulk) |
| **Reporting** | Sales reports | Sales + Shrinkage + Waste reports |

## 3. Integration Points

### 3.1 How Grocery POS Uses Core Modules

```java
// Example: Grocery POS checkout flow using core modules
public class GroceryCheckoutService {
    
    private final SalesCommandService salesService;      // Core Sales
    private final InventoryService inventoryService;     // Core Inventory
    private final PricingService pricingService;         // Core Pricing
    private final GroceryProductRepository groceryRepo;  // Grocery POS
    
    public CompletionStage<Receipt> checkout(Cart cart) {
        // 1. Validate grocery items with fresh-specific rules
        return validateFreshItems(cart)
            .thenCompose(valid -> {
                if (!valid) {
                    return CompletableFuture.failedFuture(
                        new IllegalStateException("Expired or invalid fresh items")
                    );
                }
                
                // 2. Get pricing from core pricing (with grocery adjustments)
                return pricingService.calculatePrice(cart)
                    .thenCompose(pricing -> {
                        // 3. Process through core sales
                        return salesService.createOrder(cart)
                            .thenCompose(order -> {
                                // 4. Update inventory (core + grocery-specific)
                                return inventoryService.adjustStock(order)
                                    .thenCompose(v -> 
                                        updateGroceryInventory(order)
                                    )
                                    .thenApply(v -> {
                                        // 5. Generate receipt
                                        return generateReceipt(order);
                                    });
                            });
                    });
            });
    }
    
    private CompletionStage<Void> updateGroceryInventory(Order order) {
        // Deduct from specific batches/lots
        for (OrderItem item : order.getItems()) {
            if (item.isWeighted()) {
                // Deduct from specific batch/lot
                return groceryRepo.deductFromBatch(
                    item.getBatchNumber(),
                    item.getQuantity()
                );
            }
        }
        return CompletableFuture.completedFuture(null);
    }
}
```

### 3.2 Event Integration

```java
// Core Catalog Events
public class ProductCreated implements DomainEvent { ... }
public class ProductUpdated implements DomainEvent { ... }

// Grocery POS Events
public class BatchAdded implements DomainEvent { ... }      // Grocery-specific
public class BatchExpired implements DomainEvent { ... }    // Grocery-specific
public class ShelfLifeUpdated implements DomainEvent { ... } // Grocery-specific
public class WasteProcessed implements DomainEvent { ... }   // Grocery-specific
```

## 4. When to Use Which

### Use Core Catalog Product When:
- Selling standard products with fixed quantities
- Product doesn't expire
- No weight-based pricing needed
- Simple inventory tracking sufficient
- Standard packaging

### Use Grocery Product When:
- Selling fresh/perishable goods (produce, meat, deli)
- Weight-based pricing required
- Expiry dates need tracking
- Batch/lot tracking required
- Temperature control necessary
- Food safety compliance needed
- Supplier batch tracking required

## 5. Complete Architecture View

```
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                              WHOLE ERP ARCHITECTURE                                  │
├──────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐│
│  │                              FOUNDATION LAYER                                    ││
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────────────────┐ ││
│  │  │ Domain   │  │Application│  │ Reactive │  │ Company  │  │  Accounting        │ ││
│  │  └──────────┘  └──────────┘  └──────────┘  └──────────┘  └────────────────────┘ ││
│  └──────────────────────────────────────────────────────────────────────────────────┘│
│                                          │                                          │
│                                          ▼                                          │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐│
│  │                            CORE BUSINESS LAYER                                   ││
│  │                                                                                  ││
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        ││
│  │  │ Catalog  │  │  Sales   │  │Inventory │  │ Pricing  │  │  CRM     │        ││
│  │  │ (Core)   │  │  (Core)  │  │  (Core)  │  │  (Core)  │  │  (Core)  │        ││
│  │  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  └──────────┘        ││
│  │       │             │             │             │                               ││
│  └───────┼─────────────┼─────────────┼─────────────┼──────────────────────────────┘│
│          │             │             │             │                                │
│          ▼             ▼             ▼             ▼                                │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐│
│  │                         GROCERY POS LAYER                                       ││
│  │                                                                                  ││
│  │  ┌────────────────────────────────────────────────────────────────────────────┐ ││
│  │  │                                                                             │ ││
│  │  │  ┌────────────────────────────────────────────────────────────────────┐    │ ││
│  │  │  │                     Grocery Product Management                     │    │ ││
│  │  │  │  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐  │    │ ││
│  │  │  │  │  Register  │  │  Batch/Lot │  │  Expiry    │  │  Shelf     │  │    │ ││
│  │  │  │  │  Product   │  │  Tracking  │  │  Manager   │  │  Life      │  │    │ ││
│  │  │  │  └────────────┘  └────────────┘  └────────────┘  └────────────┘  │    │ ││
│  │  │  └────────────────────────────────────────────────────────────────────┘    │ ││
│  │  │                                                                             │ ││
│  │  │  ┌────────────────────────────────────────────────────────────────────┐    │ ││
│  │  │  │                     Scale & Weighing Integration                   │    │ ││
│  │  │  │  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐  │    │ ││
│  │  │  │  │   Scale    │  │   Tare     │  │   Weight   │  │   Print    │  │    │ ││
│  │  │  │  │  Manager   │  │  Manager   │  │  Validator │  │  Labels    │  │    │ ││
│  │  │  │  └────────────┘  └────────────┘  └────────────┘  └────────────┘  │    │ ││
│  │  │  └────────────────────────────────────────────────────────────────────┘    │ ││
│  │  │                                                                             │ ││
│  │  │  ┌────────────────────────────────────────────────────────────────────┐    │ ││
│  │  │  │                      Checkout & POS Operations                     │    │ ││
│  │  │  │  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐  │    │ ││
│  │  │  │  │  Self-     │  │  Deli/      │  │  Fresh     │  │  Receipt   │  │    │ ││
│  │  │  │  │  Checkout  │  │  Scale     │  │  Item      │  │  Print     │  │    │ ││
│  │  │  │  └────────────┘  └────────────┘  └────────────┘  └────────────┘  │    │ ││
│  │  │  └────────────────────────────────────────────────────────────────────┘    │ ││
│  │  └────────────────────────────────────────────────────────────────────────────┘ ││
│  └──────────────────────────────────────────────────────────────────────────────────┘│
│                                                                                      │
└──────────────────────────────────────────────────────────────────────────────────────┘
```

## Summary

| Aspect | Core Product Modules | Grocery POS Module |
|--------|---------------------|-------------------|
| **Purpose** | Universal product management | Grocery-specific product management |
| **Relationship** | Foundation | Extension/Specialization |
| **Dependency** | None (foundation) | Depends on Catalog, Sales, Inventory, Pricing |
| **Data Model** | Simple | Complex (batch, expiry, weight) |
| **Processes** | Standard | Fresh goods-specific |
| **Integration** | Internal | External (scales, labels) |
| **Regulations** | General | Food safety, labeling compliance |
| **Use Cases** | All retail | Grocery stores, supermarkets |

**Key Takeaway**: Grocery POS doesn't replace core Product modules—it extends and specializes them. The core modules handle generic product management, while Grocery POS adds grocery-specific capabilities like weight-based pricing, batch/lot tracking, shelf life management, and scale integration.
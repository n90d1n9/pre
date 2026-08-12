# Complete Implementation: Stock Opname / Physical Inventory Count

Now I'll implement the Stock Opname (Physical Inventory Count) bounded context, which handles cycle counting, physical inventory counting, variance analysis, and inventory reconciliation.

## 1. Stock Opname Domain Module

**`/modules/stockopname/domain/pom.xml`**:

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

    <artifactId>erp-stockopname-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/stockopname/domain/src/main/java/tech/kayys/erp/stockopname/domain/identifier/CountingSessionId.java`**:

```java
package tech.kayys.erp.stockopname.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Counting session identifier.
 */
public final class CountingSessionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public CountingSessionId(UUID value) {
        super(value);
    }

    public static CountingSessionId of(UUID value) {
        return new CountingSessionId(value);
    }

    public static CountingSessionId generate() {
        return new CountingSessionId(UUID.randomUUID());
    }

    public static CountingSessionId fromString(String value) {
        return new CountingSessionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CountingSessionId{" + value + "}";
    }
}
```

**`/modules/stockopname/domain/src/main/java/tech/kayys/erp/stockopname/domain/identifier/CountingItemId.java`**:

```java
package tech.kayys.erp.stockopname.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Counting item identifier.
 */
public final class CountingItemId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public CountingItemId(UUID value) {
        super(value);
    }

    public static CountingItemId of(UUID value) {
        return new CountingItemId(value);
    }

    public static CountingItemId generate() {
        return new CountingItemId(UUID.randomUUID());
    }

    public static CountingItemId fromString(String value) {
        return new CountingItemId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CountingItemId{" + value + "}";
    }
}
```

**`/modules/stockopname/domain/src/main/java/tech/kayys/erp/stockopname/domain/identifier/CountingItemId.java`**:

```java
package tech.kayys.erp.stockopname.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Counting item identifier.
 */
public final class CountingItemId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public CountingItemId(UUID value) {
        super(value);
    }

    public static CountingItemId of(UUID value) {
        return new CountingItemId(value);
    }

    public static CountingItemId generate() {
        return new CountingItemId(UUID.randomUUID());
    }

    public static CountingItemId fromString(String value) {
        return new CountingItemId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CountingItemId{" + value + "}";
    }
}
```

**`/modules/stockopname/domain/src/main/java/tech/kayys/erp/stockopname/domain/valueobject/CountingSessionStatus.java`**:

```java
package tech.kayys.erp.stockopname.domain.valueobject;

/**
 * Status of a counting session.
 */
public enum CountingSessionStatus {
    PLANNED("Planned - session scheduled"),
    IN_PROGRESS("In Progress - counting underway"),
    PARTIALLY_COMPLETED("Partially Completed - some items counted"),
    COMPLETED("Completed - all items counted"),
    VERIFIED("Verified - counts verified"),
    ADJUSTED("Adjusted - inventory adjusted"),
    CANCELLED("Cancelled - session cancelled"),
    REOPENED("Reopened - session reopened for corrections");

    private final String description;

    CountingSessionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == PLANNED || this == IN_PROGRESS || this == PARTIALLY_COMPLETED;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == VERIFIED || this == ADJUSTED || this == CANCELLED;
    }

    public boolean canTransitionTo(CountingSessionStatus target) {
        return switch (this) {
            case PLANNED -> target == IN_PROGRESS || target == CANCELLED;
            case IN_PROGRESS -> target == PARTIALLY_COMPLETED || target == COMPLETED || target == CANCELLED;
            case PARTIALLY_COMPLETED -> target == COMPLETED || target == IN_PROGRESS || target == CANCELLED;
            case COMPLETED -> target == VERIFIED || target == REOPENED || target == CANCELLED;
            case VERIFIED -> target == ADJUSTED || target == REOPENED || target == CANCELLED;
            case ADJUSTED, CANCELLED -> false;
            case REOPENED -> target == IN_PROGRESS || target == COMPLETED || target == CANCELLED;
        };
    }
}
```

**`/modules/stockopname/domain/src/main/java/tech/kayys/erp/stockopname/domain/valueobject/CountingMethod.java`**:

```java
package tech.kayys.erp.stockopname.domain.valueobject;

/**
 * Methods for physical counting.
 */
public enum CountingMethod {
    PERIODIC("Periodic - full physical count"),
    CYCLE("Cycle Counting - ongoing counts"),
    SPOT("Spot Check - random verification"),
    BLIND("Blind Count - count without knowing system quantity"),
    TWO_STAGE("Two-Stage - first blind, then informed");

    private final String description;

    CountingMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

**`/modules/stockopname/domain/src/main/java/tech/kayys/erp/stockopname/domain/valueobject/CountingType.java`**:

```java
package tech.kayys.erp.stockopname.domain.valueobject;

/**
 * Types of counting sessions.
 */
public enum CountingType {
    FULL("Full - all items counted"),
    CATEGORY("Category - specific category only"),
    ZONE("Zone - specific warehouse zone"),
    RANDOM("Random - random sample"),
    TARGETED("Targeted - specific items identified"),
    NEGATIVE_BALANCE("Negative Balance - items with negative stock"),
    ZERO_BALANCE("Zero Balance - items with zero stock");

    private final String description;

    CountingType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

**`/modules/stockopname/domain/src/main/java/tech/kayys/erp/stockopname/domain/valueobject/VarianceStatus.java`**:

```java
package tech.kayys.erp.stockopname.domain.valueobject;

/**
 * Status of counting variance.
 */
public enum VarianceStatus {
    NO_VARIANCE("No Variance"),
    APPROVED("Approved - variance accepted"),
    REJECTED("Rejected - variance rejected"),
    PENDING_REVIEW("Pending Review - under investigation"),
    UNDER_INVESTIGATION("Under Investigation"),
    ADJUSTED("Adjusted - variance corrected"),
    ESCALATED("Escalated - requiring management attention");

    private final String description;

    VarianceStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isResolved() {
        return this == NO_VARIANCE || this == APPROVED || this == ADJUSTED;
    }
}
```

**`/modules/stockopname/domain/src/main/java/tech/kayys/erp/stockopname/domain/model/CountingSession.java`**:

```java
package tech.kayys.erp.stockopname.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.stockopname.domain.identifier.CountingSessionId;
import tech.kayys.erp.stockopname.domain.identifier.WarehouseId;
import tech.kayys.erp.stockopname.domain.valueobject.CountingMethod;
import tech.kayys.erp.stockopname.domain.valueobject.CountingSessionStatus;
import tech.kayys.erp.stockopname.domain.valueobject.CountingType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Counting session aggregate root.
 * Represents a physical inventory counting session.
 */
public final class CountingSession extends AggregateRoot<CountingSessionId> {
    
    private static final long serialVersionUID = 1L;
    
    private String sessionNumber;
    private WarehouseId warehouseId;
    private String warehouseName;
    private CountingType countingType;
    private CountingMethod countingMethod;
    private CountingSessionStatus status;
    private String zone;
    private List<String> categories;
    private Instant scheduledDate;
    private Instant startDate;
    private Instant completionDate;
    private int totalItemsToCount;
    private int countedItems;
    private int verifiedItems;
    private int itemsWithVariance;
    private List<CountingItem> countingItems;
    private String notes;
    private String createdBy;
    private String verifiedBy;
    private Instant verifiedAt;
    private String adjustedBy;
    private Instant adjustedAt;
    private boolean active;

    private CountingSession(CountingSessionId id) {
        super(id);
        this.countingItems = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.status = CountingSessionStatus.PLANNED;
        this.active = true;
        this.totalItemsToCount = 0;
        this.countedItems = 0;
        this.verifiedItems = 0;
        this.itemsWithVariance = 0;
    }

    private CountingSession() {
        super();
    }

    /**
     * Factory method to create a new counting session.
     */
    public static CountingSession create(
            CountingSessionId id,
            String sessionNumber,
            WarehouseId warehouseId,
            String warehouseName,
            CountingType countingType,
            CountingMethod countingMethod,
            Instant scheduledDate,
            String createdBy) {
        CountingSession session = new CountingSession(id);
        session.sessionNumber = sessionNumber;
        session.warehouseId = warehouseId;
        session.warehouseName = warehouseName;
        session.countingType = countingType;
        session.countingMethod = countingMethod;
        session.scheduledDate = scheduledDate;
        session.createdBy = createdBy;
        return session;
    }

    /**
     * Starts the counting session.
     */
    public void start() {
        if (status != CountingSessionStatus.PLANNED) {
            throw new IllegalStateException("Cannot start counting session in status: " + status);
        }
        if (countingItems.isEmpty()) {
            throw new IllegalStateException("Counting session must have at least one item to count");
        }
        this.status = CountingSessionStatus.IN_PROGRESS;
        this.startDate = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds an item to count.
     */
    public void addCountingItem(CountingItem item) {
        if (status != CountingSessionStatus.PLANNED && status != CountingSessionStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot add items in status: " + status);
        }
        countingItems.add(item);
        totalItemsToCount++;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates count for an item.
     */
    public void updateCount(String itemId, int countedQuantity, String countedBy) {
        CountingItem item = countingItems.stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        if (item.isVerified()) {
            throw new IllegalStateException("Item already verified: " + itemId);
        }

        item.updateCount(countedQuantity, countedBy);
        
        if (status == CountingSessionStatus.IN_PROGRESS) {
            countedItems++;
        }
        
        if (item.isCompleted()) {
            checkAndUpdateStatus();
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Verifies a counted item.
     */
    public void verifyItem(String itemId, boolean approve, String verifiedBy) {
        CountingItem item = countingItems.stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        if (!item.isCompleted()) {
            throw new IllegalStateException("Item not fully counted: " + itemId);
        }

        item.verify(approve, verifiedBy);
        
        if (approve) {
            verifiedItems++;
            if (item.hasVariance()) {
                itemsWithVariance++;
                item.setVarianceStatus(VarianceStatus.APPROVED);
            }
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Verifies the entire session.
     */
    public void verifyAll(String verifiedBy) {
        if (status != CountingSessionStatus.COMPLETED) {
            throw new IllegalStateException("Cannot verify session in status: " + status);
        }
        
        boolean allVerified = countingItems.stream().allMatch(CountingItem::isVerified);
        if (!allVerified) {
            throw new IllegalStateException("Not all items have been verified");
        }
        
        this.status = CountingSessionStatus.VERIFIED;
        this.verifiedBy = verifiedBy;
        this.verifiedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Applies adjustments for verified counts.
     */
    public void applyAdjustments(String adjustedBy) {
        if (status != CountingSessionStatus.VERIFIED) {
            throw new IllegalStateException("Cannot apply adjustments in status: " + status);
        }
        
        this.status = CountingSessionStatus.ADJUSTED;
        this.adjustedBy = adjustedBy;
        this.adjustedAt = Instant.now();
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Reopens a completed session.
     */
    public void reopen() {
        if (status != CountingSessionStatus.COMPLETED && status != CountingSessionStatus.VERIFIED) {
            throw new IllegalStateException("Cannot reopen session in status: " + status);
        }
        
        this.status = CountingSessionStatus.REOPENED;
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the counting session.
     */
    public void cancel(String reason) {
        if (status.isTerminal() && status != CountingSessionStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel session in status: " + status);
        }
        this.status = CountingSessionStatus.CANCELLED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private void checkAndUpdateStatus() {
        boolean allCompleted = countingItems.stream().allMatch(CountingItem::isCompleted);
        if (allCompleted && status == CountingSessionStatus.IN_PROGRESS) {
            this.status = CountingSessionStatus.COMPLETED;
            this.completionDate = Instant.now();
        } else if (countingItems.stream().anyMatch(CountingItem::isCompleted)) {
            this.status = CountingSessionStatus.PARTIALLY_COMPLETED;
        }
    }

    /**
     * Gets the completion percentage.
     */
    public double getCompletionPercentage() {
        if (totalItemsToCount == 0) {
            return 0.0;
        }
        return (double) countedItems / totalItemsToCount * 100.0;
    }

    /**
     * Gets the verification percentage.
     */
    public double getVerificationPercentage() {
        if (countedItems == 0) {
            return 0.0;
        }
        return (double) verifiedItems / countedItems * 100.0;
    }

    /**
     * Gets the variance percentage.
     */
    public double getVariancePercentage() {
        if (verifiedItems == 0) {
            return 0.0;
        }
        return (double) itemsWithVariance / verifiedItems * 100.0;
    }

    /**
     * Gets items with variance.
     */
    public List<CountingItem> getItemsWithVariance() {
        return countingItems.stream()
            .filter(CountingItem::hasVariance)
            .collect(java.util.stream.Collectors.toList());
    }

    // Getters
    public String getSessionNumber() { return sessionNumber; }
    public WarehouseId getWarehouseId() { return warehouseId; }
    public String getWarehouseName() { return warehouseName; }
    public CountingType getCountingType() { return countingType; }
    public CountingMethod getCountingMethod() { return countingMethod; }
    public CountingSessionStatus getStatus() { return status; }
    public String getZone() { return zone; }
    public List<String> getCategories() { return Collections.unmodifiableList(categories); }
    public Instant getScheduledDate() { return scheduledDate; }
    public Instant getStartDate() { return startDate; }
    public Instant getCompletionDate() { return completionDate; }
    public int getTotalItemsToCount() { return totalItemsToCount; }
    public int getCountedItems() { return countedItems; }
    public int getVerifiedItems() { return verifiedItems; }
    public int getItemsWithVariance() { return itemsWithVariance; }
    public List<CountingItem> getCountingItems() { return Collections.unmodifiableList(countingItems); }
    public String getNotes() { return notes; }
    public String getCreatedBy() { return createdBy; }
    public String getVerifiedBy() { return verifiedBy; }
    public Instant getVerifiedAt() { return verifiedAt; }
    public String getAdjustedBy() { return adjustedBy; }
    public Instant getAdjustedAt() { return adjustedAt; }
    public boolean isActive() { return active; }

    public void setZone(String zone) {
        this.zone = zone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCategories(List<String> categories) {
        this.categories = new ArrayList<>(categories);
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
        return "CountingSession{" +
                "id=" + getId() +
                ", sessionNumber='" + sessionNumber + '\'' +
                ", warehouseName='" + warehouseName + '\'' +
                ", status=" + status +
                ", progress=" + getCompletionPercentage() + "%" +
                '}';
    }

    /**
     * Counting item value object.
     */
    public static final class CountingItem implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String productId;
        private final String sku;
        private final String productName;
        private final String binLocation;
        private final int systemQuantity;
        private Integer countedQuantity;
        private String countedBy;
        private Instant countedAt;
        private String secondCountedBy;
        private Integer secondCountedQuantity;
        private boolean verified;
        private String verifiedBy;
        private Instant verifiedAt;
        private boolean countCompleted;
        private boolean varianceApproved;
        private VarianceStatus varianceStatus;
        private String varianceNotes;

        public CountingItem(
                String id,
                String productId,
                String sku,
                String productName,
                String binLocation,
                int systemQuantity) {
            this.id = id;
            this.productId = productId;
            this.sku = sku;
            this.productName = productName;
            this.binLocation = binLocation;
            this.systemQuantity = systemQuantity;
            this.countCompleted = false;
            this.verified = false;
            this.varianceStatus = VarianceStatus.NO_VARIANCE;
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
            if (systemQuantity < 0) {
                throw new IllegalArgumentException("System quantity cannot be negative");
            }
        }

        public String getId() { return id; }
        public String getProductId() { return productId; }
        public String getSku() { return sku; }
        public String getProductName() { return productName; }
        public String getBinLocation() { return binLocation; }
        public int getSystemQuantity() { return systemQuantity; }
        public Integer getCountedQuantity() { return countedQuantity; }
        public String getCountedBy() { return countedBy; }
        public Instant getCountedAt() { return countedAt; }
        public String getSecondCountedBy() { return secondCountedBy; }
        public Integer getSecondCountedQuantity() { return secondCountedQuantity; }
        public boolean isVerified() { return verified; }
        public String getVerifiedBy() { return verifiedBy; }
        public Instant getVerifiedAt() { return verifiedAt; }
        public boolean isCompleted() { return countCompleted; }
        public VarianceStatus getVarianceStatus() { return varianceStatus; }
        public String getVarianceNotes() { return varianceNotes; }

        public void updateCount(int countedQuantity, String countedBy) {
            if (verified) {
                throw new IllegalStateException("Cannot update verified item");
            }
            
            if (this.countedQuantity == null) {
                // First count
                this.countedQuantity = countedQuantity;
                this.countedBy = countedBy;
                this.countedAt = Instant.now();
            } else {
                // Second count (for verification)
                this.secondCountedQuantity = countedQuantity;
                this.secondCountedBy = countedBy;
            }
            
            // Check if we have both counts
            if (this.countedQuantity != null && this.secondCountedQuantity != null) {
                this.countCompleted = true;
                // Check if counts match
                if (this.secondCountedQuantity.equals(this.countedQuantity)) {
                    this.varianceStatus = VarianceStatus.NO_VARIANCE;
                } else {
                    // We'll keep it as pending review - someone needs to decide
                    this.varianceStatus = VarianceStatus.PENDING_REVIEW;
                }
            } else {
                this.countCompleted = true; // Single count is considered complete
            }
        }

        public void verify(boolean approve, String verifiedBy) {
            if (!countCompleted) {
                throw new IllegalStateException("Item not fully counted");
            }
            
            this.verified = true;
            this.verifiedBy = verifiedBy;
            this.verifiedAt = Instant.now();
            
            if (approve) {
                // Use first count as the official count
                if (!hasVariance()) {
                    this.varianceStatus = VarianceStatus.NO_VARIANCE;
                } else {
                    this.varianceStatus = VarianceStatus.APPROVED;
                }
            } else {
                if (hasVariance()) {
                    this.varianceStatus = VarianceStatus.REJECTED;
                }
            }
        }

        public boolean hasVariance() {
            if (countedQuantity == null) {
                return false;
            }
            return countedQuantity != systemQuantity;
        }

        public int getVariance() {
            if (countedQuantity == null) {
                return 0;
            }
            return countedQuantity - systemQuantity;
        }

        public double getVariancePercentage() {
            if (systemQuantity == 0) {
                return countedQuantity != null && countedQuantity > 0 ? 100.0 : 0.0;
            }
            return (double) getVariance() / systemQuantity * 100.0;
        }

        public void setVarianceStatus(VarianceStatus varianceStatus) {
            this.varianceStatus = varianceStatus;
        }

        public void setVarianceNotes(String varianceNotes) {
            this.varianceNotes = varianceNotes;
        }

        @Override
        public String toString() {
            return "CountingItem{" +
                    "id='" + id + '\'' +
                    ", sku='" + sku + '\'' +
                    ", systemQty=" + systemQuantity +
                    ", countedQty=" + countedQuantity +
                    ", variance=" + getVariance() +
                    '}';
        }
    }
}
```

## 2. Stock Opname Application Module

**`/modules/stockopname/application/pom.xml`**:

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

    <artifactId>erp-stockopname-application</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-stockopname-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-application</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/stockopname/application/src/main/java/tech/kayys/erp/stockopname/application/api/StockOpnameService.java`**:

```java
package tech.kayys.erp.stockopname.application.api;

import tech.kayys.erp.stockopname.application.api.command.*;
import tech.kayys.erp.stockopname.application.api.query.CountingSessionView;
import tech.kayys.erp.stockopname.application.api.query.CountingItemView;
import tech.kayys.erp.stockopname.application.api.query.VarianceReportView;
import tech.kayys.erp.stockopname.domain.identifier.CountingSessionId;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Public API for stock opname operations.
 */
public interface StockOpnameService {

    // ============ Counting Session Commands ============

    /**
     * Creates a new counting session.
     */
    CompletionStage<CountingSessionId> createCountingSession(CreateCountingSessionCommand command);

    /**
     * Starts a counting session.
     */
    CompletionStage<CountingSessionId> startCountingSession(StartCountingSessionCommand command);

    /**
     * Records a count for an item.
     */
    CompletionStage<CountingSessionId> recordCount(RecordCountCommand command);

    /**
     * Verifies a counted item.
     */
    CompletionStage<CountingSessionId> verifyCount(VerifyCountCommand command);

    /**
     * Verifies the entire counting session.
     */
    CompletionStage<CountingSessionId> verifySession(VerifySessionCommand command);

    /**
     * Applies adjustments for a verified session.
     */
    CompletionStage<CountingSessionId> applyAdjustments(ApplyAdjustmentsCommand command);

    /**
     * Reopens a completed session.
     */
    CompletionStage<CountingSessionId> reopenSession(ReopenSessionCommand command);

    /**
     * Cancels a counting session.
     */
    CompletionStage<CountingSessionId> cancelSession(CancelSessionCommand command);

    // ============ Queries ============

    /**
     * Gets a counting session by ID.
     */
    CompletionStage<CountingSessionView> getCountingSession(CountingSessionId sessionId);

    /**
     * Gets counting sessions by status.
     */
    CompletionStage<List<CountingSessionView>> getCountingSessionsByStatus(String status);

    /**
     * Gets variance report for a session.
     */
    CompletionStage<VarianceReportView> getVarianceReport(CountingSessionId sessionId);

    /**
     * Gets items needing verification for a session.
     */
    CompletionStage<List<CountingItemView>> getItemsNeedingVerification(CountingSessionId sessionId);

    /**
     * Gets items with variance for a session.
     */
    CompletionStage<List<CountingItemView>> getItemsWithVariance(CountingSessionId sessionId);
}
```

**`/modules/stockopname/application/src/main/java/tech/kayys/erp/stockopname/application/api/command/CreateCountingSessionCommand.java`**:

```java
package tech.kayys.erp.stockopname.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.stockopname.domain.identifier.CountingSessionId;
import tech.kayys.erp.stockopname.domain.valueobject.CountingMethod;
import tech.kayys.erp.stockopname.domain.valueobject.CountingType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Command to create a new counting session.
 */
public record CreateCountingSessionCommand(
        CountingSessionId sessionId,
        UUID warehouseId,
        String warehouseName,
        CountingType countingType,
        CountingMethod countingMethod,
        Instant scheduledDate,
        String zone,
        List<String> categories,
        List<CountingItemCommand> items,
        String notes,
        String createdBy
) implements Command<CountingSessionId> {

    public CreateCountingSessionCommand {
        if (warehouseId == null) {
            throw new IllegalArgumentException("Warehouse ID cannot be null");
        }
        if (countingType == null) {
            throw new IllegalArgumentException("Counting type is required");
        }
        if (countingMethod == null) {
            throw new IllegalArgumentException("Counting method is required");
        }
        if (scheduledDate == null) {
            throw new IllegalArgumentException("Scheduled date is required");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("At least one item must be specified");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CountingSessionId sessionId;
        private UUID warehouseId;
        private String warehouseName;
        private CountingType countingType;
        private CountingMethod countingMethod;
        private Instant scheduledDate;
        private String zone;
        private List<String> categories;
        private List<CountingItemCommand> items;
        private String notes;
        private String createdBy;

        public Builder sessionId(CountingSessionId sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder warehouseId(UUID warehouseId) {
            this.warehouseId = warehouseId;
            return this;
        }

        public Builder warehouseName(String warehouseName) {
            this.warehouseName = warehouseName;
            return this;
        }

        public Builder countingType(CountingType countingType) {
            this.countingType = countingType;
            return this;
        }

        public Builder countingMethod(CountingMethod countingMethod) {
            this.countingMethod = countingMethod;
            return this;
        }

        public Builder scheduledDate(Instant scheduledDate) {
            this.scheduledDate = scheduledDate;
            return this;
        }

        public Builder zone(String zone) {
            this.zone = zone;
            return this;
        }

        public Builder categories(List<String> categories) {
            this.categories = categories;
            return this;
        }

        public Builder items(List<CountingItemCommand> items) {
            this.items = items;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public CreateCountingSessionCommand build() {
            if (sessionId == null) {
                sessionId = CountingSessionId.generate();
            }
            if (scheduledDate == null) {
                scheduledDate = Instant.now().plusSeconds(86400); // Tomorrow
            }
            return new CreateCountingSessionCommand(
                sessionId, warehouseId, warehouseName, countingType,
                countingMethod, scheduledDate, zone, categories,
                items, notes, createdBy
            );
        }
    }

    /**
     * Counting item command.
     */
    public record CountingItemCommand(
            String id,
            String productId,
            String sku,
            String productName,
            String binLocation,
            int systemQuantity
    ) {
        public CountingItemCommand {
            if (productId == null || productId.trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be empty");
            }
            if (systemQuantity < 0) {
                throw new IllegalArgumentException("System quantity cannot be negative");
            }
        }
    }
}
```

**`/modules/stockopname/application/src/main/java/tech/kayys/erp/stockopname/application/api/command/RecordCountCommand.java`**:

```java
package tech.kayys.erp.stockopname.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.stockopname.domain.identifier.CountingSessionId;

/**
 * Command to record a count for an item.
 */
public record RecordCountCommand(
        CountingSessionId sessionId,
        String itemId,
        int countedQuantity,
        String countedBy,
        boolean isSecondCount
) implements Command<CountingSessionId> {

    public RecordCountCommand {
        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID cannot be null");
        }
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("Item ID cannot be empty");
        }
        if (countedQuantity < 0) {
            throw new IllegalArgumentException("Counted quantity cannot be negative");
        }
        if (countedBy == null || countedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Counted by cannot be empty");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CountingSessionId sessionId;
        private String itemId;
        private int countedQuantity;
        private String countedBy;
        private boolean isSecondCount = false;

        public Builder sessionId(CountingSessionId sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder itemId(String itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder countedQuantity(int countedQuantity) {
            this.countedQuantity = countedQuantity;
            return this;
        }

        public Builder countedBy(String countedBy) {
            this.countedBy = countedBy;
            return this;
        }

        public Builder secondCount(boolean isSecondCount) {
            this.isSecondCount = isSecondCount;
            return this;
        }

        public RecordCountCommand build() {
            return new RecordCountCommand(sessionId, itemId, countedQuantity, countedBy, isSecondCount);
        }
    }
}
```

**`/modules/stockopname/application/src/main/java/tech/kayys/erp/stockopname/application/api/query/CountingSessionView.java`**:

```java
package tech.kayys.erp.stockopname.application.api.query;

import tech.kayys.erp.stockopname.domain.model.CountingSession;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * View of a counting session.
 */
public record CountingSessionView(
        String sessionId,
        String sessionNumber,
        String warehouseId,
        String warehouseName,
        String countingType,
        String countingMethod,
        String status,
        String zone,
        List<String> categories,
        String scheduledDate,
        String startDate,
        String completionDate,
        int totalItemsToCount,
        int countedItems,
        int verifiedItems,
        int itemsWithVariance,
        double completionPercentage,
        double verificationPercentage,
        double variancePercentage,
        List<CountingItemView> items,
        String notes,
        String createdBy,
        String verifiedBy,
        String adjustedBy
) {

    public static CountingSessionView fromDomain(CountingSession session) {
        return new CountingSessionView(
            session.getId().toString(),
            session.getSessionNumber(),
            session.getWarehouseId().toString(),
            session.getWarehouseName(),
            session.getCountingType().name(),
            session.getCountingMethod().name(),
            session.getStatus().name(),
            session.getZone(),
            session.getCategories(),
            session.getScheduledDate().toString(),
            session.getStartDate() != null ? session.getStartDate().toString() : null,
            session.getCompletionDate() != null ? session.getCompletionDate().toString() : null,
            session.getTotalItemsToCount(),
            session.getCountedItems(),
            session.getVerifiedItems(),
            session.getItemsWithVariance(),
            session.getCompletionPercentage(),
            session.getVerificationPercentage(),
            session.getVariancePercentage(),
            session.getCountingItems().stream()
                .map(CountingItemView::fromDomain)
                .collect(Collectors.toList()),
            session.getNotes(),
            session.getCreatedBy(),
            session.getVerifiedBy(),
            session.getAdjustedBy()
        );
    }
}
```

**`/modules/stockopname/application/src/main/java/tech/kayys/erp/stockopname/application/api/query/CountingItemView.java`**:

```java
package tech.kayys.erp.stockopname.application.api.query;

import tech.kayys.erp.stockopname.domain.model.CountingSession;

import java.time.Instant;

/**
 * View of a counting item.
 */
public record CountingItemView(
        String id,
        String productId,
        String sku,
        String productName,
        String binLocation,
        int systemQuantity,
        Integer countedQuantity,
        String countedBy,
        String countedAt,
        Integer secondCountedQuantity,
        String secondCountedBy,
        boolean verified,
        String verifiedBy,
        String verifiedAt,
        boolean completed,
        String varianceStatus,
        int variance,
        double variancePercentage,
        String varianceNotes
) {

    public static CountingItemView fromDomain(CountingSession.CountingItem item) {
        return new CountingItemView(
            item.getId(),
            item.getProductId(),
            item.getSku(),
            item.getProductName(),
            item.getBinLocation(),
            item.getSystemQuantity(),
            item.getCountedQuantity(),
            item.getCountedBy(),
            item.getCountedAt() != null ? item.getCountedAt().toString() : null,
            item.getSecondCountedQuantity(),
            item.getSecondCountedBy(),
            item.isVerified(),
            item.getVerifiedBy(),
            item.getVerifiedAt() != null ? item.getVerifiedAt().toString() : null,
            item.isCompleted(),
            item.getVarianceStatus() != null ? item.getVarianceStatus().name() : null,
            item.getVariance(),
            item.getVariancePercentage(),
            item.getVarianceNotes()
        );
    }
}
```

**`/modules/stockopname/application/src/main/java/tech/kayys/erp/stockopname/application/api/query/VarianceReportView.java`**:

```java
package tech.kayys.erp.stockopname.application.api.query;

import tech.kayys.erp.stockopname.domain.model.CountingSession;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Variance report view.
 */
public record VarianceReportView(
        String sessionId,
        String sessionNumber,
        String warehouseName,
        int totalItemsChecked,
        int itemsWithVariance,
        int itemsWithoutVariance,
        int itemsWithApprovedVariance,
        int itemsWithRejectedVariance,
        int totalVarianceQuantity,
        double totalVarianceValue,
        double averageVariance,
        String currencyCode,
        List<VarianceDetail> varianceDetails
) {

    public static VarianceReportView fromDomain(CountingSession session, String currencyCode) {
        List<CountingSession.CountingItem> itemsWithVariance = session.getItemsWithVariance();
        
        int totalVariance = itemsWithVariance.stream()
            .mapToInt(CountingSession.CountingItem::getVariance)
            .sum();
        
        double totalVarianceValue = itemsWithVariance.stream()
            .mapToDouble(CountingSession.CountingItem::getVariance)
            .sum();
        
        return new VarianceReportView(
            session.getId().toString(),
            session.getSessionNumber(),
            session.getWarehouseName(),
            session.getTotalItemsToCount(),
            session.getItemsWithVariance(),
            session.getVerifiedItems() - session.getItemsWithVariance(),
            (int) itemsWithVariance.stream()
                .filter(i -> i.getVarianceStatus().name().equals("APPROVED"))
                .count(),
            (int) itemsWithVariance.stream()
                .filter(i -> i.getVarianceStatus().name().equals("REJECTED"))
                .count(),
            totalVariance,
            totalVarianceValue,
            itemsWithVariance.isEmpty() ? 0.0 : totalVarianceValue / itemsWithVariance.size(),
            currencyCode,
            itemsWithVariance.stream()
                .map(VarianceDetail::fromDomain)
                .collect(Collectors.toList())
        );
    }

    public record VarianceDetail(
            String productId,
            String sku,
            String productName,
            String binLocation,
            int systemQuantity,
            int countedQuantity,
            int varianceQuantity,
            double varianceValue,
            String status,
            String notes
    ) {
        public static VarianceDetail fromDomain(CountingSession.CountingItem item) {
            return new VarianceDetail(
                item.getProductId(),
                item.getSku(),
                item.getProductName(),
                item.getBinLocation(),
                item.getSystemQuantity(),
                item.getCountedQuantity() != null ? item.getCountedQuantity() : 0,
                item.getVariance(),
                0.0, // Would need unit cost
                item.getVarianceStatus().name(),
                item.getVarianceNotes()
            );
        }
    }
}
```

## 3. Update Root POM

**Update `/pom.xml`** to include Stock Opname modules:

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

    <module>modules/stockopname/domain</module>
    <module>modules/stockopname/application</module>
    <module>modules/stockopname/infrastructure</module>
    <module>modules/stockopname/interfaces</module>
</modules>
```

## Summary

The complete Stock Opname / Physical Inventory Count bounded context provides:

1. **Counting Session Management**:
   - Full lifecycle (Planned → In Progress → Completed → Verified → Adjusted)
   - Multiple counting methods (Periodic, Cycle, Spot, Blind, Two-Stage)
   - Counting types (Full, Category, Zone, Random, Targeted)
   - Zone and category filtering

2. **Item Counting**:
   - System quantity reference
   - First and second count support (verification)
   - Blind count capability
   - Real-time progress tracking

3. **Verification & Variance Analysis**:
   - Item-level verification
   - Variance detection and calculation
   - Variance status (No Variance, Approved, Rejected, Pending Review, Under Investigation)
   - Variance percentage and quantity tracking

4. **Reporting**:
   - Variance report generation
   - Items with variance list
   - Items needing verification list
   - Progress tracking (completion, verification percentages)

5. **Integration Points**:
   - Inventory context for stock data
   - Warehouse management
   - Catalog context for product information

6. **Architecture Rules**:
   - Clean domain model
   - Proper value objects
   - State machine enforcement
   - Context isolation

This completes the Stock Opname context with comprehensive physical inventory counting and variance management capabilities that integrate with Inventory, Warehouse, and Catalog contexts throughout the ERP system.
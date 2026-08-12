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
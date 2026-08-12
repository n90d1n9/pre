package tech.kayys.erp.pricing.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.pricing.domain.identifier.PriceBookId;
import tech.kayys.erp.pricing.domain.valueobject.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Price Book aggregate root.
 * Represents a collection of prices for products/services.
 */
public final class PriceBook extends AggregateRoot<PriceBookId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String code;
    private String description;
    private PriceBookType type;
    private PriceBookStatus status;
    private String currencyCode;
    private String customerSegment;
    private String region;
    private String channel;
    private List<PriceEntry> entries;
    private Instant validFrom;
    private Instant validTo;
    private String createdBy;
    private String approvedBy;
    private Instant approvedAt;
    private String notes;
    private boolean active;

    private PriceBook(PriceBookId id) {
        super(id);
        this.entries = new ArrayList<>();
        this.status = PriceBookStatus.DRAFT;
        this.active = true;
        this.type = PriceBookType.STANDARD;
    }

    private PriceBook() {
        super();
    }

    /**
     * Factory method to create a new price book.
     */
    public static PriceBook create(
            PriceBookId id,
            String name,
            String code,
            PriceBookType type,
            String currencyCode) {
        PriceBook priceBook = new PriceBook(id);
        priceBook.name = name;
        priceBook.code = code;
        priceBook.type = type;
        priceBook.currencyCode = currencyCode;
        return priceBook;
    }

    /**
     * Adds a price entry to the price book.
     */
    public void addEntry(PriceEntry entry) {
        if (status == PriceBookStatus.APPROVED || status == PriceBookStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot modify approved or archived price book");
        }
        entries.removeIf(e -> e.getProductId().equals(entry.getProductId()));
        entries.add(entry);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes a price entry from the price book.
     */
    public void removeEntry(String productId) {
        if (status == PriceBookStatus.APPROVED || status == PriceBookStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot modify approved or archived price book");
        }
        entries.removeIf(e -> e.getProductId().equals(productId));
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the price for a product from the price book.
     */
    public Money getPrice(String productId) {
        return entries.stream()
            .filter(e -> e.getProductId().equals(productId))
            .findFirst()
            .map(PriceEntry::getPrice)
            .orElse(null);
    }

    /**
     * Submits the price book for approval.
     */
    public void submitForApproval() {
        if (status != PriceBookStatus.DRAFT) {
            throw new IllegalStateException("Cannot submit price book in status: " + status);
        }
        if (entries.isEmpty()) {
            throw new IllegalStateException("Price book must have at least one entry");
        }
        this.status = PriceBookStatus.PENDING_APPROVAL;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Approves the price book.
     */
    public void approve(String approvedBy) {
        if (status != PriceBookStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Cannot approve price book in status: " + status);
        }
        this.status = PriceBookStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Rejects the price book.
     */
    public void reject(String reason) {
        if (status != PriceBookStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Cannot reject price book in status: " + status);
        }
        this.status = PriceBookStatus.REJECTED;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the price book.
     */
    public void activate() {
        if (status != PriceBookStatus.APPROVED) {
            throw new IllegalStateException("Cannot activate price book in status: " + status);
        }
        this.status = PriceBookStatus.ACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Archives the price book.
     */
    public void archive() {
        if (status != PriceBookStatus.ACTIVE) {
            throw new IllegalStateException("Cannot archive price book in status: " + status);
        }
        this.status = PriceBookStatus.ARCHIVED;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if the price book is currently valid.
     */
    public boolean isValid() {
        if (status != PriceBookStatus.ACTIVE) {
            return false;
        }
        Instant now = Instant.now();
        if (validFrom != null && now.isBefore(validFrom)) {
            return false;
        }
        if (validTo != null && now.isAfter(validTo)) {
            return false;
        }
        return true;
    }

    // Getters
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public PriceBookType getType() { return type; }
    public PriceBookStatus getStatus() { return status; }
    public String getCurrencyCode() { return currencyCode; }
    public String getCustomerSegment() { return customerSegment; }
    public String getRegion() { return region; }
    public String getChannel() { return channel; }
    public List<PriceEntry> getEntries() { return Collections.unmodifiableList(entries); }
    public Instant getValidFrom() { return validFrom; }
    public Instant getValidTo() { return validTo; }
    public String getCreatedBy() { return createdBy; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCustomerSegment(String customerSegment) {
        this.customerSegment = customerSegment;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setRegion(String region) {
        this.region = region;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setChannel(String channel) {
        this.channel = channel;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setValidTo(Instant validTo) {
        this.validTo = validTo;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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
        return "PriceBook{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", entries=" + entries.size() +
                '}';
    }

    /**
     * Price book type enum.
     */
    public enum PriceBookType {
        STANDARD("Standard Price Book"),
        PROMOTIONAL("Promotional Price Book"),
        CUSTOMER_SPECIFIC("Customer Specific"),
        REGIONAL("Regional Price Book"),
        CHANNEL_SPECIFIC("Channel Specific");

        private final String displayName;

        PriceBookType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Price book status enum.
     */
    public enum PriceBookStatus {
        DRAFT("Draft"),
        PENDING_APPROVAL("Pending Approval"),
        APPROVED("Approved"),
        REJECTED("Rejected"),
        ACTIVE("Active"),
        ARCHIVED("Archived");

        private final String description;

        PriceBookStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Price entry value object.
     */
    public static final class PriceEntry implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String productId;
        private final String productSku;
        private final String productName;
        private final Money price;
        private final Money compareAtPrice;
        private final Money cost;
        private final String priceType; // FIXED, VARIABLE
        private final String unit;
        private final double minQuantity;
        private final double maxQuantity;
        private final String notes;

        public PriceEntry(
                String productId,
                String productSku,
                String productName,
                Money price,
                Money compareAtPrice,
                Money cost,
                String priceType,
                String unit,
                double minQuantity,
                double maxQuantity,
                String notes) {
            this.productId = productId;
            this.productSku = productSku;
            this.productName = productName;
            this.price = price;
            this.compareAtPrice = compareAtPrice;
            this.cost = cost;
            this.priceType = priceType;
            this.unit = unit;
            this.minQuantity = minQuantity;
            this.maxQuantity = maxQuantity;
            this.notes = notes;
            validate();
        }

        @Override
        public void validate() {
            if (productId == null || productId.trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be empty");
            }
            if (price == null || price.isNegative()) {
                throw new IllegalArgumentException("Price must be positive");
            }
        }

        public String getProductId() { return productId; }
        public String getProductSku() { return productSku; }
        public String getProductName() { return productName; }
        public Money getPrice() { return price; }
        public Money getCompareAtPrice() { return compareAtPrice; }
        public Money getCost() { return cost; }
        public String getPriceType() { return priceType; }
        public String getUnit() { return unit; }
        public double getMinQuantity() { return minQuantity; }
        public double getMaxQuantity() { return maxQuantity; }
        public String getNotes() { return notes; }

        public Money getMargin() {
            if (cost == null || cost.isZero()) {
                return Money.zero(price.getCurrency().getCurrencyCode());
            }
            return price.subtract(cost);
        }

        public double getMarginPercentage() {
            if (price.isZero()) {
                return 0.0;
            }
            Money margin = getMargin();
            return margin.getAmount()
                .divide(price.getAmount(), 4, java.math.RoundingMode.HALF_UP)
                .multiply(java.math.BigDecimal.valueOf(100))
                .doubleValue();
        }
    }
}
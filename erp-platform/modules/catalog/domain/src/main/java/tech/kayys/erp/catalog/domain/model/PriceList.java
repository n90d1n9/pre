package tech.kayys.erp.catalog.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.catalog.domain.identifier.PriceListId;
import tech.kayys.erp.catalog.domain.valueobject.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Price list aggregate root.
 * Represents a price list with product-specific pricing.
 */
public final class PriceList extends AggregateRoot<PriceListId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private PriceListType type; // RETAIL, WHOLESALE, PROMOTION, CUSTOMER
    private String currencyCode;
    private boolean active;
    private Instant validFrom;
    private Instant validTo;
    private List<PriceListItem> items;
    private boolean defaultForType;
    private String customerSegment;

    private PriceList(PriceListId id) {
        super(id);
        this.items = new ArrayList<>();
        this.active = true;
        this.defaultForType = false;
    }

    private PriceList() {
        super();
    }

    /**
     * Factory method to create a new price list.
     */
    public static PriceList create(
            PriceListId id,
            String name,
            PriceListType type,
            String currencyCode) {
        PriceList priceList = new PriceList(id);
        priceList.name = name;
        priceList.type = type;
        priceList.currencyCode = currencyCode;
        return priceList;
    }

    /**
     * Adds a price list item.
     */
    public void addItem(PriceListItem item) {
        if (items.stream().anyMatch(i -> i.getProductId().equals(item.getProductId()))) {
            throw new IllegalArgumentException("Product already in price list: " + item.getProductId());
        }
        items.add(item);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates a price list item.
     */
    public void updateItem(UUID productId, Money price, Money compareAtPrice) {
        for (PriceListItem item : items) {
            if (item.getProductId().equals(productId)) {
                // Remove and re-add with updated price
                items.remove(item);
                PriceListItem updated = new PriceListItem(
                    productId,
                    price,
                    compareAtPrice
                );
                items.add(updated);
                break;
            }
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes a price list item.
     */
    public void removeItem(UUID productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the price for a product.
     */
    public Money getPriceForProduct(UUID productId) {
        return items.stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst()
            .map(PriceListItem::getPrice)
            .orElse(null);
    }

    /**
     * Validates the price list.
     */
    public boolean isValid() {
        if (!active) {
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

    // Getters and Setters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public PriceListType getType() { return type; }
    public String getCurrencyCode() { return currencyCode; }
    public boolean isActive() { return active; }
    public Instant getValidFrom() { return validFrom; }
    public Instant getValidTo() { return validTo; }
    public List<PriceListItem> getItems() { return Collections.unmodifiableList(items); }
    public boolean isDefaultForType() { return defaultForType; }
    public String getCustomerSegment() { return customerSegment; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public void setDefaultForType(boolean defaultForType) {
        this.defaultForType = defaultForType;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCustomerSegment(String customerSegment) {
        this.customerSegment = customerSegment;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "PriceList{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", items=" + items.size() +
                '}';
    }

    /**
     * Price list type enum.
     */
    public enum PriceListType {
        RETAIL("Retail"),
        WHOLESALE("Wholesale"),
        PROMOTION("Promotion"),
        CUSTOMER("Customer-Specific"),
        DISTRIBUTOR("Distributor");

        private final String displayName;

        PriceListType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Price list item.
     */
    public static final class PriceListItem implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final UUID productId;
        private final Money price;
        private final Money compareAtPrice;

        public PriceListItem(UUID productId, Money price, Money compareAtPrice) {
            this.productId = productId;
            this.price = price;
            this.compareAtPrice = compareAtPrice;
            validate();
        }

        @Override
        public void validate() {
            if (productId == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            if (price == null || price.isNegative()) {
                throw new IllegalArgumentException("Price must be positive");
            }
        }

        public UUID getProductId() { return productId; }
        public Money getPrice() { return price; }
        public Money getCompareAtPrice() { return compareAtPrice; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PriceListItem that = (PriceListItem) o;
            return Objects.equals(productId, that.productId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(productId);
        }
    }
}
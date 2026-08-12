package tech.kayys.erp.catalog.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.catalog.domain.identifier.BundleId;
import tech.kayys.erp.catalog.domain.valueobject.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Product bundle aggregate root.
 * Represents a bundle or kit of products sold together.
 */
public final class ProductBundle extends AggregateRoot<BundleId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private UUID parentProductId; // The bundle product in catalog
    private List<BundleItem> items;
    private Money totalPrice;
    private Money savings;
    private double savingsPercentage;
    private boolean active;
    private boolean allowPartialSelection;
    private String bundleType; // FIXED, VARIABLE, SUBSCRIPTION
    private int maxItemsSelected;

    private ProductBundle(BundleId id) {
        super(id);
        this.items = new ArrayList<>();
        this.active = true;
        this.allowPartialSelection = false;
        this.bundleType = "FIXED";
        this.maxItemsSelected = 0;
    }

    private ProductBundle() {
        super();
    }

    /**
     * Factory method to create a new product bundle.
     */
    public static ProductBundle create(
            BundleId id,
            String name,
            UUID parentProductId,
            String bundleType) {
        ProductBundle bundle = new ProductBundle(id);
        bundle.name = name;
        bundle.parentProductId = parentProductId;
        bundle.bundleType = bundleType;
        return bundle;
    }

    /**
     * Adds an item to the bundle.
     */
    public void addItem(BundleItem item) {
        if (items.stream().anyMatch(i -> i.getProductId().equals(item.getProductId()))) {
            throw new IllegalArgumentException("Product already in bundle: " + item.getProductId());
        }
        items.add(item);
        recalculate();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes an item from the bundle.
     */
    public void removeItem(UUID productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
        recalculate();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private void recalculate() {
        if (items.isEmpty()) {
            totalPrice = Money.zero("USD");
            savings = Money.zero("USD");
            savingsPercentage = 0.0;
            return;
        }

        Money totalItemPrice = items.stream()
            .map(BundleItem::getPrice)
            .reduce(Money.zero("USD"), Money::add);

        // Bundle price is typically less than sum of individual prices
        totalPrice = totalItemPrice.multiply(0.9); // 10% discount for bundle
        savings = totalItemPrice.subtract(totalPrice);
        
        if (!totalItemPrice.isZero()) {
            savingsPercentage = savings.getAmount()
                .divide(totalItemPrice.getAmount(), 4, java.math.RoundingMode.HALF_UP)
                .multiply(java.math.BigDecimal.valueOf(100))
                .doubleValue();
        }
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public UUID getParentProductId() { return parentProductId; }
    public List<BundleItem> getItems() { return Collections.unmodifiableList(items); }
    public Money getTotalPrice() { return totalPrice; }
    public Money getSavings() { return savings; }
    public double getSavingsPercentage() { return savingsPercentage; }
    public boolean isActive() { return active; }
    public boolean isAllowPartialSelection() { return allowPartialSelection; }
    public String getBundleType() { return bundleType; }
    public int getMaxItemsSelected() { return maxItemsSelected; }

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

    public void setAllowPartialSelection(boolean allowPartialSelection) {
        this.allowPartialSelection = allowPartialSelection;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMaxItemsSelected(int maxItemsSelected) {
        this.maxItemsSelected = maxItemsSelected;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "ProductBundle{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", items=" + items.size() +
                ", totalPrice=" + totalPrice +
                '}';
    }

    /**
     * Bundle item value object.
     */
    public static final class BundleItem implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final UUID productId;
        private final String productName;
        private final String sku;
        private final int quantity;
        private final Money price;
        private final boolean optional;
        private final String variationId;

        public BundleItem(
                UUID productId,
                String productName,
                String sku,
                int quantity,
                Money price,
                boolean optional,
                String variationId) {
            this.productId = productId;
            this.productName = productName;
            this.sku = sku;
            this.quantity = quantity;
            this.price = price;
            this.optional = optional;
            this.variationId = variationId;
            validate();
        }

        @Override
        public void validate() {
            if (productId == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (price == null || price.isNegative()) {
                throw new IllegalArgumentException("Price must be positive");
            }
        }

        public UUID getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getSku() { return sku; }
        public int getQuantity() { return quantity; }
        public Money getPrice() { return price; }
        public boolean isOptional() { return optional; }
        public String getVariationId() { return variationId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BundleItem that = (BundleItem) o;
            return Objects.equals(productId, that.productId) &&
                   Objects.equals(variationId, that.variationId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(productId, variationId);
        }

        @Override
        public String toString() {
            return "BundleItem{" +
                    "productId=" + productId +
                    ", productName='" + productName + '\'' +
                    ", quantity=" + quantity +
                    ", price=" + price +
                    '}';
        }
    }
}
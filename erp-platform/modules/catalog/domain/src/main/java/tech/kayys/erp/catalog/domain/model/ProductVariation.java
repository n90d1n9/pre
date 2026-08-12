package tech.kayys.erp.catalog.domain.model;

import tech.kayys.erp.foundation.domain.ValueObject;
import tech.kayys.erp.catalog.domain.valueobject.Money;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Product variation value object.
 * Represents a product variant (e.g., size, color, style).
 */
public final class ProductVariation implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID variationId;
    private final String sku;
    private final Map<String, String> attributes; // attribute name -> value
    private final Money price;
    private final Money compareAtPrice;
    private final int stockQuantity;
    private final int reservedQuantity;
    private final String imageUrl;
    private final boolean active;

    public ProductVariation(
            UUID variationId,
            String sku,
            Map<String, String> attributes,
            Money price,
            Money compareAtPrice,
            int stockQuantity,
            int reservedQuantity,
            String imageUrl,
            boolean active) {
        this.variationId = variationId;
        this.sku = sku;
        this.attributes = attributes;
        this.price = price;
        this.compareAtPrice = compareAtPrice;
        this.stockQuantity = stockQuantity;
        this.reservedQuantity = reservedQuantity;
        this.imageUrl = imageUrl;
        this.active = active;
        validate();
    }

    @Override
    public void validate() {
        if (variationId == null) {
            throw new IllegalArgumentException("Variation ID cannot be null");
        }
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU cannot be empty");
        }
        if (attributes == null || attributes.isEmpty()) {
            throw new IllegalArgumentException("Attributes cannot be empty");
        }
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
    }

    // Getters
    public UUID getVariationId() { return variationId; }
    public String getSku() { return sku; }
    public Map<String, String> getAttributes() { return Collections.unmodifiableMap(attributes); }
    public Money getPrice() { return price; }
    public Money getCompareAtPrice() { return compareAtPrice; }
    public int getStockQuantity() { return stockQuantity; }
    public int getReservedQuantity() { return reservedQuantity; }
    public int getAvailableQuantity() { return stockQuantity - reservedQuantity; }
    public String getImageUrl() { return imageUrl; }
    public boolean isActive() { return active; }

    public ProductVariation withStock(int newStock) {
        return new ProductVariation(
            variationId, sku, attributes, price, compareAtPrice,
            newStock, reservedQuantity, imageUrl, active
        );
    }

    public ProductVariation withReservedStock(int newReserved) {
        return new ProductVariation(
            variationId, sku, attributes, price, compareAtPrice,
            stockQuantity, newReserved, imageUrl, active
        );
    }

    public ProductVariation withPrice(Money newPrice) {
        return new ProductVariation(
            variationId, sku, attributes, newPrice, compareAtPrice,
            stockQuantity, reservedQuantity, imageUrl, active
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductVariation that = (ProductVariation) o;
        return Objects.equals(variationId, that.variationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variationId);
    }

    @Override
    public String toString() {
        return "ProductVariation{" +
                "variationId=" + variationId +
                ", sku='" + sku + '\'' +
                ", attributes=" + attributes +
                ", price=" + price +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID variationId;
        private String sku;
        private Map<String, String> attributes;
        private Money price;
        private Money compareAtPrice;
        private int stockQuantity = 0;
        private int reservedQuantity = 0;
        private String imageUrl;
        private boolean active = true;

        public Builder variationId(UUID variationId) {
            this.variationId = variationId;
            return this;
        }

        public Builder sku(String sku) {
            this.sku = sku;
            return this;
        }

        public Builder attributes(Map<String, String> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder addAttribute(String name, String value) {
            if (this.attributes == null) {
                this.attributes = new HashMap<>();
            }
            this.attributes.put(name, value);
            return this;
        }

        public Builder price(Money price) {
            this.price = price;
            return this;
        }

        public Builder compareAtPrice(Money compareAtPrice) {
            this.compareAtPrice = compareAtPrice;
            return this;
        }

        public Builder stockQuantity(int stockQuantity) {
            this.stockQuantity = stockQuantity;
            return this;
        }

        public Builder reservedQuantity(int reservedQuantity) {
            this.reservedQuantity = reservedQuantity;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public ProductVariation build() {
            if (variationId == null) {
                variationId = UUID.randomUUID();
            }
            if (attributes == null) {
                attributes = new HashMap<>();
            }
            return new ProductVariation(
                variationId, sku, attributes, price, compareAtPrice,
                stockQuantity, reservedQuantity, imageUrl, active
            );
        }
    }
}
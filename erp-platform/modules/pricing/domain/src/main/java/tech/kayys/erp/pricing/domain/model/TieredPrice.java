package tech.kayys.erp.pricing.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.pricing.domain.identifier.TieredPriceId;
import tech.kayys.erp.pricing.domain.valueobject.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tiered Price aggregate root.
 * Implements volume-based pricing with tiers.
 */
public final class TieredPrice extends AggregateRoot<TieredPriceId> {
    
    private static final long serialVersionUID = 1L;
    
    private String productId;
    private String productName;
    private String productSku;
    private List<PriceTier> tiers;
    private String currencyCode;
    private String customerSegment;
    private boolean active;
    private Instant validFrom;
    private Instant validTo;
    private String createdBy;
    private String notes;

    private TieredPrice(TieredPriceId id) {
        super(id);
        this.tiers = new ArrayList<>();
        this.active = true;
    }

    private TieredPrice() {
        super();
    }

    /**
     * Factory method to create a new tiered price.
     */
    public static TieredPrice create(
            TieredPriceId id,
            String productId,
            String productName,
            String currencyCode) {
        TieredPrice tieredPrice = new TieredPrice(id);
        tieredPrice.productId = productId;
        tieredPrice.productName = productName;
        tieredPrice.currencyCode = currencyCode;
        return tieredPrice;
    }

    /**
     * Adds a price tier.
     */
    public void addTier(PriceTier tier) {
        tiers.removeIf(t -> t.getMinQuantity() == tier.getMinQuantity());
        tiers.add(tier);
        tiers.sort((a, b) -> Double.compare(a.getMinQuantity(), b.getMinQuantity()));
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes a price tier.
     */
    public void removeTier(double minQuantity) {
        tiers.removeIf(t -> t.getMinQuantity() == minQuantity);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the price for a given quantity.
     */
    public Money getPriceForQuantity(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        PriceTier applicableTier = null;
        for (PriceTier tier : tiers) {
            if (quantity >= tier.getMinQuantity()) {
                applicableTier = tier;
            }
        }
        
        if (applicableTier == null) {
            throw new IllegalStateException("No applicable tier for quantity: " + quantity);
        }
        
        Money unitPrice = applicableTier.getUnitPrice();
        return unitPrice.multiply(java.math.BigDecimal.valueOf(quantity));
    }

    /**
     * Gets the unit price for a given quantity.
     */
    public Money getUnitPriceForQuantity(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        PriceTier applicableTier = null;
        for (PriceTier tier : tiers) {
            if (quantity >= tier.getMinQuantity()) {
                applicableTier = tier;
            }
        }
        
        if (applicableTier == null) {
            throw new IllegalStateException("No applicable tier for quantity: " + quantity);
        }
        
        return applicableTier.getUnitPrice();
    }

    /**
     * Gets the discount for a given quantity.
     */
    public Money getDiscountForQuantity(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        PriceTier applicableTier = null;
        for (PriceTier tier : tiers) {
            if (quantity >= tier.getMinQuantity()) {
                applicableTier = tier;
            }
        }
        
        if (applicableTier == null) {
            return Money.zero(currencyCode);
        }
        
        Money basePrice = tiers.get(0).getUnitPrice();
        Money tierPrice = applicableTier.getUnitPrice();
        return basePrice.subtract(tierPrice).multiply(java.math.BigDecimal.valueOf(quantity));
    }

    /**
     * Gets the discount percentage for a given quantity.
     */
    public double getDiscountPercentageForQuantity(double quantity) {
        if (quantity <= 0) {
            return 0.0;
        }
        
        PriceTier applicableTier = null;
        for (PriceTier tier : tiers) {
            if (quantity >= tier.getMinQuantity()) {
                applicableTier = tier;
            }
        }
        
        if (applicableTier == null) {
            return 0.0;
        }
        
        Money basePrice = tiers.get(0).getUnitPrice();
        Money tierPrice = applicableTier.getUnitPrice();
        Money discount = basePrice.subtract(tierPrice);
        
        if (basePrice.isZero()) {
            return 0.0;
        }
        
        return discount.getAmount()
            .divide(basePrice.getAmount(), 4, java.math.RoundingMode.HALF_UP)
            .multiply(java.math.BigDecimal.valueOf(100))
            .doubleValue();
    }

    // Getters
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getProductSku() { return productSku; }
    public List<PriceTier> getTiers() { return Collections.unmodifiableList(tiers); }
    public String getCurrencyCode() { return currencyCode; }
    public String getCustomerSegment() { return customerSegment; }
    public boolean isActive() { return active; }
    public Instant getValidFrom() { return validFrom; }
    public Instant getValidTo() { return validTo; }
    public String getCreatedBy() { return createdBy; }
    public String getNotes() { return notes; }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCustomerSegment(String customerSegment) {
        this.customerSegment = customerSegment;
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
        return "TieredPrice{" +
                "id=" + getId() +
                ", productId='" + productId + '\'' +
                ", tiers=" + tiers.size() +
                '}';
    }

    /**
     * Price tier value object.
     */
    public static final class PriceTier implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final double minQuantity;
        private final double maxQuantity;
        private final Money unitPrice;
        private final double discountPercentage;
        private final String description;

        public PriceTier(
                double minQuantity,
                double maxQuantity,
                Money unitPrice,
                double discountPercentage,
                String description) {
            this.minQuantity = minQuantity;
            this.maxQuantity = maxQuantity;
            this.unitPrice = unitPrice;
            this.discountPercentage = discountPercentage;
            this.description = description;
            validate();
        }

        @Override
        public void validate() {
            if (minQuantity < 0) {
                throw new IllegalArgumentException("Min quantity cannot be negative");
            }
            if (maxQuantity < minQuantity) {
                throw new IllegalArgumentException("Max quantity must be >= min quantity");
            }
            if (unitPrice == null || unitPrice.isNegative()) {
                throw new IllegalArgumentException("Unit price must be positive");
            }
        }

        public double getMinQuantity() { return minQuantity; }
        public double getMaxQuantity() { return maxQuantity; }
        public Money getUnitPrice() { return unitPrice; }
        public double getDiscountPercentage() { return discountPercentage; }
        public String getDescription() { return description; }

        public boolean isInRange(double quantity) {
            return quantity >= minQuantity && (maxQuantity == 0 || quantity <= maxQuantity);
        }

        @Override
        public String toString() {
            return "PriceTier{" +
                    "minQuantity=" + minQuantity +
                    ", unitPrice=" + unitPrice +
                    ", discount=" + discountPercentage + "%" +
                    '}';
        }
    }
}
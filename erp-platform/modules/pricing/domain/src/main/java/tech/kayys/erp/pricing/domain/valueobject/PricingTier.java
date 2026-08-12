package tech.kayys.erp.pricing.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Pricing tier for volume-based pricing.
 */
public final class PricingTier implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final int minQuantity;
    private final int maxQuantity; // null means unlimited
    private final BigDecimal discountPercentage;

    public PricingTier(int minQuantity, Integer maxQuantity, BigDecimal discountPercentage) {
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity != null ? maxQuantity : Integer.MAX_VALUE;
        this.discountPercentage = discountPercentage;
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
        if (discountPercentage == null || discountPercentage.signum() < 0 || 
            discountPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
    }

    public int getMinQuantity() { return minQuantity; }
    public int getMaxQuantity() { return maxQuantity; }
    public BigDecimal getDiscountPercentage() { return discountPercentage; }

    public boolean appliesTo(int quantity) {
        return quantity >= minQuantity && quantity <= maxQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PricingTier that = (PricingTier) o;
        return minQuantity == that.minQuantity &&
               maxQuantity == that.maxQuantity &&
               Objects.equals(discountPercentage, that.discountPercentage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minQuantity, maxQuantity, discountPercentage);
    }

    @Override
    public String toString() {
        return "PricingTier{" +
                "minQuantity=" + minQuantity +
                ", maxQuantity=" + (maxQuantity == Integer.MAX_VALUE ? "∞" : maxQuantity) +
                ", discountPercentage=" + discountPercentage + "%" +
                '}';
    }

    public static PricingTier of(int minQuantity, Integer maxQuantity, BigDecimal discountPercentage) {
        return new PricingTier(minQuantity, maxQuantity, discountPercentage);
    }

    public static PricingTier of(int minQuantity, BigDecimal discountPercentage) {
        return new PricingTier(minQuantity, null, discountPercentage);
    }
}
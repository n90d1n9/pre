package tech.kayys.erp.pricing.domain.valueobject;

/**
 * Types of discounts that can be applied.
 */
public enum DiscountType {
    PERCENTAGE("Percentage discount"),
    FIXED_AMOUNT("Fixed amount discount"),
    BUY_X_GET_Y("Buy X get Y free"),
    VOLUME_DISCOUNT("Volume-based discount"),
    SEASONAL("Seasonal discount"),
    COUPON("Coupon code discount"),
    BUNDLE("Bundle discount");

    private final String description;

    DiscountType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
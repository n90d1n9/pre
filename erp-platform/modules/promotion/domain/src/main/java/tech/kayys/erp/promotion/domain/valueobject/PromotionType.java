package tech.kayys.erp.promotion.domain.valueobject;

/**
 * Types of promotions.
 */
public enum PromotionType {
    PERCENTAGE_DISCOUNT("Percentage Discount"),
    FIXED_AMOUNT_DISCOUNT("Fixed Amount Discount"),
    BUY_ONE_GET_ONE("Buy One Get One"),
    BUY_X_GET_Y("Buy X Get Y Free"),
    FREE_SHIPPING("Free Shipping"),
    GIFT_WITH_PURCHASE("Gift With Purchase"),
    BUNDLE("Bundle Discount"),
    COUPON("Coupon Code"),
    VOLUME_DISCOUNT("Volume Discount"),
    SEASONAL("Seasonal Promotion"),
    FLASH_SALE("Flash Sale"),
    LOYALTY_REWARD("Loyalty Reward"),
    REFERRAL("Referral Promotion");

    private final String displayName;

    PromotionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isDiscount() {
        return this == PERCENTAGE_DISCOUNT || this == FIXED_AMOUNT_DISCOUNT || 
               this == VOLUME_DISCOUNT || this == LOYALTY_REWARD;
    }

    public boolean isBogo() {
        return this == BUY_ONE_GET_ONE || this == BUY_X_GET_Y;
    }

    public boolean isShippingRelated() {
        return this == FREE_SHIPPING;
    }
}
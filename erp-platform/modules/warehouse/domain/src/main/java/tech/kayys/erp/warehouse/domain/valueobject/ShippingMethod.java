package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Shipping methods.
 */
public enum ShippingMethod {
    STANDARD("Standard - 5-7 business days"),
    EXPEDITED("Expedited - 2-3 business days"),
    EXPRESS("Express - 1-2 business days"),
    OVERNIGHT("Overnight - next day delivery"),
    SAME_DAY("Same Day - same day delivery"),
    GROUND("Ground - 3-5 business days"),
    AIR("Air - 2-3 business days"),
    FREIGHT("Freight - 3-7 business days");

    private final String description;

    ShippingMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getEstimatedDays() {
        return switch (this) {
            case SAME_DAY -> 1;
            case OVERNIGHT -> 1;
            case EXPRESS -> 2;
            case EXPEDITED -> 3;
            case AIR -> 3;
            case STANDARD -> 7;
            case GROUND -> 5;
            case FREIGHT -> 7;
        };
    }
}
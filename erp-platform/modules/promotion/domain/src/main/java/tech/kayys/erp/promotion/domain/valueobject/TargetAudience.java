package tech.kayys.erp.promotion.domain.valueobject;

/**
 * Target audience for promotions.
 */
public enum TargetAudience {
    ALL_CUSTOMERS("All Customers"),
    NEW_CUSTOMERS("New Customers"),
    RETURNING_CUSTOMERS("Returning Customers"),
    VIP_CUSTOMERS("VIP Customers"),
    LOYALTY_MEMBERS("Loyalty Members"),
    CART_ABANDONERS("Cart Abandoners"),
    SEGMENT_A("Segment A"),
    SEGMENT_B("Segment B"),
    SEGMENT_C("Segment C"),
    BUSINESS_CUSTOMERS("Business Customers"),
    RETAIL_CUSTOMERS("Retail Customers"),
    REGISTERED_ONLY("Registered Only"),
    FIRST_PURCHASE("First Purchase"),
    HIGH_VALUE("High Value Customers");

    private final String displayName;

    TargetAudience(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean matchesCustomer(CustomerSegment segment) {
        return switch (this) {
            case ALL_CUSTOMERS -> true;
            case NEW_CUSTOMERS -> segment == CustomerSegment.NEW;
            case RETURNING_CUSTOMERS -> segment == CustomerSegment.RETURNING;
            case VIP_CUSTOMERS -> segment == CustomerSegment.VIP;
            case LOYALTY_MEMBERS -> segment == CustomerSegment.LOYALTY;
            case HIGH_VALUE -> segment == CustomerSegment.HIGH_VALUE;
            default -> false;
        };
    }

    public enum CustomerSegment {
        NEW, RETURNING, VIP, LOYALTY, HIGH_VALUE, REGULAR
    }
}
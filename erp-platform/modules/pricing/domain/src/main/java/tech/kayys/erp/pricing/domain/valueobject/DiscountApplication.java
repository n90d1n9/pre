package tech.kayys.erp.pricing.domain.valueobject;

/**
 * When the discount should be applied in the pricing calculation.
 */
public enum DiscountApplication {
    BEFORE_TAX("Applied before tax calculation"),
    AFTER_TAX("Applied after tax calculation"),
    AT_CHECKOUT("Applied at checkout only");

    private final String description;

    DiscountApplication(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
package tech.kayys.erp.stockopname.domain.valueobject;

/**
 * Types of counting sessions.
 */
public enum CountingType {
    FULL("Full - all items counted"),
    CATEGORY("Category - specific category only"),
    ZONE("Zone - specific warehouse zone"),
    RANDOM("Random - random sample"),
    TARGETED("Targeted - specific items identified"),
    NEGATIVE_BALANCE("Negative Balance - items with negative stock"),
    ZERO_BALANCE("Zero Balance - items with zero stock");

    private final String description;

    CountingType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
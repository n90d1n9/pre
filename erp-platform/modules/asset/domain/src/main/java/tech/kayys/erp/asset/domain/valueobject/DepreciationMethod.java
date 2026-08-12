package tech.kayys.erp.asset.domain.valueobject;

/**
 * Methods for calculating depreciation.
 */
public enum DepreciationMethod {
    STRAIGHT_LINE("Straight Line - equal amount each period"),
    DECLINING_BALANCE("Declining Balance - accelerated depreciation"),
    DOUBLE_DECLINING("Double Declining Balance - faster depreciation"),
    SUM_OF_YEARS_DIGITS("Sum of Years Digits"),
    UNITS_OF_PRODUCTION("Units of Production - based on usage"),
    MACRS("MACRS - Modified Accelerated Cost Recovery System");

    private final String description;

    DepreciationMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAccelerated() {
        return this == DECLINING_BALANCE || this == DOUBLE_DECLINING || this == SUM_OF_YEARS_DIGITS;
    }
}
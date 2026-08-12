package tech.kayys.erp.tenant.domain.valueobject;

/**
 * Subscription plan types for tenants.
 */
public enum PlanType {
    FREE("Free - limited features"),
    BASIC("Basic - essential features"),
    PROFESSIONAL("Professional - advanced features"),
    ENTERPRISE("Enterprise - full features"),
    CUSTOM("Custom - tailored solution"),
    TRIAL("Trial - free trial period");

    private final String description;

    PlanType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasMultiCurrency() {
        return this == PROFESSIONAL || this == ENTERPRISE || this == CUSTOM;
    }

    public boolean hasMultiCompany() {
        return this == ENTERPRISE || this == CUSTOM;
    }

    public boolean hasApiAccess() {
        return this != FREE && this != TRIAL;
    }

    public int getMaxUsers() {
        return switch (this) {
            case FREE -> 5;
            case BASIC -> 10;
            case PROFESSIONAL -> 50;
            case ENTERPRISE -> Integer.MAX_VALUE;
            case CUSTOM -> Integer.MAX_VALUE;
            case TRIAL -> 10;
        };
    }

    public int getMaxCompanies() {
        return switch (this) {
            case FREE -> 1;
            case BASIC -> 1;
            case PROFESSIONAL -> 3;
            case ENTERPRISE -> Integer.MAX_VALUE;
            case CUSTOM -> Integer.MAX_VALUE;
            case TRIAL -> 1;
        };
    }
}
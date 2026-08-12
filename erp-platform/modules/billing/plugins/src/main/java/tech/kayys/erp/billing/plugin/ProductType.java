package tech.kayys.erp.billing.plugin;

/**
 * Product types supported by the billing platform.
 */
public enum ProductType {
    // SaaS Products
    SAAS_SUBSCRIPTION("SaaS Subscription"),
    SAAS_USAGE_BASED("SaaS Usage-Based"),
    SAAS_TIERED("SaaS Tiered Pricing"),
    
    // PaaS Products
    PAAS_PLATFORM("PaaS Platform"),
    PAAS_RESOURCES("PaaS Resources"),
    PAAS_CONTAINERS("PaaS Containers"),
    
    // Digital Products
    DIGITAL_DOWNLOAD("Digital Download"),
    DIGITAL_STREAMING("Digital Streaming"),
    DIGITAL_LICENSE("Digital License"),
    DIGITAL_SOFTWARE("Digital Software"),
    
    // E-Commerce
    PHYSICAL_GOODS("Physical Goods"),
    DIGITAL_GOODS("Digital Goods"),
    SUBSCRIPTION_BOX("Subscription Box"),
    
    // Services
    PROFESSIONAL_SERVICES("Professional Services"),
    CONSULTING_SERVICES("Consulting Services"),
    MAINTENANCE_SERVICES("Maintenance Services"),
    SUPPORT_SERVICES("Support Services"),
    
    // Platform
    PLATFORM_FEE("Platform Fee"),
    MARKETPLACE_FEE("Marketplace Fee"),
    COMMISSION("Commission"),
    
    // Hybrid
    BUNDLE("Bundle/Kit"),
    FREEMIUM("Freemium"),
    TRIAL("Trial"),
    
    // Custom
    CUSTOM("Custom Product Type");

    private final String displayName;

    ProductType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isSubscription() {
        return this == SAAS_SUBSCRIPTION || this == SAAS_USAGE_BASED || 
               this == SAAS_TIERED || this == SUBSCRIPTION_BOX;
    }

    public boolean isDigital() {
        return this == DIGITAL_DOWNLOAD || this == DIGITAL_STREAMING || 
               this == DIGITAL_LICENSE || this == DIGITAL_SOFTWARE || 
               this == DIGITAL_GOODS;
    }

    public boolean isPhysical() {
        return this == PHYSICAL_GOODS;
    }

    public boolean isService() {
        return this == PROFESSIONAL_SERVICES || this == CONSULTING_SERVICES || 
               this == MAINTENANCE_SERVICES || this == SUPPORT_SERVICES;
    }

    public boolean isPlatform() {
        return this == PLATFORM_FEE || this == MARKETPLACE_FEE || this == COMMISSION;
    }
}
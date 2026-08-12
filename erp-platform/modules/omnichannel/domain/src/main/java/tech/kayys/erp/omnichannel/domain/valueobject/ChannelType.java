package tech.kayys.erp.omnichannel.domain.valueobject;

/**
 * Types of sales channels.
 */
public enum ChannelType {
    POS("Point of Sale - Physical Store"),
    KIOSK("Self-Service Kiosk"),
    ECOMMERCE("E-commerce Website"),
    MOBILE_APP("Mobile Application"),
    MARKETPLACE("Third-party Marketplace"),
    SOCIAL_COMMERCE("Social Media Commerce"),
    WHOLESALE("Wholesale/B2B"),
    CATALOG("Catalog/Phone Order"),
    QR_CODE("QR Code Ordering"),
    CURBSIDE("Curbside Pickup");

    private final String description;

    ChannelType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPhysical() {
        return this == POS || this == KIOSK || this == CURBSIDE;
    }

    public boolean isDigital() {
        return this == ECOMMERCE || this == MOBILE_APP || this == SOCIAL_COMMERCE;
    }

    public boolean isThirdParty() {
        return this == MARKETPLACE;
    }
}
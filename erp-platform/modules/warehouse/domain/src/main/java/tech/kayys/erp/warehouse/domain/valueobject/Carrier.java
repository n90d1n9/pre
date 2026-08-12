package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Shipping carriers.
 */
public enum Carrier {
    FEDEX("FedEx"),
    UPS("UPS"),
    DHL("DHL"),
    USPS("USPS"),
    AMAZON("Amazon Logistics"),
    ONTRAC("OnTrac"),
    LASERSHIP("LaserShip"),
    ROYAL_MAIL("Royal Mail"),
    CANADA_POST("Canada Post"),
    AUSTRALIA_POST("Australia Post"),
    OTHER("Other");

    private final String displayName;

    Carrier(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isInternational() {
        return this == DHL || this == ROYAL_MAIL || this == CANADA_POST || this == AUSTRALIA_POST;
    }
}
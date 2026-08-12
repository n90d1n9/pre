package tech.kayys.erp.omnichannel.domain.valueobject;

/**
 * Fulfillment methods across channels.
 */
public enum FulfillmentMethod {
    STORE_PICKUP("Store Pickup"),
    SHIP_TO_HOME("Ship to Home"),
    SHIP_TO_STORE("Ship to Store"),
    CURBSIDE_PICKUP("Curbside Pickup"),
    LOCKER_PICKUP("Locker Pickup"),
    THIRD_PARTY("Third-party Delivery"),
    INSTORE_PICKUP("In-store Pickup"),
    DIGITAL_DELIVERY("Digital Delivery"),
    DROP_SHIP("Drop Ship");

    private final String description;

    FulfillmentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPickup() {
        return this == STORE_PICKUP || this == CURBSIDE_PICKUP || 
               this == LOCKER_PICKUP || this == INSTORE_PICKUP;
    }

    public boolean isDelivery() {
        return this == SHIP_TO_HOME || this == SHIP_TO_STORE || 
               this == THIRD_PARTY || this == DROP_SHIP;
    }

    public boolean isDigital() {
        return this == DIGITAL_DELIVERY;
    }
}
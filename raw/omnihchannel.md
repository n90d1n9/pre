# Complete Implementation: Omnichannel Bounded Context

Now I'll implement the Omnichannel (Unified Commerce) bounded context, which orchestrates all channels (POS, Kiosk, E-commerce, Mobile, Marketplace) into a single unified experience. This is the "brain" that ensures consistent customer experience, inventory visibility, and order management across all touchpoints.

## Overview of Omnichannel Module

Omnichannel commerce is the foundation for modern retail, providing:
- **Unified customer profile** across all channels
- **Real-time inventory visibility** (store, warehouse, DC)  
- **Cross-channel fulfillment** (BOPIS, ship-from-store, etc.)
- **Consistent pricing & promotions** across channels
- **Order orchestration** from any channel to any fulfillment location
- **Customer journey tracking** across touchpoints
- **Channel analytics & attribution**

## 1. Omnichannel Domain Module

**`/modules/omnichannel/domain/pom.xml`**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>tech.kayys.erp</groupId>
        <artifactId>erp-platform</artifactId>
        <version>0.1.0-SNAPSHOT</version>
        <relativePath>../../../pom.xml</relativePath>
    </parent>

    <artifactId>erp-omnichannel-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-sales-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-inventory-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-customer-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/omnichannel/domain/src/main/java/tech/kayys/erp/omnichannel/domain/identifier/ChannelId.java`**:

```java
package tech.kayys.erp.omnichannel.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Channel identifier.
 */
public final class ChannelId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ChannelId(UUID value) {
        super(value);
    }

    public static ChannelId of(UUID value) {
        return new ChannelId(value);
    }

    public static ChannelId generate() {
        return new ChannelId(UUID.randomUUID());
    }

    public static ChannelId fromString(String value) {
        return new ChannelId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ChannelId{" + value + "}";
    }
}
```

**`/modules/omnichannel/domain/src/main/java/tech/kayys/erp/omnichannel/domain/identifier/OmniOrderId.java`**:

```java
package tech.kayys.erp.omnichannel.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Omnichannel order identifier.
 */
public final class OmniOrderId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public OmniOrderId(UUID value) {
        super(value);
    }

    public static OmniOrderId of(UUID value) {
        return new OmniOrderId(value);
    }

    public static OmniOrderId generate() {
        return new OmniOrderId(UUID.randomUUID());
    }

    public static OmniOrderId fromString(String value) {
        return new OmniOrderId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "OmniOrderId{" + value + "}";
    }
}
```

**`/modules/omnichannel/domain/src/main/java/tech/kayys/erp/omnichannel/domain/valueobject/ChannelType.java`**:

```java
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
```

**`/modules/omnichannel/domain/src/main/java/tech/kayys/erp/omnichannel/domain/valueobject/FulfillmentMethod.java`**:

```java
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
```

**`/modules/omnichannel/domain/src/main/java/tech/kayys/erp/omnichannel/domain/valueobject/InventoryVisibility.java`**:

```java
package tech.kayys.erp.omnichannel.domain.valueobject;

/**
 * Inventory visibility levels across channels.
 */
public enum InventoryVisibility {
    GLOBAL("Global - All channels can see"),
    CHANNEL_SPECIFIC("Channel Specific - Only certain channels"),
    STORE_ONLY("Store Only - Only physical store"),
    ONLINE_ONLY("Online Only - Only digital channels"),
    HIDDEN("Hidden - Not visible");

    private final String description;

    InventoryVisibility(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

**`/modules/omnichannel/domain/src/main/java/tech/kayys/erp/omnichannel/domain/model/Channel.java`**:

```java
package tech.kayys.erp.omnichannel.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.omnichannel.domain.identifier.ChannelId;
import tech.kayys.erp.omnichannel.domain.valueobject.ChannelType;
import tech.kayys.erp.omnichannel.domain.valueobject.FulfillmentMethod;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Channel aggregate root.
 * Represents a sales channel in the omnichannel ecosystem.
 */
public final class Channel extends AggregateRoot<ChannelId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String code;
    private ChannelType channelType;
    private String storeId;
    private String region;
    private List<String> languages;
    private String currencyCode;
    private boolean active;
    private boolean isBopisEnabled;
    private boolean isDeliveryEnabled;
    private boolean isCurbsideEnabled;
    private List<FulfillmentMethod> fulfillmentMethods;
    private List<ChannelInventory> inventorySettings;
    private ChannelSettings settings;
    private String createdBy;
    private String updatedBy;

    private Channel(ChannelId id) {
        super(id);
        this.languages = new ArrayList<>();
        this.fulfillmentMethods = new ArrayList<>();
        this.inventorySettings = new ArrayList<>();
        this.active = true;
        this.settings = ChannelSettings.defaultSettings();
        this.languages.add("en");
    }

    private Channel() {
        super();
    }

    /**
     * Factory method to create a new channel.
     */
    public static Channel create(
            ChannelId id,
            String name,
            String code,
            ChannelType channelType,
            String currencyCode) {
        Channel channel = new Channel(id);
        channel.name = name;
        channel.code = code;
        channel.channelType = channelType;
        channel.currencyCode = currencyCode;
        return channel;
    }

    /**
     * Adds a fulfillment method to the channel.
     */
    public void addFulfillmentMethod(FulfillmentMethod method) {
        if (!fulfillmentMethods.contains(method)) {
            fulfillmentMethods.add(method);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a fulfillment method from the channel.
     */
    public void removeFulfillmentMethod(FulfillmentMethod method) {
        fulfillmentMethods.remove(method);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds inventory visibility setting for a product/location.
     */
    public void addInventorySetting(ChannelInventory setting) {
        inventorySettings.add(setting);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Enables BOPIS (Buy Online, Pickup In Store).
     */
    public void enableBopis() {
        this.isBopisEnabled = true;
        addFulfillmentMethod(FulfillmentMethod.STORE_PICKUP);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Disables BOPIS.
     */
    public void disableBopis() {
        this.isBopisEnabled = false;
        removeFulfillmentMethod(FulfillmentMethod.STORE_PICKUP);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Enables curbside pickup.
     */
    public void enableCurbside() {
        this.isCurbsideEnabled = true;
        addFulfillmentMethod(FulfillmentMethod.CURBSIDE_PICKUP);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Disables curbside pickup.
     */
    public void disableCurbside() {
        this.isCurbsideEnabled = false;
        removeFulfillmentMethod(FulfillmentMethod.CURBSIDE_PICKUP);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the channel.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the channel.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if a fulfillment method is available.
     */
    public boolean supportsFulfillment(FulfillmentMethod method) {
        return fulfillmentMethods.contains(method);
    }

    // Getters and Setters
    public String getName() { return name; }
    public String getCode() { return code; }
    public ChannelType getChannelType() { return channelType; }
    public String getStoreId() { return storeId; }
    public String getRegion() { return region; }
    public List<String> getLanguages() { return Collections.unmodifiableList(languages); }
    public String getCurrencyCode() { return currencyCode; }
    public boolean isActive() { return active; }
    public boolean isBopisEnabled() { return isBopisEnabled; }
    public boolean isDeliveryEnabled() { return isDeliveryEnabled; }
    public boolean isCurbsideEnabled() { return isCurbsideEnabled; }
    public List<FulfillmentMethod> getFulfillmentMethods() { return Collections.unmodifiableList(fulfillmentMethods); }
    public List<ChannelInventory> getInventorySettings() { return Collections.unmodifiableList(inventorySettings); }
    public ChannelSettings getSettings() { return settings; }
    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setRegion(String region) {
        this.region = region;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setLanguages(List<String> languages) {
        this.languages = new ArrayList<>(languages);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setSettings(ChannelSettings settings) {
        this.settings = settings;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", channelType=" + channelType +
                ", active=" + active +
                '}';
    }

    /**
     * Channel settings value object.
     */
    public static final class ChannelSettings implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final boolean requiresCustomerLogin;
        private final boolean allowsGuestCheckout;
        private final boolean requiresAgeVerification;
        private final int maxItemsPerOrder;
        private final boolean allowsCancellations;
        private final int cancellationWindowHours;
        private final boolean allowsReturns;
        private final int returnWindowDays;
        private final boolean requiresSignature;
        private final boolean collectEmail;
        private final boolean collectPhone;
        private final boolean collectAddress;
        private final boolean sendOrderConfirmation;
        private final boolean sendShippingConfirmation;

        public ChannelSettings(
                boolean requiresCustomerLogin,
                boolean allowsGuestCheckout,
                boolean requiresAgeVerification,
                int maxItemsPerOrder,
                boolean allowsCancellations,
                int cancellationWindowHours,
                boolean allowsReturns,
                int returnWindowDays,
                boolean requiresSignature,
                boolean collectEmail,
                boolean collectPhone,
                boolean collectAddress,
                boolean sendOrderConfirmation,
                boolean sendShippingConfirmation) {
            this.requiresCustomerLogin = requiresCustomerLogin;
            this.allowsGuestCheckout = allowsGuestCheckout;
            this.requiresAgeVerification = requiresAgeVerification;
            this.maxItemsPerOrder = maxItemsPerOrder;
            this.allowsCancellations = allowsCancellations;
            this.cancellationWindowHours = cancellationWindowHours;
            this.allowsReturns = allowsReturns;
            this.returnWindowDays = returnWindowDays;
            this.requiresSignature = requiresSignature;
            this.collectEmail = collectEmail;
            this.collectPhone = collectPhone;
            this.collectAddress = collectAddress;
            this.sendOrderConfirmation = sendOrderConfirmation;
            this.sendShippingConfirmation = sendShippingConfirmation;
            validate();
        }

        @Override
        public void validate() {
            if (maxItemsPerOrder < 1) {
                throw new IllegalArgumentException("Max items per order must be at least 1");
            }
            if (cancellationWindowHours < 0) {
                throw new IllegalArgumentException("Cancellation window cannot be negative");
            }
            if (returnWindowDays < 0) {
                throw new IllegalArgumentException("Return window cannot be negative");
            }
        }

        // Getters
        public boolean isRequiresCustomerLogin() { return requiresCustomerLogin; }
        public boolean isAllowsGuestCheckout() { return allowsGuestCheckout; }
        public boolean isRequiresAgeVerification() { return requiresAgeVerification; }
        public int getMaxItemsPerOrder() { return maxItemsPerOrder; }
        public boolean isAllowsCancellations() { return allowsCancellations; }
        public int getCancellationWindowHours() { return cancellationWindowHours; }
        public boolean isAllowsReturns() { return allowsReturns; }
        public int getReturnWindowDays() { return returnWindowDays; }
        public boolean isRequiresSignature() { return requiresSignature; }
        public boolean isCollectEmail() { return collectEmail; }
        public boolean isCollectPhone() { return collectPhone; }
        public boolean isCollectAddress() { return collectAddress; }
        public boolean isSendOrderConfirmation() { return sendOrderConfirmation; }
        public boolean isSendShippingConfirmation() { return sendShippingConfirmation; }

        public static ChannelSettings defaultSettings() {
            return new ChannelSettings(
                false,  // requiresCustomerLogin
                true,   // allowsGuestCheckout
                false,  // requiresAgeVerification
                100,    // maxItemsPerOrder
                true,   // allowsCancellations
                24,     // cancellationWindowHours
                true,   // allowsReturns
                30,     // returnWindowDays
                false,  // requiresSignature
                true,   // collectEmail
                true,   // collectPhone
                true,   // collectAddress
                true,   // sendOrderConfirmation
                true    // sendShippingConfirmation
            );
        }
    }

    /**
     * Channel inventory setting.
     */
    public static final class ChannelInventory implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String productId;
        private final String locationId;
        private final int visibleQuantity;
        private final int reservedQuantity;
        private final InventoryVisibility visibility;

        public ChannelInventory(
                String productId,
                String locationId,
                int visibleQuantity,
                int reservedQuantity,
                InventoryVisibility visibility) {
            this.productId = productId;
            this.locationId = locationId;
            this.visibleQuantity = visibleQuantity;
            this.reservedQuantity = reservedQuantity;
            this.visibility = visibility;
            validate();
        }

        @Override
        public void validate() {
            if (productId == null || productId.trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be empty");
            }
            if (visibleQuantity < 0) {
                throw new IllegalArgumentException("Visible quantity cannot be negative");
            }
            if (reservedQuantity < 0) {
                throw new IllegalArgumentException("Reserved quantity cannot be negative");
            }
            if (visibility == null) {
                throw new IllegalArgumentException("Visibility cannot be null");
            }
        }

        public String getProductId() { return productId; }
        public String getLocationId() { return locationId; }
        public int getVisibleQuantity() { return visibleQuantity; }
        public int getReservedQuantity() { return reservedQuantity; }
        public InventoryVisibility getVisibility() { return visibility; }
        public int getAvailableQuantity() { return visibleQuantity - reservedQuantity; }

        @Override
        public String toString() {
            return "ChannelInventory{" +
                    "productId='" + productId + '\'' +
                    ", available=" + getAvailableQuantity() +
                    ", visibility=" + visibility +
                    '}';
        }
    }
}
```

**`/modules/omnichannel/domain/src/main/java/tech/kayys/erp/omnichannel/domain/model/OmniOrder.java`**:

```java
package tech.kayys.erp.omnichannel.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.omnichannel.domain.identifier.ChannelId;
import tech.kayys.erp.omnichannel.domain.identifier.OmniOrderId;
import tech.kayys.erp.omnichannel.domain.valueobject.FulfillmentMethod;
import tech.kayys.erp.sales.domain.valueobject.OrderStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Omnichannel Order aggregate root.
 * Unified order from any channel with cross-channel fulfillment.
 */
public final class OmniOrder extends AggregateRoot<OmniOrderId> {
    
    private static final long serialVersionUID = 1L;
    
    private String orderNumber;
    private UUID salesOrderId; // Link to core Sales order
    private ChannelId channelId;
    private String channelName;
    private String customerId;
    private String customerEmail;
    private String customerPhone;
    private List<OmniOrderItem> items;
    private FulfillmentMethod fulfillmentMethod;
    private String sourceStoreId;
    private String fulfillmentStoreId;
    private String shippingAddress;
    private String billingAddress;
    private OrderStatus status;
    private String paymentStatus;
    private String paymentMethod;
    private String transactionId;
    private boolean isPickupOrder;
    private boolean isDeliveryOrder;
    private boolean isDigitalOrder;
    private String pickupLocation;
    private Instant pickupReadyAt;
    private Instant pickupCompletedAt;
    private String trackingNumber;
    private String shippingCarrier;
    private Instant shippedAt;
    private Instant deliveredAt;
    private String notes;
    private String createdBy;
    private List<OmniOrderEvent> events;

    private OmniOrder(OmniOrderId id) {
        super(id);
        this.items = new ArrayList<>();
        this.events = new ArrayList<>();
        this.status = OrderStatus.DRAFT;
        this.paymentStatus = "PENDING";
        this.isPickupOrder = false;
        this.isDeliveryOrder = false;
        this.isDigitalOrder = false;
    }

    private OmniOrder() {
        super();
    }

    /**
     * Factory method to create a new omnichannel order.
     */
    public static OmniOrder create(
            OmniOrderId id,
            String orderNumber,
            UUID salesOrderId,
            ChannelId channelId,
            String customerId) {
        OmniOrder order = new OmniOrder(id);
        order.orderNumber = orderNumber;
        order.salesOrderId = salesOrderId;
        order.channelId = channelId;
        order.customerId = customerId;
        return order;
    }

    /**
     * Adds an item to the order.
     */
    public void addItem(OmniOrderItem item) {
        items.add(item);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Submits the order for fulfillment.
     */
    public void submit() {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot submit order in status: " + status);
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Order must have at least one item");
        }
        this.status = OrderStatus.SUBMITTED;
        addEvent("Order Submitted", "Order submitted for processing");
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Allocates inventory for the order.
     */
    public void allocateInventory(String fulfillmentStoreId) {
        if (status != OrderStatus.SUBMITTED && status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot allocate inventory in status: " + status);
        }
        this.fulfillmentStoreId = fulfillmentStoreId;
        this.status = OrderStatus.CONFIRMED;
        addEvent("Inventory Allocated", "Inventory allocated at store: " + fulfillmentStoreId);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Picks the order (for pickup/delivery).
     */
    public void pick() {
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot pick order in status: " + status);
        }
        this.status = OrderStatus.PROCESSING;
        addEvent("Order Picked", "Items picked for fulfillment");
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Packages the order.
     */
    public void packageOrder() {
        if (status != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Cannot package order in status: " + status);
        }
        this.status = OrderStatus.SHIPPED;
        addEvent("Order Packaged", "Items packaged for shipment/pickup");
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Ships the order (for delivery).
     */
    public void ship(String trackingNumber, String carrier) {
        if (status != OrderStatus.SHIPPED && status != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Cannot ship order in status: " + status);
        }
        if (!isDeliveryOrder && !isPickupOrder) {
            throw new IllegalStateException("Cannot ship non-delivery/pickup order");
        }
        this.trackingNumber = trackingNumber;
        this.shippingCarrier = carrier;
        this.shippedAt = Instant.now();
        this.status = OrderStatus.SHIPPED;
        addEvent("Order Shipped", "Order shipped via " + carrier + " | Tracking: " + trackingNumber);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Makes the order ready for pickup.
     */
    public void readyForPickup() {
        if (status != OrderStatus.SHIPPED && status != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Cannot mark ready for pickup in status: " + status);
        }
        if (!isPickupOrder) {
            throw new IllegalStateException("Order is not a pickup order");
        }
        this.pickupReadyAt = Instant.now();
        this.status = OrderStatus.SHIPPED;
        addEvent("Ready for Pickup", "Order is ready for customer pickup");
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Completes the pickup.
     */
    public void completePickup() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Cannot complete pickup in status: " + status);
        }
        if (!isPickupOrder) {
            throw new IllegalStateException("Order is not a pickup order");
        }
        this.pickupCompletedAt = Instant.now();
        this.status = OrderStatus.DELIVERED;
        addEvent("Pickup Completed", "Customer picked up the order");
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Delivers the order (for delivery).
     */
    public void deliver() {
        if (status != OrderStatus.SHIPPED && status != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Cannot deliver order in status: " + status);
        }
        if (!isDeliveryOrder) {
            throw new IllegalStateException("Order is not a delivery order");
        }
        this.deliveredAt = Instant.now();
        this.status = OrderStatus.DELIVERED;
        addEvent("Order Delivered", "Order delivered successfully");
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the order.
     */
    public void cancel(String reason) {
        if (status == OrderStatus.DELIVERED || status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed order");
        }
        this.status = OrderStatus.CANCELLED;
        this.notes = reason;
        addEvent("Order Cancelled", "Reason: " + reason);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds an event to the order history.
     */
    private void addEvent(String action, String details) {
        OmniOrderEvent event = new OmniOrderEvent(
            UUID.randomUUID().toString(),
            action,
            details,
            Instant.now()
        );
        events.add(event);
    }

    /**
     * Gets the fulfillment status summary.
     */
    public FulfillmentStatus getFulfillmentStatus() {
        if (status == OrderStatus.DRAFT) return FulfillmentStatus.PENDING;
        if (status == OrderStatus.SUBMITTED) return FulfillmentStatus.PROCESSING;
        if (status == OrderStatus.CONFIRMED) return FulfillmentStatus.ALLOCATED;
        if (status == OrderStatus.PROCESSING) return FulfillmentStatus.PICKING;
        if (status == OrderStatus.SHIPPED) {
            if (isPickupOrder) {
                return FulfillmentStatus.READY_FOR_PICKUP;
            }
            return FulfillmentStatus.SHIPPED;
        }
        if (status == OrderStatus.DELIVERED) {
            if (isPickupOrder) {
                return FulfillmentStatus.PICKED_UP;
            }
            return FulfillmentStatus.DELIVERED;
        }
        if (status == OrderStatus.CANCELLED) return FulfillmentStatus.CANCELLED;
        if (status == OrderStatus.COMPLETED) return FulfillmentStatus.COMPLETED;
        return FulfillmentStatus.PENDING;
    }

    // Getters and Setters
    public String getOrderNumber() { return orderNumber; }
    public UUID getSalesOrderId() { return salesOrderId; }
    public ChannelId getChannelId() { return channelId; }
    public String getChannelName() { return channelName; }
    public String getCustomerId() { return customerId; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerPhone() { return customerPhone; }
    public List<OmniOrderItem> getItems() { return Collections.unmodifiableList(items); }
    public FulfillmentMethod getFulfillmentMethod() { return fulfillmentMethod; }
    public String getSourceStoreId() { return sourceStoreId; }
    public String getFulfillmentStoreId() { return fulfillmentStoreId; }
    public String getShippingAddress() { return shippingAddress; }
    public String getBillingAddress() { return billingAddress; }
    public OrderStatus getStatus() { return status; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getTransactionId() { return transactionId; }
    public boolean isPickupOrder() { return isPickupOrder; }
    public boolean isDeliveryOrder() { return isDeliveryOrder; }
    public boolean isDigitalOrder() { return isDigitalOrder; }
    public String getPickupLocation() { return pickupLocation; }
    public Instant getPickupReadyAt() { return pickupReadyAt; }
    public Instant getPickupCompletedAt() { return pickupCompletedAt; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getShippingCarrier() { return shippingCarrier; }
    public Instant getShippedAt() { return shippedAt; }
    public Instant getDeliveredAt() { return deliveredAt; }
    public String getNotes() { return notes; }
    public String getCreatedBy() { return createdBy; }
    public List<OmniOrderEvent> getEvents() { return Collections.unmodifiableList(events); }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setFulfillmentMethod(FulfillmentMethod fulfillmentMethod) {
        this.fulfillmentMethod = fulfillmentMethod;
        this.isPickupOrder = fulfillmentMethod.isPickup();
        this.isDeliveryOrder = fulfillmentMethod.isDelivery();
        this.isDigitalOrder = fulfillmentMethod.isDigital();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setSourceStoreId(String sourceStoreId) {
        this.sourceStoreId = sourceStoreId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "OmniOrder{" +
                "id=" + getId() +
                ", orderNumber='" + orderNumber + '\'' +
                ", channelName='" + channelName + '\'' +
                ", status=" + status +
                ", fulfillmentMethod=" + fulfillmentMethod +
                '}';
    }

    /**
     * Fulfillment status enum.
     */
    public enum FulfillmentStatus {
        PENDING("Pending"),
        PROCESSING("Processing"),
        ALLOCATED("Allocated"),
        PICKING("Picking"),
        READY_FOR_PICKUP("Ready for Pickup"),
        SHIPPED("Shipped"),
        DELIVERED("Delivered"),
        PICKED_UP("Picked Up"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled");

        private final String description;

        FulfillmentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * OmniOrder item value object.
     */
    public static final class OmniOrderItem implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String productId;
        private final String productName;
        private final String sku;
        private final int quantity;
        private final double weight;
        private final String price;
        private final String currencyCode;
        private final String variationId;
        private final boolean isWeighted;

        public OmniOrderItem(
                String productId,
                String productName,
                String sku,
                int quantity,
                double weight,
                String price,
                String currencyCode,
                String variationId,
                boolean isWeighted) {
            this.productId = productId;
            this.productName = productName;
            this.sku = sku;
            this.quantity = quantity;
            this.weight = weight;
            this.price = price;
            this.currencyCode = currencyCode;
            this.variationId = variationId;
            this.isWeighted = isWeighted;
            validate();
        }

        @Override
        public void validate() {
            if (productId == null || productId.trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be empty");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (isWeighted && weight <= 0) {
                throw new IllegalArgumentException("Weight must be positive for weighted items");
            }
        }

        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getSku() { return sku; }
        public int getQuantity() { return quantity; }
        public double getWeight() { return weight; }
        public String getPrice() { return price; }
        public String getCurrencyCode() { return currencyCode; }
        public String getVariationId() { return variationId; }
        public boolean isWeighted() { return isWeighted; }
        public double getTotalWeight() { return weight * quantity; }

        @Override
        public String toString() {
            return "OmniOrderItem{" +
                    "productName='" + productName + '\'' +
                    ", quantity=" + quantity +
                    ", price=" + price +
                    '}';
        }
    }

    /**
     * OmniOrder event record.
     */
    public static final class OmniOrderEvent implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String eventId;
        private final String action;
        private final String details;
        private final Instant timestamp;

        public OmniOrderEvent(String eventId, String action, String details, Instant timestamp) {
            this.eventId = eventId;
            this.action = action;
            this.details = details;
            this.timestamp = timestamp;
            validate();
        }

        @Override
        public void validate() {
            if (eventId == null || eventId.trim().isEmpty()) {
                throw new IllegalArgumentException("Event ID cannot be empty");
            }
            if (action == null || action.trim().isEmpty()) {
                throw new IllegalArgumentException("Action cannot be empty");
            }
        }

        public String getEventId() { return eventId; }
        public String getAction() { return action; }
        public String getDetails() { return details; }
        public Instant getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return "OmniOrderEvent{" +
                    "action='" + action + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}
```

## 2. Omnichannel Application Module

**`/modules/omnichannel/application/src/main/java/tech/kayys/erp/omnichannel/application/api/OmnichannelService.java`**:

```java
package tech.kayys.erp.omnichannel.application.api;

import tech.kayys.erp.omnichannel.application.api.command.*;
import tech.kayys.erp.omnichannel.application.api.query.*;
import tech.kayys.erp.omnichannel.domain.identifier.ChannelId;
import tech.kayys.erp.omnichannel.domain.identifier.OmniOrderId;

import java.util.concurrent.CompletionStage;

/**
 * Public API for omnichannel operations.
 */
public interface OmnichannelService {

    // ============ Channel Operations ============

    /**
     * Registers a new channel.
     */
    CompletionStage<ChannelId> registerChannel(RegisterChannelCommand command);

    /**
     * Updates channel settings.
     */
    CompletionStage<ChannelId> updateChannel(UpdateChannelCommand command);

    /**
     * Gets channel details.
     */
    CompletionStage<ChannelView> getChannel(ChannelId channelId);

    /**
     * Gets all channels.
     */
    CompletionStage<List<ChannelView>> getAllChannels();

    // ============ Inventory Visibility ============

    /**
     * Gets inventory visibility across channels.
     */
    CompletionStage<InventoryVisibilityView> getInventoryVisibility(InventoryVisibilityQuery query);

    /**
     * Updates inventory visibility for a product.
     */
    CompletionStage<Void> updateInventoryVisibility(UpdateInventoryVisibilityCommand command);

    // ============ Order Operations ============

    /**
     * Creates an omnichannel order.
     */
    CompletionStage<OmniOrderId> createOmniOrder(CreateOmniOrderCommand command);

    /**
     * Gets order details.
     */
    CompletionStage<OmniOrderView> getOmniOrder(OmniOrderId orderId);

    /**
     * Searches orders across channels.
     */
    CompletionStage<OmniOrderSearchResult> searchOmniOrders(SearchOmniOrdersQuery query);

    /**
     * Updates order status.
     */
    CompletionStage<OmniOrderId> updateOmniOrderStatus(UpdateOmniOrderStatusCommand command);

    /**
     * Cancels an omnichannel order.
     */
    CompletionStage<OmniOrderId> cancelOmniOrder(CancelOmniOrderCommand command);

    // ============ Fulfillment Operations ============

    /**
     * Finds the best fulfillment location for an order.
     */
    CompletionStage<FulfillmentRecommendation> findFulfillmentLocation(
        FindFulfillmentLocationCommand command
    );

    /**
     * Allocates inventory for an order.
     */
    CompletionStage<OmniOrderId> allocateInventory(AllocateInventoryCommand command);

    /**
     * Marks order as ready for pickup.
     */
    CompletionStage<OmniOrderId> readyForPickup(ReadyForPickupCommand command);

    /**
     * Completes pickup.
     */
    CompletionStage<OmniOrderId> completePickup(CompletePickupCommand command);

    /**
     * Ships an order.
     */
    CompletionStage<OmniOrderId> shipOrder(ShipOrderCommand command);

    /**
     * Delivers an order.
     */
    CompletionStage<OmniOrderId> deliverOrder(DeliverOrderCommand command);

    // ============ Analytics ============

    /**
     * Gets channel analytics.
     */
    CompletionStage<ChannelAnalytics> getChannelAnalytics(ChannelAnalyticsQuery query);

    /**
     * Gets cross-channel customer journey.
     */
    CompletionStage<CustomerJourneyView> getCustomerJourney(String customerId);
}
```

**`/modules/omnichannel/application/src/main/java/tech/kayys/erp/omnichannel/application/api/query/InventoryVisibilityView.java`**:

```java
package tech.kayys.erp.omnichannel.application.api.query;

import java.util.List;

/**
 * Inventory visibility across channels view.
 */
public record InventoryVisibilityView(
        String productId,
        String productName,
        List<LocationInventory> locations,
        boolean availableOnline,
        boolean availableInStore,
        String nearestStore,
        int distanceMiles
) {

    public record LocationInventory(
            String locationId,
            String locationName,
            String locationType, // STORE, WAREHOUSE, DC
            int availableQuantity,
            int reservedQuantity,
            String availabilityStatus, // IN_STOCK, LOW_STOCK, OUT_OF_STOCK
            boolean isVisible
    ) {}
}
```

## 3. Omnichannel REST API

**`/modules/omnichannel/interfaces/src/main/java/tech/kayys/erp/omnichannel/interfaces/rest/OmnichannelResource.java`**:

```java
package tech.kayys.erp.omnichannel.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.omnichannel.application.api.OmnichannelService;
import tech.kayys.erp.omnichannel.application.api.command.*;
import tech.kayys.erp.omnichannel.domain.identifier.ChannelId;
import tech.kayys.erp.omnichannel.domain.identifier.OmniOrderId;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for omnichannel operations.
 */
@Path("/api/v1/omnichannel")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Omnichannel API", description = "Unified commerce operations")
public class OmnichannelResource {

    @Inject
    OmnichannelService omnichannelService;

    // ============ Channel Endpoints ============

    @POST
    @Path("/channels")
    @Operation(summary = "Register a new channel")
    public CompletionStage<Response> registerChannel(@Valid RegisterChannelRequest request) {
        RegisterChannelCommand command = RegisterChannelCommand.builder()
            .name(request.getName())
            .code(request.getCode())
            .channelType(request.getChannelType())
            .currencyCode(request.getCurrencyCode())
            .storeId(request.getStoreId())
            .region(request.getRegion())
            .fulfillmentMethods(request.getFulfillmentMethods())
            .build();

        return omnichannelService.registerChannel(command)
            .thenApply(channelId -> Response
                .created(URI.create("/api/v1/omnichannel/channels/" + channelId.getValue()))
                .entity(new RegisterChannelResponse(channelId))
                .build()
            );
    }

    @GET
    @Path("/channels")
    @Operation(summary = "Get all channels")
    public CompletionStage<Response> getAllChannels() {
        return omnichannelService.getAllChannels()
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @GET
    @Path("/channels/{id}")
    @Operation(summary = "Get channel details")
    public CompletionStage<Response> getChannel(@PathParam("id") UUID id) {
        ChannelId channelId = ChannelId.of(id);
        return omnichannelService.getChannel(channelId)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // ============ Order Endpoints ============

    @POST
    @Path("/orders")
    @Operation(summary = "Create an omnichannel order")
    public CompletionStage<Response> createOmniOrder(@Valid CreateOmniOrderRequest request) {
        CreateOmniOrderCommand command = new CreateOmniOrderCommand(
            request.getSalesOrderId(),
            request.getChannelId(),
            request.getCustomerId(),
            request.getFulfillmentMethod(),
            request.getShippingAddress(),
            request.getBillingAddress(),
            request.getCurrencyCode()
        );

        return omnichannelService.createOmniOrder(command)
            .thenApply(orderId -> Response
                .created(URI.create("/api/v1/omnichannel/orders/" + orderId.getValue()))
                .entity(new CreateOmniOrderResponse(orderId))
                .build()
            );
    }

    @GET
    @Path("/orders/{id}")
    @Operation(summary = "Get omnichannel order")
    public CompletionStage<Response> getOmniOrder(@PathParam("id") UUID id) {
        OmniOrderId orderId = OmniOrderId.of(id);
        return omnichannelService.getOmniOrder(orderId)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @POST
    @Path("/orders/{id}/allocate")
    @Operation(summary = "Allocate inventory for order")
    public CompletionStage<Response> allocateInventory(
            @PathParam("id") UUID id,
            @Valid AllocateInventoryRequest request) {
        OmniOrderId orderId = OmniOrderId.of(id);
        AllocateInventoryCommand command = new AllocateInventoryCommand(
            orderId,
            request.getFulfillmentStoreId(),
            request.getAllocationItems()
        );
        return omnichannelService.allocateInventory(command)
            .thenApply(response -> Response.ok().build());
    }

    @POST
    @Path("/orders/{id}/pickup-ready")
    @Operation(summary = "Mark order as ready for pickup")
    public CompletionStage<Response> readyForPickup(@PathParam("id") UUID id) {
        OmniOrderId orderId = OmniOrderId.of(id);
        ReadyForPickupCommand command = new ReadyForPickupCommand(orderId);
        return omnichannelService.readyForPickup(command)
            .thenApply(response -> Response.ok().build());
    }

    @POST
    @Path("/orders/{id}/pickup-complete")
    @Operation(summary = "Complete pickup")
    public CompletionStage<Response> completePickup(@PathParam("id") UUID id) {
        OmniOrderId orderId = OmniOrderId.of(id);
        CompletePickupCommand command = new CompletePickupCommand(orderId);
        return omnichannelService.completePickup(command)
            .thenApply(response -> Response.ok().build());
    }

    @POST
    @Path("/orders/{id}/ship")
    @Operation(summary = "Ship order")
    public CompletionStage<Response> shipOrder(
            @PathParam("id") UUID id,
            @Valid ShipOrderRequest request) {
        OmniOrderId orderId = OmniOrderId.of(id);
        ShipOrderCommand command = new ShipOrderCommand(
            orderId,
            request.getTrackingNumber(),
            request.getCarrier()
        );
        return omnichannelService.shipOrder(command)
            .thenApply(response -> Response.ok().build());
    }

    @POST
    @Path("/orders/{id}/deliver")
    @Operation(summary = "Deliver order")
    public CompletionStage<Response> deliverOrder(@PathParam("id") UUID id) {
        OmniOrderId orderId = OmniOrderId.of(id);
        DeliverOrderCommand command = new DeliverOrderCommand(orderId);
        return omnichannelService.deliverOrder(command)
            .thenApply(response -> Response.ok().build());
    }

    @POST
    @Path("/orders/{id}/cancel")
    @Operation(summary = "Cancel order")
    public CompletionStage<Response> cancelOrder(
            @PathParam("id") UUID id,
            @Valid CancelOrderRequest request) {
        OmniOrderId orderId = OmniOrderId.of(id);
        CancelOmniOrderCommand command = new CancelOmniOrderCommand(
            orderId,
            request.getReason()
        );
        return omnichannelService.cancelOmniOrder(command)
            .thenApply(response -> Response.ok().build());
    }

    @GET
    @Path("/orders/search")
    @Operation(summary = "Search omnichannel orders")
    public CompletionStage<Response> searchOrders(
            @QueryParam("customerId") String customerId,
            @QueryParam("channelId") UUID channelId,
            @QueryParam("status") String status,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        SearchOmniOrdersQuery query = new SearchOmniOrdersQuery(
            customerId,
            channelId != null ? ChannelId.of(channelId) : null,
            status != null ? OrderStatus.valueOf(status) : null,
            fromDate != null ? Instant.parse(fromDate) : null,
            toDate != null ? Instant.parse(toDate) : null,
            page,
            size
        );
        return omnichannelService.searchOmniOrders(query)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // ============ Fulfillment Recommendation ============

    @POST
    @Path("/fulfillment/recommend")
    @Operation(summary = "Get fulfillment recommendation")
    public CompletionStage<Response> findFulfillmentLocation(
            @Valid FindFulfillmentLocationRequest request) {
        FindFulfillmentLocationCommand command = new FindFulfillmentLocationCommand(
            request.getProductIds(),
            request.getQuantities(),
            request.getCustomerLocation(),
            request.getChannelId()
        );
        return omnichannelService.findFulfillmentLocation(command)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // ============ Inventory Visibility ============

    @GET
    @Path("/inventory/visibility")
    @Operation(summary = "Get inventory visibility")
    public CompletionStage<Response> getInventoryVisibility(
            @QueryParam("productId") UUID productId,
            @QueryParam("locationId") String locationId,
            @QueryParam("channelId") UUID channelId) {
        InventoryVisibilityQuery query = new InventoryVisibilityQuery(
            productId != null ? productId.toString() : null,
            locationId,
            channelId != null ? ChannelId.of(channelId) : null
        );
        return omnichannelService.getInventoryVisibility(query)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // ============ Analytics ============

    @GET
    @Path("/analytics/channels")
    @Operation(summary = "Get channel analytics")
    public CompletionStage<Response> getChannelAnalytics(
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate) {
        ChannelAnalyticsQuery query = new ChannelAnalyticsQuery(
            fromDate != null ? Instant.parse(fromDate) : null,
            toDate != null ? Instant.parse(toDate) : null
        );
        return omnichannelService.getChannelAnalytics(query)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @GET
    @Path("/analytics/customer-journey/{customerId}")
    @Operation(summary = "Get customer journey")
    public CompletionStage<Response> getCustomerJourney(@PathParam("customerId") String customerId) {
        return omnichannelService.getCustomerJourney(customerId)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // ============ Request/Response DTOs ============

    public static class RegisterChannelRequest {
        private String name;
        private String code;
        private ChannelType channelType;
        private String currencyCode;
        private String storeId;
        private String region;
        private List<FulfillmentMethod> fulfillmentMethods;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public ChannelType getChannelType() { return channelType; }
        public void setChannelType(ChannelType channelType) { this.channelType = channelType; }
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
        public String getStoreId() { return storeId; }
        public void setStoreId(String storeId) { this.storeId = storeId; }
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        public List<FulfillmentMethod> getFulfillmentMethods() { return fulfillmentMethods; }
        public void setFulfillmentMethods(List<FulfillmentMethod> fulfillmentMethods) { this.fulfillmentMethods = fulfillmentMethods; }
    }

    public static class RegisterChannelResponse {
        private final ChannelId channelId;

        public RegisterChannelResponse(ChannelId channelId) {
            this.channelId = channelId;
        }

        public UUID getChannelId() { return channelId.getValue(); }
    }

    public static class CreateOmniOrderRequest {
        private UUID salesOrderId;
        private UUID channelId;
        private String customerId;
        private FulfillmentMethod fulfillmentMethod;
        private String shippingAddress;
        private String billingAddress;
        private String currencyCode;

        // Getters and setters
        public UUID getSalesOrderId() { return salesOrderId; }
        public void setSalesOrderId(UUID salesOrderId) { this.salesOrderId = salesOrderId; }
        public UUID getChannelId() { return channelId; }
        public void setChannelId(UUID channelId) { this.channelId = channelId; }
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
        public FulfillmentMethod getFulfillmentMethod() { return fulfillmentMethod; }
        public void setFulfillmentMethod(FulfillmentMethod fulfillmentMethod) { this.fulfillmentMethod = fulfillmentMethod; }
        public String getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
        public String getBillingAddress() { return billingAddress; }
        public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    }

    public static class CreateOmniOrderResponse {
        private final OmniOrderId orderId;

        public CreateOmniOrderResponse(OmniOrderId orderId) {
            this.orderId = orderId;
        }

        public UUID getOrderId() { return orderId.getValue(); }
    }

    public static class AllocateInventoryRequest {
        private String fulfillmentStoreId;
        private List<AllocationItem> allocationItems;

        public String getFulfillmentStoreId() { return fulfillmentStoreId; }
        public void setFulfillmentStoreId(String fulfillmentStoreId) { this.fulfillmentStoreId = fulfillmentStoreId; }
        public List<AllocationItem> getAllocationItems() { return allocationItems; }
        public void setAllocationItems(List<AllocationItem> allocationItems) { this.allocationItems = allocationItems; }
    }

    public static class AllocationItem {
        private String productId;
        private int quantity;

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static class ShipOrderRequest {
        private String trackingNumber;
        private String carrier;

        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
        public String getCarrier() { return carrier; }
        public void setCarrier(String carrier) { this.carrier = carrier; }
    }

    public static class CancelOrderRequest {
        private String reason;

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class FindFulfillmentLocationRequest {
        private List<String> productIds;
        private List<Integer> quantities;
        private String customerLocation;
        private UUID channelId;

        public List<String> getProductIds() { return productIds; }
        public void setProductIds(List<String> productIds) { this.productIds = productIds; }
        public List<Integer> getQuantities() { return quantities; }
        public void setQuantities(List<Integer> quantities) { this.quantities = quantities; }
        public String getCustomerLocation() { return customerLocation; }
        public void setCustomerLocation(String customerLocation) { this.customerLocation = customerLocation; }
        public UUID getChannelId() { return channelId; }
        public void setChannelId(UUID channelId) { this.channelId = channelId; }
    }
}
```

## 4. Update Root POM

**Update `/pom.xml`** to include Omnichannel modules:

```xml
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <module>modules/catalog/domain</module>
    <module>modules/catalog/application</module>
    <module>modules/catalog/infrastructure</module>
    <module>modules/catalog/interfaces</module>

    <module>modules/sales/domain</module>
    <module>modules/sales/application</module>
    <module>modules/sales/infrastructure</module>
    <module>modules/sales/interfaces</module>

    <module>modules/inventory/domain</module>
    <module>modules/inventory/application</module>
    <module>modules/inventory/infrastructure</module>
    <module>modules/inventory/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/customer/domain</module>
    <module>modules/customer/application</module>
    <module>modules/customer/infrastructure</module>
    <module>modules/customer/interfaces</module>

    <module>modules/grocery-pos/domain</module>
    <module>modules/grocery-pos/application</module>
    <module>modules/grocery-pos/infrastructure</module>
    <module>modules/grocery-pos/interfaces</module>

    <module>modules/kiosk/domain</module>
    <module>modules/kiosk/application</module>
    <module>modules/kiosk/infrastructure</module>
    <module>modules/kiosk/interfaces</module>

    <module>modules/omnichannel/domain</module>
    <module>modules/omnichannel/application</module>
    <module>modules/omnichannel/infrastructure</module>
    <module>modules/omnichannel/interfaces</module>
</modules>
```

## Summary

The Omnichannel bounded context provides:

1. **Unified Channel Management**:
   - Register and configure all sales channels (POS, Kiosk, E-commerce, Mobile, Marketplace)
   - Channel-specific settings and fulfillment methods
   - Multi-language, multi-currency support

2. **Unified Order Management**:
   - Single order view across all channels
   - Cross-channel order search and tracking
   - Order lifecycle management (Draft → Submitted → Confirmed → Processing → Shipped → Delivered)

3. **Cross-Channel Fulfillment**:
   - BOPIS (Buy Online, Pickup In Store)
   - Ship-from-store
   - Curbside pickup
   - Drop shipping
   - Digital delivery
   - Locker pickup

4. **Inventory Visibility**:
   - Real-time inventory across all locations
   - Channel-specific inventory visibility
   - Store availability for online customers

5. **Fulfillment Optimization**:
   - Smart fulfillment location recommendation
   - Inventory allocation across channels
   - Order routing optimization

6. **Customer Journey**:
   - Cross-channel customer tracking
   - Attribution analytics
   - Customer experience consistency

7. **Analytics**:
   - Channel performance metrics
   - Cross-channel order analytics
   - Customer journey analysis

## Architecture Integration

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                         OMNICHANNEL ORCHESTRATOR                            │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │   POS        │  │   Kiosk      │  │  E-commerce  │  │  Mobile App  │   │
│  │   Channel    │  │   Channel    │  │   Channel    │  │   Channel    │   │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘   │
│         │                 │                 │                 │             │
│         └─────────────────┼─────────────────┼─────────────────┘             │
│                           │                 │                               │
│                    ┌──────▼─────────────────▼──────┐                        │
│                    │      OMNICHANNEL ENGINE       │                        │
│                    │  ┌────────────────────────┐   │                        │
│                    │  │  Unified Order          │   │                        │
│                    │  │  Management            │   │                        │
│                    │  └────────────────────────┘   │                        │
│                    │  ┌────────────────────────┐   │                        │
│                    │  │  Cross-Channel         │   │                        │
│                    │  │  Fulfillment           │   │                        │
│                    │  └────────────────────────┘   │                        │
│                    │  ┌────────────────────────┐   │                        │
│                    │  │  Inventory Visibility  │   │                        │
│                    │  └────────────────────────┘   │                        │
│                    │  ┌────────────────────────┐   │                        │
│                    │  │  Customer Journey      │   │                        │
│                    │  │  Analytics             │   │                        │
│                    │  └────────────────────────┘   │                        │
│                    └────────────────────────────────┘                        │
│                                      │                                       │
│         ┌────────────────────────────┼────────────────────────────┐         │
│         │                            │                            │         │
│    ┌────▼────┐               ┌───────▼───────┐          ┌─────────▼──────┐ │
│    │ Sales   │               │   Inventory   │          │   Customer     │ │
│    │ Context │               │    Context    │          │    Context     │ │
│    └─────────┘               └───────────────┘          └────────────────┘ │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

This completes the Omnichannel implementation, providing a unified commerce platform that seamlessly connects all sales channels with consistent inventory, pricing, and fulfillment.
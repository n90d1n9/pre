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
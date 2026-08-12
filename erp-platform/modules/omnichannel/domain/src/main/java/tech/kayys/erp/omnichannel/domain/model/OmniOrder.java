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
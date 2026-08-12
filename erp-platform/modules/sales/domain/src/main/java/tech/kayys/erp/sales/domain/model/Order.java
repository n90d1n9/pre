package tech.kayys.erp.sales.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.sales.domain.event.OrderConfirmed;
import tech.kayys.erp.sales.domain.event.OrderCreated;
import tech.kayys.erp.sales.domain.event.OrderSubmitted;
import tech.kayys.erp.sales.domain.identifier.CustomerId;
import tech.kayys.erp.sales.domain.identifier.OrderId;
import tech.kayys.erp.sales.domain.valueobject.Address;
import tech.kayys.erp.sales.domain.valueobject.Money;
import tech.kayys.erp.sales.domain.valueobject.OrderStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Order aggregate root in the Sales bounded context.
 * Represents a customer order from placement to fulfillment.
 */
public final class Order extends AggregateRoot<OrderId> {
    
    private static final long serialVersionUID = 1L;
    
    private CustomerId customerId;
    private List<OrderItem> items;
    private Money subtotal;
    private Money taxTotal;
    private Money shippingCost;
    private Money discountTotal;
    private Money grandTotal;
    private OrderStatus status;
    private Address shippingAddress;
    private Address billingAddress;
    private String customerNotes;
    private String internalNotes;
    private Instant submittedAt;
    private Instant confirmedAt;
    private Instant shippedAt;
    private Instant deliveredAt;
    private String shippingMethod;
    private String trackingNumber;

    private Order(OrderId id, CustomerId customerId) {
        super(id);
        this.customerId = customerId;
        this.items = new ArrayList<>();
        this.status = OrderStatus.DRAFT;
        this.shippingCost = Money.zero("USD");
        this.discountTotal = Money.zero("USD");
        this.taxTotal = Money.zero("USD");
    }

    // Private constructor for ORM/deserialization
    private Order() {
        super();
    }

    /**
     * Factory method to create a new Order.
     */
    public static Order create(OrderId id, CustomerId customerId) {
        Order order = new Order(id, customerId);
        order.registerEvent(new OrderCreated(order));
        return order;
    }

    /**
     * Adds an item to the order.
     */
    public void addItem(OrderItem item) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify order in status: " + status);
        }
        
        // Check for duplicate product
        items.stream()
            .filter(existing -> existing.getProductId().equals(item.getProductId()))
            .findFirst()
            .ifPresent(existing -> {
                throw new IllegalArgumentException(
                    "Product already in order: " + item.getProductName()
                );
            });
        
        items.add(item);
        recalculateTotals();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes an item from the order.
     */
    public void removeItem(int index) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify order in status: " + status);
        }
        if (index < 0 || index >= items.size()) {
            throw new IllegalArgumentException("Invalid item index: " + index);
        }
        items.remove(index);
        recalculateTotals();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the quantity of an item.
     */
    public void updateItemQuantity(int index, int quantity) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify order in status: " + status);
        }
        if (index < 0 || index >= items.size()) {
            throw new IllegalArgumentException("Invalid item index: " + index);
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        OrderItem current = items.get(index);
        OrderItem updated = current.withQuantity(quantity);
        items.set(index, updated);
        recalculateTotals();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the shipping address.
     */
    public void setShippingAddress(Address address) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify order in status: " + status);
        }
        this.shippingAddress = address;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the billing address.
     */
    public void setBillingAddress(Address address) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify order in status: " + status);
        }
        this.billingAddress = address;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Submits the order for processing.
     */
    public void submit() {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Order is not in draft status: " + status);
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Order must have at least one item");
        }
        if (shippingAddress == null) {
            throw new IllegalStateException("Shipping address is required");
        }
        
        this.status = OrderStatus.SUBMITTED;
        this.submittedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
        
        registerEvent(new OrderSubmitted(this));
    }

    /**
     * Confirms the order.
     */
    public void confirm() {
        if (status != OrderStatus.SUBMITTED) {
            throw new IllegalStateException("Order is not in submitted status: " + status);
        }
        
        this.status = OrderStatus.CONFIRMED;
        this.confirmedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
        
        registerEvent(new OrderConfirmed(this));
    }

    /**
     * Cancels the order.
     */
    public void cancel(String reason) {
        if (status.isFinal()) {
            throw new IllegalStateException("Order is already finalized: " + status);
        }
        
        this.status = OrderStatus.CANCELLED;
        this.internalNotes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the order as shipped.
     */
    public void ship(String trackingNumber, String shippingMethod) {
        if (status != OrderStatus.CONFIRMED && status != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Cannot ship order in status: " + status);
        }
        
        this.status = OrderStatus.SHIPPED;
        this.trackingNumber = trackingNumber;
        this.shippingMethod = shippingMethod;
        this.shippedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the order as delivered.
     */
    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Cannot deliver order in status: " + status);
        }
        
        this.status = OrderStatus.DELIVERED;
        this.deliveredAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private void recalculateTotals() {
        // Calculate subtotal
        Money newSubtotal = items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(Money.zero("USD"), Money::add);
        
        // Calculate tax (simplified - 10% in this example)
        // In a real system, this would use a tax calculation service
        Money newTaxTotal = newSubtotal.percentage(BigDecimal.TEN);
        
        // Calculate discount (simplified)
        // In a real system, this would apply promotion rules
        Money newDiscountTotal = Money.zero("USD");
        
        // Calculate grand total
        Money newGrandTotal = newSubtotal
            .add(newTaxTotal)
            .add(shippingCost)
            .subtract(newDiscountTotal);
        
        this.subtotal = newSubtotal;
        this.taxTotal = newTaxTotal;
        this.discountTotal = newDiscountTotal;
        this.grandTotal = newGrandTotal;
    }

    /**
     * Calculates the total weight of the order (for shipping).
     */
    public double calculateTotalWeight() {
        // In a real system, this would use product weights from Catalog
        return items.stream()
            .mapToDouble(item -> item.getQuantity() * 1.0) // placeholder weight
            .sum();
    }

    /**
     * Checks if the order is eligible for free shipping.
     */
    public boolean isEligibleForFreeShipping() {
        return grandTotal != null && grandTotal.isGreaterThan(
            Money.of(100, "USD")
        );
    }

    // Getters
    public CustomerId getCustomerId() { return customerId; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
    public Money getSubtotal() { return subtotal; }
    public Money getTaxTotal() { return taxTotal; }
    public Money getShippingCost() { return shippingCost; }
    public Money getDiscountTotal() { return discountTotal; }
    public Money getGrandTotal() { return grandTotal; }
    public OrderStatus getStatus() { return status; }
    public Address getShippingAddress() { return shippingAddress; }
    public Address getBillingAddress() { return billingAddress; }
    public String getCustomerNotes() { return customerNotes; }
    public String getInternalNotes() { return internalNotes; }
    public Instant getSubmittedAt() { return submittedAt; }
    public Instant getConfirmedAt() { return confirmedAt; }
    public Instant getShippedAt() { return shippedAt; }
    public Instant getDeliveredAt() { return deliveredAt; }
    public String getShippingMethod() { return shippingMethod; }
    public String getTrackingNumber() { return trackingNumber; }

    public void setCustomerNotes(String customerNotes) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify order in status: " + status);
        }
        this.customerNotes = customerNotes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setShippingCost(Money shippingCost) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify order in status: " + status);
        }
        this.shippingCost = shippingCost;
        recalculateTotals();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + getId() +
                ", customerId=" + customerId +
                ", status=" + status +
                ", total=" + grandTotal +
                ", items=" + items.size() +
                '}';
    }
}
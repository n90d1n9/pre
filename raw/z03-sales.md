# Complete Implementation: Sales/Order Bounded Context

Now I'll implement the complete Sales/Order bounded context with full DDD patterns, showing how it integrates with the existing architecture.

## 1. Sales/Order Domain Module

**`/modules/sales/domain/pom.xml`**:

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

    <artifactId>erp-sales-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/sales/domain/src/main/java/tech/kayys/erp/sales/domain/identifier/OrderId.java`**:

```java
package tech.kayys.erp.sales.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Order identifier in the Sales bounded context.
 */
public final class OrderId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public OrderId(UUID value) {
        super(value);
    }

    public static OrderId of(UUID value) {
        return new OrderId(value);
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }

    public static OrderId fromString(String value) {
        return new OrderId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "OrderId{" + value + "}";
    }
}
```

**`/modules/sales/domain/src/main/java/tech/kayys/erp/sales/domain/identifier/CustomerId.java`**:

```java
package tech.kayys.erp.sales.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Customer ID in the Sales bounded context.
 * This represents a customer from the perspective of Sales.
 * It's a value object, not an entity reference to CRM's Customer.
 */
public final class CustomerId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public CustomerId(UUID value) {
        super(value);
    }

    public static CustomerId of(UUID value) {
        return new CustomerId(value);
    }

    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return "CustomerId{" + value + "}";
    }
}
```

**`/modules/sales/domain/src/main/java/tech/kayys/erp/sales/domain/valueobject/OrderStatus.java`**:

```java
package tech.kayys.erp.sales.domain.valueobject;

/**
 * Status of an order in the Sales context.
 */
public enum OrderStatus {
    DRAFT("Draft - being created"),
    SUBMITTED("Submitted - awaiting confirmation"),
    CONFIRMED("Confirmed - approved for processing"),
    PROCESSING("Processing - being fulfilled"),
    SHIPPED("Shipped - in transit"),
    DELIVERED("Delivered - completed"),
    CANCELLED("Cancelled - order voided"),
    REFUNDED("Refunded - money returned");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canTransitionTo(OrderStatus target) {
        return switch (this) {
            case DRAFT -> target == SUBMITTED || target == CANCELLED;
            case SUBMITTED -> target == CONFIRMED || target == CANCELLED;
            case CONFIRMED -> target == PROCESSING || target == CANCELLED;
            case PROCESSING -> target == SHIPPED || target == CANCELLED;
            case SHIPPED -> target == DELIVERED || target == REFUNDED;
            case DELIVERED, REFUNDED, CANCELLED -> false;
        };
    }

    public boolean isFinal() {
        return this == DELIVERED || this == REFUNDED || this == CANCELLED;
    }

    public boolean isActive() {
        return this != DELIVERED && this != REFUNDED && this != CANCELLED;
    }
}
```

**`/modules/sales/domain/src/main/java/tech/kayys/erp/sales/domain/valueobject/Money.java`**:

```java
package tech.kayys.erp.sales.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Money value object for the Sales context.
 * Different from Catalog's Money but conceptually similar.
 */
public final class Money implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final BigDecimal amount;
    private final Currency currency;
    private final int scale;

    public Money(BigDecimal amount, Currency currency) {
        this(amount, currency, 2);
    }

    public Money(BigDecimal amount, Currency currency, int scale) {
        this.amount = amount.setScale(scale, RoundingMode.HALF_EVEN);
        this.currency = currency;
        this.scale = scale;
        validate();
    }

    @Override
    public void validate() {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        validateCurrency(other);
        return new Money(amount.add(other.amount), currency, scale);
    }

    public Money subtract(Money other) {
        validateCurrency(other);
        return new Money(amount.subtract(other.amount), currency, scale);
    }

    public Money multiply(BigDecimal multiplier) {
        return new Money(amount.multiply(multiplier), currency, scale);
    }

    public Money multiply(int multiplier) {
        return multiply(BigDecimal.valueOf(multiplier));
    }

    public Money divide(BigDecimal divisor) {
        return new Money(amount.divide(divisor, scale, RoundingMode.HALF_EVEN), currency, scale);
    }

    public Money percentage(BigDecimal percentage) {
        return multiply(percentage.divide(BigDecimal.valueOf(100), scale, RoundingMode.HALF_EVEN));
    }

    public int compareTo(Money other) {
        validateCurrency(other);
        return amount.compareTo(other.amount);
    }

    public boolean isGreaterThan(Money other) {
        return compareTo(other) > 0;
    }

    public boolean isLessThan(Money other) {
        return compareTo(other) < 0;
    }

    public boolean isZero() {
        return amount.signum() == 0;
    }

    public boolean isPositive() {
        return amount.signum() > 0;
    }

    public boolean isNegative() {
        return amount.signum() < 0;
    }

    private void validateCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Currency mismatch: " + this.currency + " != " + other.currency
            );
        }
    }

    public Money withScale(int newScale) {
        return new Money(amount, currency, newScale);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 &&
               Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return currency.getCurrencyCode() + " " + amount.toPlainString();
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, Currency.getInstance(currencyCode));
    }

    public static Money of(String amount, String currencyCode) {
        return new Money(new BigDecimal(amount), Currency.getInstance(currencyCode));
    }

    public static Money of(long amount, String currencyCode) {
        return new Money(BigDecimal.valueOf(amount), Currency.getInstance(currencyCode));
    }

    public static Money zero(String currencyCode) {
        return new Money(BigDecimal.ZERO, Currency.getInstance(currencyCode));
    }
}
```

**`/modules/sales/domain/src/main/java/tech/kayys/erp/sales/domain/valueobject/Address.java`**:

```java
package tech.kayys.erp.sales.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.util.Objects;

/**
 * Address value object for shipping and billing.
 */
public final class Address implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String street;
    private final String city;
    private final String state;
    private final String postalCode;
    private final String country;

    public Address(String street, String city, String state, String postalCode, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        validate();
    }

    @Override
    public void validate() {
        if (street == null || street.trim().isEmpty()) {
            throw new IllegalArgumentException("Street cannot be empty");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be empty");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country cannot be empty");
        }
    }

    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }

    public String formattedAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(street).append("\n");
        sb.append(city);
        if (state != null && !state.isEmpty()) {
            sb.append(", ").append(state);
        }
        if (postalCode != null && !postalCode.isEmpty()) {
            sb.append(" ").append(postalCode);
        }
        sb.append("\n").append(country);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
               Objects.equals(city, address.city) &&
               Objects.equals(state, address.state) &&
               Objects.equals(postalCode, address.postalCode) &&
               Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, state, postalCode, country);
    }

    @Override
    public String toString() {
        return formattedAddress();
    }

    public static Address of(String street, String city, String state, String postalCode, String country) {
        return new Address(street, city, state, postalCode, country);
    }
}
```

**`/modules/sales/domain/src/main/java/tech/kayys/erp/sales/domain/model/OrderItem.java`**:

```java
package tech.kayys.erp.sales.domain.model;

import tech.kayys.erp.foundation.domain.ValueObject;
import tech.kayys.erp.sales.domain.valueobject.Money;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Order item value object representing a product in an order.
 * This is a value object, not an entity.
 */
public final class OrderItem implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID productId;
    private final String productName;
    private final String sku;
    private final int quantity;
    private final Money unitPrice;
    private final Money totalPrice;
    private final Money taxAmount;
    private final Money discountAmount;

    private OrderItem(Builder builder) {
        this.productId = builder.productId;
        this.productName = builder.productName;
        this.sku = builder.sku;
        this.quantity = builder.quantity;
        this.unitPrice = builder.unitPrice;
        this.taxAmount = builder.taxAmount;
        this.discountAmount = builder.discountAmount;
        this.totalPrice = calculateTotal();
        validate();
    }

    @Override
    public void validate() {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (unitPrice == null || unitPrice.isNegative()) {
            throw new IllegalArgumentException("Unit price must be positive");
        }
        if (taxAmount != null && taxAmount.isNegative()) {
            throw new IllegalArgumentException("Tax amount cannot be negative");
        }
        if (discountAmount != null && discountAmount.isNegative()) {
            throw new IllegalArgumentException("Discount amount cannot be negative");
        }
    }

    private Money calculateTotal() {
        Money subtotal = unitPrice.multiply(quantity);
        
        if (discountAmount != null && !discountAmount.isZero()) {
            subtotal = subtotal.subtract(discountAmount);
        }
        
        if (taxAmount != null && !taxAmount.isZero()) {
            subtotal = subtotal.add(taxAmount);
        }
        
        return subtotal;
    }

    // Getters
    public UUID getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getSku() { return sku; }
    public int getQuantity() { return quantity; }
    public Money getUnitPrice() { return unitPrice; }
    public Money getTotalPrice() { return totalPrice; }
    public Money getTaxAmount() { return taxAmount; }
    public Money getDiscountAmount() { return discountAmount; }
    public Money getSubtotal() { return unitPrice.multiply(quantity); }

    public OrderItem withQuantity(int newQuantity) {
        return new Builder()
            .productId(productId)
            .productName(productName)
            .sku(sku)
            .quantity(newQuantity)
            .unitPrice(unitPrice)
            .taxAmount(taxAmount)
            .discountAmount(discountAmount)
            .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem that = (OrderItem) o;
        return quantity == that.quantity &&
               Objects.equals(productId, that.productId) &&
               Objects.equals(productName, that.productName) &&
               Objects.equals(unitPrice, that.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName, quantity, unitPrice);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", sku='" + sku + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID productId;
        private String productName;
        private String sku;
        private int quantity = 1;
        private Money unitPrice;
        private Money taxAmount = Money.zero("USD");
        private Money discountAmount = Money.zero("USD");

        public Builder productId(UUID productId) {
            this.productId = productId;
            return this;
        }

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder sku(String sku) {
            this.sku = sku;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder unitPrice(Money unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public Builder taxAmount(Money taxAmount) {
            this.taxAmount = taxAmount;
            return this;
        }

        public Builder discountAmount(Money discountAmount) {
            this.discountAmount = discountAmount;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}
```

**`/modules/sales/domain/src/main/java/tech/kayys/erp/sales/domain/model/Order.java`**:

```java
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
```

**`/modules/sales/domain/src/main/java/tech/kayys/erp/sales/domain/event/OrderCreated.java`**:

```java
package tech.kayys.erp.sales.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.sales.domain.model.Order;

import java.time.Instant;
import java.util.UUID;

public class OrderCreated implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String customerId;
    private final int itemCount;

    public OrderCreated(Order order) {
        this.eventId = UUID.randomUUID();
        this.eventType = "OrderCreated";
        this.occurredAt = Instant.now();
        this.aggregateId = order.getId().toString();
        this.aggregateType = "Order";
        this.customerId = order.getCustomerId().toString();
        this.itemCount = order.getItems().size();
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getAggregateId() {
        return aggregateId;
    }

    @Override
    public String getAggregateType() {
        return aggregateType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getItemCount() {
        return itemCount;
    }

    @Override
    public String toString() {
        return "OrderCreated{" +
                "eventId=" + eventId +
                ", orderId=" + aggregateId +
                ", customerId='" + customerId + '\'' +
                '}';
    }
}
```

**`/modules/sales/domain/src/main/java/tech/kayys/erp/sales/domain/event/OrderSubmitted.java`**:

```java
package tech.kayys.erp.sales.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.sales.domain.model.Order;

import java.time.Instant;
import java.util.UUID;

public class OrderSubmitted implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String grandTotal;
    private final String currency;

    public OrderSubmitted(Order order) {
        this.eventId = UUID.randomUUID();
        this.eventType = "OrderSubmitted";
        this.occurredAt = Instant.now();
        this.aggregateId = order.getId().toString();
        this.aggregateType = "Order";
        this.grandTotal = order.getGrandTotal().getAmount().toPlainString();
        this.currency = order.getGrandTotal().getCurrency().getCurrencyCode();
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getAggregateId() {
        return aggregateId;
    }

    @Override
    public String getAggregateType() {
        return aggregateType;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "OrderSubmitted{" +
                "eventId=" + eventId +
                ", orderId=" + aggregateId +
                ", grandTotal=" + grandTotal +
                '}';
    }
}
```

**`/modules/sales/domain/src/main/java/tech/kayys/erp/sales/domain/event/OrderConfirmed.java`**:

```java
package tech.kayys.erp.sales.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.sales.domain.model.Order;

import java.time.Instant;
import java.util.UUID;

public class OrderConfirmed implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String customerId;

    public OrderConfirmed(Order order) {
        this.eventId = UUID.randomUUID();
        this.eventType = "OrderConfirmed";
        this.occurredAt = Instant.now();
        this.aggregateId = order.getId().toString();
        this.aggregateType = "Order";
        this.customerId = order.getCustomerId().toString();
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getAggregateId() {
        return aggregateId;
    }

    @Override
    public String getAggregateType() {
        return aggregateType;
    }

    public String getCustomerId() {
        return customerId;
    }

    @Override
    public String toString() {
        return "OrderConfirmed{" +
                "eventId=" + eventId +
                ", orderId=" + aggregateId +
                '}';
    }
}
```

**`/modules/sales/domain/src/main/java/tech/kayys/erp/sales/domain/repository/OrderRepository.java`**:

```java
package tech.kayys.erp.sales.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.sales.domain.identifier.CustomerId;
import tech.kayys.erp.sales.domain.identifier.OrderId;
import tech.kayys.erp.sales.domain.model.Order;
import tech.kayys.erp.sales.domain.valueobject.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for Order aggregates.
 * Provides domain-specific query methods beyond the generic Repository interface.
 */
public interface OrderRepository extends Repository<Order, OrderId> {

    /**
     * Finds all orders for a customer.
     */
    CompletionStage<List<Order>> findByCustomerId(CustomerId customerId);

    /**
     * Finds orders by status.
     */
    CompletionStage<List<Order>> findByStatus(OrderStatus status);

    /**
     * Finds orders submitted between two dates.
     */
    CompletionStage<List<Order>> findSubmittedBetween(Instant start, Instant end);

    /**
     * Finds orders that need processing (submitted or confirmed).
     */
    default CompletionStage<List<Order>> findOrdersNeedingProcessing() {
        return findByStatus(OrderStatus.SUBMITTED)
            .thenCombine(findByStatus(OrderStatus.CONFIRMED), 
                (submitted, confirmed) -> {
                    submitted.addAll(confirmed);
                    return submitted;
                });
    }

    /**
     * Counts orders by status.
     */
    CompletionStage<Long> countByStatus(OrderStatus status);

    /**
     * Checks if a customer has any active orders.
     */
    default CompletionStage<Boolean> customerHasActiveOrders(CustomerId customerId) {
        return findByCustomerId(customerId)
            .thenApply(orders -> orders.stream()
                .anyMatch(order -> order.getStatus().isActive())
            );
    }
}
```

## 2. Sales/Order Application Module

**`/modules/sales/application/pom.xml`**:

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

    <artifactId>erp-sales-application</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-sales-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-application</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

**`/modules/sales/application/src/main/java/tech/kayys/erp/sales/application/api/command/CreateOrderCommand.java`**:

```java
package tech.kayys.erp.sales.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.sales.domain.identifier.CustomerId;
import tech.kayys.erp.sales.domain.identifier.OrderId;

import java.util.List;
import java.util.UUID;

/**
 * Command to create a new order.
 */
public record CreateOrderCommand(
        OrderId orderId,
        CustomerId customerId,
        List<OrderItemCommand> items,
        AddressCommand shippingAddress,
        AddressCommand billingAddress,
        String customerNotes
) implements Command<OrderId> {

    public CreateOrderCommand {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        if (shippingAddress == null) {
            throw new IllegalArgumentException("Shipping address is required");
        }
    }

    /**
     * Creates a builder for the command.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private OrderId orderId;
        private CustomerId customerId;
        private List<OrderItemCommand> items;
        private AddressCommand shippingAddress;
        private AddressCommand billingAddress;
        private String customerNotes;

        public Builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder customerId(CustomerId customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder items(List<OrderItemCommand> items) {
            this.items = items;
            return this;
        }

        public Builder shippingAddress(AddressCommand shippingAddress) {
            this.shippingAddress = shippingAddress;
            return this;
        }

        public Builder billingAddress(AddressCommand billingAddress) {
            this.billingAddress = billingAddress;
            return this;
        }

        public Builder customerNotes(String customerNotes) {
            this.customerNotes = customerNotes;
            return this;
        }

        public CreateOrderCommand build() {
            if (orderId == null) {
                orderId = OrderId.generate();
            }
            if (billingAddress == null) {
                billingAddress = shippingAddress;
            }
            return new CreateOrderCommand(
                orderId, customerId, items, 
                shippingAddress, billingAddress, customerNotes
            );
        }
    }

    /**
     * Order item command.
     */
    public record OrderItemCommand(
            UUID productId,
            String productName,
            String sku,
            int quantity,
            MoneyCommand unitPrice
    ) {
        public OrderItemCommand {
            if (productId == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            if (productName == null || productName.trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be empty");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (unitPrice == null) {
                throw new IllegalArgumentException("Unit price is required");
            }
        }
    }

    /**
     * Address command.
     */
    public record AddressCommand(
            String street,
            String city,
            String state,
            String postalCode,
            String country
    ) {
        public AddressCommand {
            if (street == null || street.trim().isEmpty()) {
                throw new IllegalArgumentException("Street cannot be empty");
            }
            if (city == null || city.trim().isEmpty()) {
                throw new IllegalArgumentException("City cannot be empty");
            }
            if (country == null || country.trim().isEmpty()) {
                throw new IllegalArgumentException("Country cannot be empty");
            }
        }
    }

    /**
     * Money command.
     */
    public record MoneyCommand(
            String amount,
            String currencyCode
    ) {
        public MoneyCommand {
            if (amount == null || amount.trim().isEmpty()) {
                throw new IllegalArgumentException("Amount cannot be empty");
            }
            if (currencyCode == null || currencyCode.trim().isEmpty()) {
                throw new IllegalArgumentException("Currency code cannot be empty");
            }
        }
    }
}
```

**`/modules/sales/application/src/main/java/tech/kayys/erp/sales/application/api/command/SubmitOrderCommand.java`**:

```java
package tech.kayys.erp.sales.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.sales.domain.identifier.OrderId;

/**
 * Command to submit an order for processing.
 */
public record SubmitOrderCommand(
        OrderId orderId
) implements Command<OrderId> {

    public SubmitOrderCommand {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
    }
}
```

**`/modules/sales/application/src/main/java/tech/kayys/erp/sales/application/api/command/ConfirmOrderCommand.java`**:

```java
package tech.kayys.erp.sales.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.sales.domain.identifier.OrderId;

/**
 * Command to confirm an order.
 */
public record ConfirmOrderCommand(
        OrderId orderId
) implements Command<OrderId> {

    public ConfirmOrderCommand {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
    }
}
```

**`/modules/sales/application/src/main/java/tech/kayys/erp/sales/application/api/command/ShipOrderCommand.java`**:

```java
package tech.kayys.erp.sales.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.sales.domain.identifier.OrderId;

/**
 * Command to mark an order as shipped.
 */
public record ShipOrderCommand(
        OrderId orderId,
        String trackingNumber,
        String shippingMethod
) implements Command<OrderId> {

    public ShipOrderCommand {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (trackingNumber == null || trackingNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Tracking number is required");
        }
        if (shippingMethod == null || shippingMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipping method is required");
        }
    }
}
```

**`/modules/sales/application/src/main/java/tech/kayys/erp/sales/application/api/command/CancelOrderCommand.java`**:

```java
package tech.kayys.erp.sales.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.sales.domain.identifier.OrderId;

/**
 * Command to cancel an order.
 */
public record CancelOrderCommand(
        OrderId orderId,
        String reason
) implements Command<OrderId> {

    public CancelOrderCommand {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Cancellation reason is required");
        }
    }
}
```

**`/modules/sales/application/src/main/java/tech/kayys/erp/sales/application/api/query/GetOrderQuery.java`**:

```java
package tech.kayys.erp.sales.application.api.query;

import tech.kayys.erp.foundation.application.Query;
import tech.kayys.erp.sales.domain.identifier.OrderId;

/**
 * Query to retrieve an order by ID.
 */
public record GetOrderQuery(
        OrderId orderId
) implements Query<OrderView> {

    public GetOrderQuery {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
    }
}
```

**`/modules/sales/application/src/main/java/tech/kayys/erp/sales/application/api/query/SearchOrdersQuery.java`**:

```java
package tech.kayys.erp.sales.application.api.query;

import tech.kayys.erp.foundation.application.Query;
import tech.kayys.erp.sales.domain.identifier.CustomerId;
import tech.kayys.erp.sales.domain.valueobject.OrderStatus;

import java.time.Instant;
import java.util.List;

/**
 * Query to search for orders.
 */
public record SearchOrdersQuery(
        CustomerId customerId,
        OrderStatus status,
        Instant fromDate,
        Instant toDate,
        Double minTotal,
        Double maxTotal,
        String currencyCode,
        int page,
        int size,
        SortBy sortBy
) implements Query<List<OrderView>> {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;

    public SearchOrdersQuery {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
    }

    public enum SortBy {
        ORDER_DATE_ASC,
        ORDER_DATE_DESC,
        STATUS_ASC,
        STATUS_DESC,
        TOTAL_ASC,
        TOTAL_DESC
    }

    public static SearchOrdersQuery defaultQuery() {
        return new SearchOrdersQuery(
            null, null, null, null, null, null,
            DEFAULT_PAGE, DEFAULT_SIZE, SortBy.ORDER_DATE_DESC
        );
    }

    public SearchOrdersQuery withPage(int page) {
        return new SearchOrdersQuery(
            customerId, status, fromDate, toDate,
            minTotal, maxTotal, currencyCode,
            page, size, sortBy
        );
    }

    public SearchOrdersQuery withSize(int size) {
        return new SearchOrdersQuery(
            customerId, status, fromDate, toDate,
            minTotal, maxTotal, currencyCode,
            page, size, sortBy
        );
    }

    public SearchOrdersQuery withStatus(OrderStatus status) {
        return new SearchOrdersQuery(
            customerId, status, fromDate, toDate,
            minTotal, maxTotal, currencyCode,
            page, size, sortBy
        );
    }

    public SearchOrdersQuery forCustomer(CustomerId customerId) {
        return new SearchOrdersQuery(
            customerId, status, fromDate, toDate,
            minTotal, maxTotal, currencyCode,
            page, size, sortBy
        );
    }
}
```

**`/modules/sales/application/src/main/java/tech/kayys/erp/sales/application/api/query/OrderView.java`**:

```java
package tech.kayys.erp.sales.application.api.query;

import tech.kayys.erp.sales.domain.model.Order;
import tech.kayys.erp.sales.domain.model.OrderItem;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Read-only view of an order.
 */
public record OrderView(
        String orderId,
        String customerId,
        List<OrderItemView> items,
        String subtotal,
        String taxTotal,
        String shippingCost,
        String discountTotal,
        String grandTotal,
        String currency,
        String status,
        AddressView shippingAddress,
        AddressView billingAddress,
        String customerNotes,
        String trackingNumber,
        String shippingMethod,
        Instant createdAt,
        Instant submittedAt,
        Instant confirmedAt,
        Instant shippedAt,
        Instant deliveredAt,
        int itemCount
) {

    public static OrderView fromDomain(Order order) {
        List<OrderItemView> items = order.getItems().stream()
            .map(OrderItemView::fromDomain)
            .collect(Collectors.toList());

        return new OrderView(
            order.getId().toString(),
            order.getCustomerId().toString(),
            items,
            order.getSubtotal().getAmount().toPlainString(),
            order.getTaxTotal().getAmount().toPlainString(),
            order.getShippingCost().getAmount().toPlainString(),
            order.getDiscountTotal().getAmount().toPlainString(),
            order.getGrandTotal().getAmount().toPlainString(),
            order.getGrandTotal().getCurrency().getCurrencyCode(),
            order.getStatus().name(),
            AddressView.fromDomain(order.getShippingAddress()),
            AddressView.fromDomain(order.getBillingAddress()),
            order.getCustomerNotes(),
            order.getTrackingNumber(),
            order.getShippingMethod(),
            order.getCreatedAt(),
            order.getSubmittedAt(),
            order.getConfirmedAt(),
            order.getShippedAt(),
            order.getDeliveredAt(),
            order.getItems().size()
        );
    }

    public record OrderItemView(
            String productId,
            String productName,
            String sku,
            int quantity,
            String unitPrice,
            String totalPrice,
            String currency
    ) {
        public static OrderItemView fromDomain(OrderItem item) {
            return new OrderItemView(
                item.getProductId().toString(),
                item.getProductName(),
                item.getSku(),
                item.getQuantity(),
                item.getUnitPrice().getAmount().toPlainString(),
                item.getTotalPrice().getAmount().toPlainString(),
                item.getTotalPrice().getCurrency().getCurrencyCode()
            );
        }
    }

    public record AddressView(
            String street,
            String city,
            String state,
            String postalCode,
            String country
    ) {
        public static AddressView fromDomain(tech.kayys.erp.sales.domain.valueobject.Address address) {
            if (address == null) {
                return null;
            }
            return new AddressView(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry()
            );
        }
    }
}
```

**`/modules/sales/application/src/main/java/tech/kayys/erp/sales/application/api/OrderCommandService.java`**:

```java
package tech.kayys.erp.sales.application.api;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.sales.application.api.command.*;
import tech.kayys.erp.sales.domain.identifier.OrderId;

import java.util.concurrent.CompletionStage;

/**
 * Public API for order commands.
 */
public interface OrderCommandService {

    /**
     * Creates a new order.
     */
    CompletionStage<OrderId> createOrder(CreateOrderCommand command);

    /**
     * Submits an order for processing.
     */
    CompletionStage<OrderId> submitOrder(SubmitOrderCommand command);

    /**
     * Confirms an order.
     */
    CompletionStage<OrderId> confirmOrder(ConfirmOrderCommand command);

    /**
     * Ships an order.
     */
    CompletionStage<OrderId> shipOrder(ShipOrderCommand command);

    /**
     * Cancels an order.
     */
    CompletionStage<OrderId> cancelOrder(CancelOrderCommand command);
}
```

**`/modules/sales/application/src/main/java/tech/kayys/erp/sales/application/api/OrderQueryService.java`**:

```java
package tech.kayys.erp.sales.application.api;

import tech.kayys.erp.sales.application.api.query.GetOrderQuery;
import tech.kayys.erp.sales.application.api.query.OrderView;
import tech.kayys.erp.sales.application.api.query.SearchOrdersQuery;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Public API for order queries.
 */
public interface OrderQueryService {

    /**
     * Retrieves an order by ID.
     */
    CompletionStage<OrderView> getOrder(GetOrderQuery query);

    /**
     * Searches for orders.
     */
    CompletionStage<List<OrderView>> searchOrders(SearchOrdersQuery query);

    /**
     * Checks if an order exists.
     */
    CompletionStage<Boolean> orderExists(tech.kayys.erp.sales.domain.identifier.OrderId orderId);

    /**
     * Gets orders for a customer.
     */
    default CompletionStage<List<OrderView>> getCustomerOrders(
            tech.kayys.erp.sales.domain.identifier.CustomerId customerId) {
        SearchOrdersQuery query = SearchOrdersQuery.defaultQuery()
            .forCustomer(customerId)
            .withSize(100);
        return searchOrders(query);
    }
}
```

**`/modules/sales/application/src/main/java/tech/kayys/erp/sales/application/internal/command/CreateOrderHandler.java`**:

```java
package tech.kayys.erp.sales.application.internal.command;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.sales.application.api.command.CreateOrderCommand;
import tech.kayys.erp.sales.application.port.ProductPricePort;
import tech.kayys.erp.sales.domain.identifier.OrderId;
import tech.kayys.erp.sales.domain.model.Order;
import tech.kayys.erp.sales.domain.model.OrderItem;
import tech.kayys.erp.sales.domain.repository.OrderRepository;
import tech.kayys.erp.sales.domain.valueobject.Address;
import tech.kayys.erp.sales.domain.valueobject.Money;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Handler for creating orders.
 */
@UseCase("Create a new order")
public class CreateOrderHandler implements CommandHandler<CreateOrderCommand, OrderId> {

    private final OrderRepository orderRepository;
    private final ProductPricePort productPricePort;

    @Inject
    public CreateOrderHandler(OrderRepository orderRepository, ProductPricePort productPricePort) {
        this.orderRepository = orderRepository;
        this.productPricePort = productPricePort;
    }

    @Override
    public CompletionStage<OrderId> handle(CreateOrderCommand command) {
        // 1. Validate customer exists (would use a customer service)
        // This would call the CRM context through a port

        // 2. Create the order
        Order order = Order.create(command.orderId(), command.customerId());

        // 3. Convert and add items
        List<OrderItem> items = command.items().stream()
            .map(this::toOrderItem)
            .collect(Collectors.toList());

        for (OrderItem item : items) {
            // Validate product availability (would call Inventory)
            // This is a cross-context interaction
            order.addItem(item);
        }

        // 4. Set addresses
        Address shippingAddress = toAddress(command.shippingAddress());
        Address billingAddress = toAddress(command.billingAddress());
        
        order.setShippingAddress(shippingAddress);
        order.setBillingAddress(billingAddress);

        // 5. Set customer notes
        if (command.customerNotes() != null) {
            order.setCustomerNotes(command.customerNotes());
        }

        // 6. Save the order
        return orderRepository.save(order)
            .thenApply(Order::getId);
    }

    private OrderItem toOrderItem(CreateOrderCommand.OrderItemCommand itemCommand) {
        Money unitPrice = Money.of(
            new BigDecimal(itemCommand.unitPrice().amount()),
            itemCommand.unitPrice().currencyCode()
        );

        return OrderItem.builder()
            .productId(itemCommand.productId())
            .productName(itemCommand.productName())
            .sku(itemCommand.sku())
            .quantity(itemCommand.quantity())
            .unitPrice(unitPrice)
            .build();
    }

    private Address toAddress(CreateOrderCommand.AddressCommand addressCommand) {
        return Address.of(
            addressCommand.street(),
            addressCommand.city(),
            addressCommand.state(),
            addressCommand.postalCode(),
            addressCommand.country()
        );
    }
}
```

**`/modules/sales/application/src/main/java/tech/kayys/erp/sales/application/internal/command/SubmitOrderHandler.java`**:

```java
package tech.kayys.erp.sales.application.internal.command;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.sales.application.api.command.SubmitOrderCommand;
import tech.kayys.erp.sales.application.port.OrderEventPublisher;
import tech.kayys.erp.sales.domain.identifier.OrderId;
import tech.kayys.erp.sales.domain.repository.OrderRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * Handler for submitting orders.
 */
@UseCase("Submit an order for processing")
public class SubmitOrderHandler implements CommandHandler<SubmitOrderCommand, OrderId> {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    @Inject
    public SubmitOrderHandler(OrderRepository orderRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletionStage<OrderId> handle(SubmitOrderCommand command) {
        return orderRepository.findById(command.orderId())
            .thenCompose(orderOpt -> {
                if (orderOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Order not found: " + command.orderId())
                    );
                }

                Order order = orderOpt.get();
                
                // Submit the order (domain logic)
                order.submit();

                // Save the updated order
                return orderRepository.save(order)
                    .thenCompose(saved -> {
                        // Publish events
                        return eventPublisher.publishAllEvents(saved)
                            .thenApply(v -> saved.getId());
                    });
            });
    }
}
```

**`/modules/sales/application/src/main/java/tech/kayys/erp/sales/application/internal/command/ConfirmOrderHandler.java`**:

```java
package tech.kayys.erp.sales.application.internal.command;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.sales.application.api.command.ConfirmOrderCommand;
import tech.kayys.erp.sales.application.port.OrderEventPublisher;
import tech.kayys.erp.sales.application.port.ReserveInventoryPort;
import tech.kayys.erp.sales.domain.identifier.OrderId;
import tech.kayys.erp.sales.domain.repository.OrderRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for confirming orders.
 */
@UseCase("Confirm an order")
public class ConfirmOrderHandler implements CommandHandler<ConfirmOrderCommand, OrderId> {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final ReserveInventoryPort reserveInventoryPort;

    @Inject
    public ConfirmOrderHandler(
            OrderRepository orderRepository,
            OrderEventPublisher eventPublisher,
            ReserveInventoryPort reserveInventoryPort) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.reserveInventoryPort = reserveInventoryPort;
    }

    @Override
    public CompletionStage<OrderId> handle(ConfirmOrderCommand command) {
        return orderRepository.findById(command.orderId())
            .thenCompose(orderOpt -> {
                if (orderOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Order not found: " + command.orderId())
                    );
                }

                Order order = orderOpt.get();

                // 1. Reserve inventory
                return reserveInventoryPort.reserveForOrder(order)
                    .thenCompose(v -> {
                        // 2. Confirm the order
                        order.confirm();

                        // 3. Save and publish events
                        return orderRepository.save(order)
                            .thenCompose(saved -> 
                                eventPublisher.publishAllEvents(saved)
                                    .thenApply(x -> saved.getId())
                            );
                    });
            });
    }
}
```

**`/modules/sales/application/src/main/java/tech/kayys/erp/sales/application/port/ProductPricePort.java`**:

```java
package tech.kayys.erp.sales.application.port;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Port for retrieving product information from Catalog context.
 */
public interface ProductPricePort {

    /**
     * Gets the current price of a product.
     */
    CompletionStage<MoneyDto> getProductPrice(UUID productId);

    /**
     * Validates if a product exists and is active.
     */
    CompletionStage<Boolean> productExists(UUID productId);

    /**
     * Gets product details.
     */
    CompletionStage<ProductInfoDto> getProductInfo(UUID productId);

    /**
     * Data transfer object for money.
     */
    record MoneyDto(String amount, String currencyCode) {}

    /**
     * Data transfer object for product information.
     */
    record ProductInfoDto(
            UUID productId,
            String name,
            String sku,
            MoneyDto price,
            boolean active,
            int stockLevel
    ) {}
}
```

**`/modules/sales/application/src/main/java/tech/kayys/erp/sales/application/port/ReserveInventoryPort.java`**:

```java
package tech.kayys.erp.sales.application.port;

import tech.kayys.erp.sales.domain.model.Order;

import java.util.concurrent.CompletionStage;

/**
 * Port for reserving inventory from the Inventory context.
 */
public interface ReserveInventoryPort {

    /**
     * Reserves inventory for an order.
     */
    CompletionStage<Void> reserveForOrder(Order order);

    /**
     * Releases inventory reserved for an order.
     */
    CompletionStage<Void> releaseReservation(Order order);

    /**
     * Checks if inventory is available for an order.
     */
    CompletionStage<Boolean> isInventoryAvailable(Order order);

    /**
     * Gets inventory reservation status.
     */
    CompletionStage<ReservationStatus> getReservationStatus(Order order);

    enum ReservationStatus {
        PENDING,
        RESERVED,
        CONFIRMED,
        RELEASED,
        FAILED
    }
}
```

**`/modules/sales/application/src/main/java/tech/kayys/erp/sales/application/port/OrderEventPublisher.java`**:

```java
package tech.kayys.erp.sales.application.port;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.sales.domain.model.Order;

import java.util.concurrent.CompletionStage;

/**
 * Port for publishing order events to other bounded contexts.
 */
public interface OrderEventPublisher {

    /**
     * Publishes all events for an order.
     */
    CompletionStage<Void> publishAllEvents(Order order);

    /**
     * Publishes a specific domain event.
     */
    CompletionStage<Void> publishEvent(DomainEvent event);
}
```

## 3. Integration with Root POM

**Update `/pom.xml` to include Sales modules**:

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
</modules>
```

## 4. Update Architecture Tests

**`/architecture/tests/src/test/java/tech/kayys/erp/architecture/CompleteArchitectureTest.java`** (add Sales context rules):

```java
// Add to existing CompleteArchitectureTest class:

@ArchTest
static final ArchRule salesDomainMustNotDependOnCatalog =
        noClasses()
                .that()
                .resideInAPackage("tech.kayys.erp.sales.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("tech.kayys.erp.catalog.domain..");

@ArchTest
static final ArchRule salesApplicationMustNotDependOnCatalog =
        noClasses()
                .that()
                .resideInAPackage("tech.kayys.erp.sales.application..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("tech.kayys.erp.catalog..")
                .andShould()
                .haveFullyQualifiedName("tech.kayys.erp.catalog.domain.model.Product");

@ArchTest
static final ArchRule salesApplicationMayUseCatalogPorts =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.sales.application.port..")
                .should()
                .haveSimpleNameContaining("Port")
                .orShould()
                .haveSimpleNameContaining("Catalog");

@ArchTest
static final ArchRule salesDomainPackagesCorrect =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.sales.domain..")
                .should()
                .resideInAnyPackage(
                        "tech.kayys.erp.sales.domain.model..",
                        "tech.kayys.erp.sales.domain.identifier..",
                        "tech.kayys.erp.sales.domain.valueobject..",
                        "tech.kayys.erp.sales.domain.event..",
                        "tech.kayys.erp.sales.domain.repository.."
                );
```

## Summary

The complete Sales/Order implementation demonstrates:

1. **Domain Layer**:
   - Order aggregate with comprehensive business logic
   - OrderItem value object
   - Money value object (different from Catalog's Money)
   - CustomerId identifier (not referencing CRM's Customer)
   - Domain events for state changes
   - Status state machine with transition rules

2. **Application Layer**:
   - Commands for all order operations
   - Handlers implementing use cases
   - Ports for cross-context communication
   - Public API interfaces

3. **Cross-Context Integration**:
   - ProductPricePort to Catalog context
   - ReserveInventoryPort to Inventory context
   - OrderEventPublisher for event propagation
   - No direct dependencies on other contexts

4. **Architecture Rules Enforced**:
   - Sales domain doesn't depend on Catalog domain
   - Sales application uses ports, not direct dependencies
   - Proper package structure
   - Foundation remains pure

This demonstrates the **hexagonal architecture** pattern where each bounded context is self-contained and communicates with others through well-defined ports.
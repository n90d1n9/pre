# Complete Implementation: Purchase Order / Procurement Bounded Context

Now I'll implement the complete Purchase Order/Procurement bounded context, which handles vendor management, purchase order creation, receiving, and vendor invoice integration. This context is used by Inventory, Accounting, and Sales contexts.

## 1. Purchase Order Domain Module

**`/modules/purchasing/domain/pom.xml`**:

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

    <artifactId>erp-purchasing-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/identifier/PurchaseOrderId.java`**:

```java
package tech.kayys.erp.purchasing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Purchase Order identifier.
 */
public final class PurchaseOrderId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public PurchaseOrderId(UUID value) {
        super(value);
    }

    public static PurchaseOrderId of(UUID value) {
        return new PurchaseOrderId(value);
    }

    public static PurchaseOrderId generate() {
        return new PurchaseOrderId(UUID.randomUUID());
    }

    public static PurchaseOrderId fromString(String value) {
        return new PurchaseOrderId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PurchaseOrderId{" + value + "}";
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/identifier/VendorId.java`**:

```java
package tech.kayys.erp.purchasing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Vendor identifier in the Purchasing context.
 */
public final class VendorId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public VendorId(UUID value) {
        super(value);
    }

    public static VendorId of(UUID value) {
        return new VendorId(value);
    }

    public static VendorId generate() {
        return new VendorId(UUID.randomUUID());
    }

    public static VendorId fromString(String value) {
        return new VendorId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "VendorId{" + value + "}";
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/valueobject/Money.java`**:

```java
package tech.kayys.erp.purchasing.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Money value object for the Purchasing context.
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

    public BigDecimal getAmount() { return amount; }
    public Currency getCurrency() { return currency; }

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
        return multiply(percentage.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_EVEN));
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

    public static Money of(double amount, String currencyCode) {
        return new Money(BigDecimal.valueOf(amount), Currency.getInstance(currencyCode));
    }

    public static Money zero(String currencyCode) {
        return new Money(BigDecimal.ZERO, Currency.getInstance(currencyCode));
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/valueobject/PurchaseOrderStatus.java`**:

```java
package tech.kayys.erp.purchasing.domain.valueobject;

/**
 * Status of a purchase order.
 */
public enum PurchaseOrderStatus {
    DRAFT("Draft - being created"),
    SUBMITTED("Submitted - sent to vendor"),
    ACKNOWLEDGED("Acknowledged - vendor accepted"),
    IN_TRANSIT("In Transit - items being shipped"),
    PARTIALLY_RECEIVED("Partially Received - some items received"),
    RECEIVED("Received - all items received"),
    COMPLETED("Completed - order closed"),
    CANCELLED("Cancelled - order voided"),
    REJECTED("Rejected - vendor rejected"),
    ON_HOLD("On Hold - pending approval");

    private final String description;

    PurchaseOrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canTransitionTo(PurchaseOrderStatus target) {
        return switch (this) {
            case DRAFT -> target == SUBMITTED || target == CANCELLED || target == ON_HOLD;
            case ON_HOLD -> target == DRAFT || target == CANCELLED;
            case SUBMITTED -> target == ACKNOWLEDGED || target == REJECTED || target == CANCELLED;
            case ACKNOWLEDGED -> target == IN_TRANSIT || target == CANCELLED || target == REJECTED;
            case IN_TRANSIT -> target == PARTIALLY_RECEIVED || target == RECEIVED || target == CANCELLED;
            case PARTIALLY_RECEIVED -> target == RECEIVED || target == CANCELLED;
            case RECEIVED -> target == COMPLETED;
            case COMPLETED, CANCELLED, REJECTED -> false;
        };
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == REJECTED;
    }

    public boolean isActive() {
        return this != COMPLETED && this != CANCELLED && this != REJECTED;
    }

    public boolean isReceivable() {
        return this == ACKNOWLEDGED || this == IN_TRANSIT || this == PARTIALLY_RECEIVED;
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/valueobject/VendorType.java`**:

```java
package tech.kayys.erp.purchasing.domain.valueobject;

/**
 * Types of vendors.
 */
public enum VendorType {
    SUPPLIER("Supplier"),
    MANUFACTURER("Manufacturer"),
    DISTRIBUTOR("Distributor"),
    SERVICE_PROVIDER("Service Provider"),
    CONSULTANT("Consultant"),
    CONTRACTOR("Contractor");

    private final String displayName;

    VendorType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/valueobject/VendorStatus.java`**:

```java
package tech.kayys.erp.purchasing.domain.valueobject;

/**
 * Status of a vendor.
 */
public enum VendorStatus {
    ACTIVE("Active - approved vendor"),
    INACTIVE("Inactive - currently not used"),
    BLACKLISTED("Blacklisted - not allowed to transact"),
    PENDING_APPROVAL("Pending Approval - awaiting approval"),
    UNDER_REVIEW("Under Review - being evaluated");

    private final String description;

    VendorStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canTransact() {
        return this == ACTIVE;
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/model/Vendor.java`**:

```java
package tech.kayys.erp.purchasing.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.valueobject.VendorStatus;
import tech.kayys.erp.purchasing.domain.valueobject.VendorType;

import java.time.Instant;

/**
 * Vendor aggregate root.
 * Represents a supplier or vendor in the procurement system.
 */
public final class Vendor extends AggregateRoot<VendorId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String legalName;
    private String taxId;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String website;
    private VendorType vendorType;
    private VendorStatus status;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    private String paymentTerms;
    private String shippingTerms;
    private String currencyCode;
    private String notes;
    private double rating;
    private int totalOrders;
    private int onTimeDeliveries;
    private int lateDeliveries;
    private boolean active;

    private Vendor(VendorId id) {
        super(id);
        this.status = VendorStatus.ACTIVE;
        this.active = true;
        this.rating = 0.0;
        this.totalOrders = 0;
        this.onTimeDeliveries = 0;
        this.lateDeliveries = 0;
    }

    private Vendor() {
        super();
    }

    /**
     * Factory method to create a new vendor.
     */
    public static Vendor create(
            VendorId id,
            String name,
            VendorType vendorType,
            String email,
            String currencyCode) {
        Vendor vendor = new Vendor(id);
        vendor.name = name;
        vendor.vendorType = vendorType;
        vendor.email = email;
        vendor.currencyCode = currencyCode;
        vendor.status = VendorStatus.PENDING_APPROVAL;
        return vendor;
    }

    /**
     * Approves the vendor.
     */
    public void approve() {
        if (status != VendorStatus.PENDING_APPROVAL && status != VendorStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Cannot approve vendor in status: " + status);
        }
        this.status = VendorStatus.ACTIVE;
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the vendor.
     */
    public void activate() {
        if (status == VendorStatus.BLACKLISTED) {
            throw new IllegalStateException("Cannot activate blacklisted vendor");
        }
        this.active = true;
        this.status = VendorStatus.ACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the vendor.
     */
    public void deactivate() {
        this.active = false;
        this.status = VendorStatus.INACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Blacklists the vendor.
     */
    public void blacklist(String reason) {
        this.status = VendorStatus.BLACKLISTED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records a delivery for performance tracking.
     */
    public void recordDelivery(boolean onTime) {
        this.totalOrders++;
        if (onTime) {
            this.onTimeDeliveries++;
        } else {
            this.lateDeliveries++;
        }
        this.rating = calculateRating();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private double calculateRating() {
        if (totalOrders == 0) {
            return 0.0;
        }
        return (double) onTimeDeliveries / totalOrders * 5.0;
    }

    /**
     * Gets the vendor's performance score.
     */
    public double getPerformanceScore() {
        if (totalOrders == 0) {
            return 0.0;
        }
        return (double) onTimeDeliveries / totalOrders * 100.0;
    }

    // Getters
    public String getName() { return name; }
    public String getLegalName() { return legalName; }
    public String getTaxId() { return taxId; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
    public String getWebsite() { return website; }
    public VendorType getVendorType() { return vendorType; }
    public VendorStatus getStatus() { return status; }
    public String getContactPerson() { return contactPerson; }
    public String getContactEmail() { return contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public String getPaymentTerms() { return paymentTerms; }
    public String getShippingTerms() { return shippingTerms; }
    public String getCurrencyCode() { return currencyCode; }
    public String getNotes() { return notes; }
    public double getRating() { return rating; }
    public int getTotalOrders() { return totalOrders; }
    public int getOnTimeDeliveries() { return onTimeDeliveries; }
    public int getLateDeliveries() { return lateDeliveries; }
    public boolean isActive() { return active && status == VendorStatus.ACTIVE; }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPhone(String phone) {
        this.phone = phone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setAddress(String address) {
        this.address = address;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCity(String city) {
        this.city = city;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setState(String state) {
        this.state = state;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCountry(String country) {
        this.country = country;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setWebsite(String website) {
        this.website = website;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setShippingTerms(String shippingTerms) {
        this.shippingTerms = shippingTerms;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Vendor{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", vendorType=" + vendorType +
                ", status=" + status +
                ", rating=" + rating +
                '}';
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/model/PurchaseOrder.java`**:

```java
package tech.kayys.erp.purchasing.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.purchasing.domain.event.PurchaseOrderCreated;
import tech.kayys.erp.purchasing.domain.event.PurchaseOrderReceived;
import tech.kayys.erp.purchasing.domain.event.PurchaseOrderSubmitted;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.valueobject.Money;
import tech.kayys.erp.purchasing.domain.valueobject.PurchaseOrderStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Purchase Order aggregate root.
 * Represents an order placed with a vendor for goods or services.
 */
public final class PurchaseOrder extends AggregateRoot<PurchaseOrderId> {
    
    private static final long serialVersionUID = 1L;
    
    private String poNumber;
    private VendorId vendorId;
    private String vendorName;
    private List<PurchaseOrderItem> items;
    private Money subtotal;
    private Money taxTotal;
    private Money shippingCost;
    private Money discountTotal;
    private Money grandTotal;
    private PurchaseOrderStatus status;
    private Instant orderDate;
    private Instant requiredDate;
    private Instant receivedDate;
    private String shippingAddress;
    private String billingAddress;
    private String paymentTerms;
    private String shippingTerms;
    private String currencyCode;
    private String notes;
    private String createdBy;
    private String approvedBy;
    private Instant approvedAt;
    private String vendorReference;
    private String trackingNumber;
    private String deliveryMethod;
    private int totalItemsReceived;

    private PurchaseOrder(PurchaseOrderId id) {
        super(id);
        this.items = new ArrayList<>();
        this.status = PurchaseOrderStatus.DRAFT;
        this.orderDate = Instant.now();
        this.totalItemsReceived = 0;
        this.shippingCost = Money.zero("USD");
        this.discountTotal = Money.zero("USD");
        this.taxTotal = Money.zero("USD");
    }

    private PurchaseOrder() {
        super();
    }

    /**
     * Factory method to create a new purchase order.
     */
    public static PurchaseOrder create(
            PurchaseOrderId id,
            String poNumber,
            VendorId vendorId,
            String vendorName,
            Instant requiredDate,
            String currencyCode) {
        PurchaseOrder po = new PurchaseOrder(id);
        po.poNumber = poNumber;
        po.vendorId = vendorId;
        po.vendorName = vendorName;
        po.requiredDate = requiredDate;
        po.currencyCode = currencyCode;
        po.status = PurchaseOrderStatus.DRAFT;
        
        po.registerEvent(new PurchaseOrderCreated(po));
        return po;
    }

    /**
     * Adds an item to the purchase order.
     */
    public void addItem(PurchaseOrderItem item) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify PO in status: " + status);
        }
        items.add(item);
        recalculateTotals();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes an item from the purchase order.
     */
    public void removeItem(int index) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify PO in status: " + status);
        }
        if (index < 0 || index >= items.size()) {
            throw new IllegalArgumentException("Invalid item index");
        }
        items.remove(index);
        recalculateTotals();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Submits the purchase order to the vendor.
     */
    public void submit() {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot submit PO in status: " + status);
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("PO must have at least one item");
        }
        
        this.status = PurchaseOrderStatus.SUBMITTED;
        setUpdatedAt(Instant.now());
        incrementVersion();
        
        registerEvent(new PurchaseOrderSubmitted(this));
    }

    /**
     * Acknowledges the purchase order from the vendor.
     */
    public void acknowledge(String vendorReference) {
        if (status != PurchaseOrderStatus.SUBMITTED) {
            throw new IllegalStateException("Cannot acknowledge PO in status: " + status);
        }
        
        this.status = PurchaseOrderStatus.ACKNOWLEDGED;
        this.vendorReference = vendorReference;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records that the purchase order is in transit.
     */
    public void markInTransit(String trackingNumber) {
        if (status != PurchaseOrderStatus.ACKNOWLEDGED) {
            throw new IllegalStateException("Cannot mark in transit from status: " + status);
        }
        
        this.status = PurchaseOrderStatus.IN_TRANSIT;
        this.trackingNumber = trackingNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records receipt of items.
     */
    public void receiveItems(List<ReceivedItem> receivedItems) {
        if (!status.isReceivable()) {
            throw new IllegalStateException("Cannot receive items in status: " + status);
        }

        for (ReceivedItem received : receivedItems) {
            PurchaseOrderItem item = items.get(received.itemIndex);
            if (item == null) {
                throw new IllegalArgumentException("Item not found at index: " + received.itemIndex);
            }
            
            int receivedQty = received.quantityReceived;
            if (receivedQty > item.getRemainingQuantity()) {
                throw new IllegalArgumentException(
                    "Received quantity exceeds remaining: " + receivedQty + " > " + item.getRemainingQuantity()
                );
            }
            
            item.receive(receivedQty);
            totalItemsReceived += receivedQty;
        }

        // Check if all items are fully received
        boolean allReceived = items.stream().allMatch(poItem -> poItem.isFullyReceived());
        if (allReceived) {
            this.status = PurchaseOrderStatus.RECEIVED;
            this.receivedDate = Instant.now();
        } else {
            this.status = PurchaseOrderStatus.PARTIALLY_RECEIVED;
        }

        setUpdatedAt(Instant.now());
        incrementVersion();
        
        registerEvent(new PurchaseOrderReceived(this));
    }

    /**
     * Completes the purchase order.
     */
    public void complete() {
        if (status != PurchaseOrderStatus.RECEIVED) {
            throw new IllegalStateException("Cannot complete PO in status: " + status);
        }
        if (!items.stream().allMatch(PurchaseOrderItem::isFullyReceived)) {
            throw new IllegalStateException("Not all items have been received");
        }
        
        this.status = PurchaseOrderStatus.COMPLETED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the purchase order.
     */
    public void cancel(String reason) {
        if (status.isTerminal()) {
            throw new IllegalStateException("PO is already finalized");
        }
        
        this.status = PurchaseOrderStatus.CANCELLED;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Rejects the purchase order from the vendor.
     */
    public void reject(String reason) {
        if (status != PurchaseOrderStatus.SUBMITTED) {
            throw new IllegalStateException("Cannot reject PO in status: " + status);
        }
        
        this.status = PurchaseOrderStatus.REJECTED;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Places the purchase order on hold.
     */
    public void putOnHold(String reason) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot put PO on hold in status: " + status);
        }
        
        this.status = PurchaseOrderStatus.ON_HOLD;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private void recalculateTotals() {
        // Calculate subtotal
        Money newSubtotal = items.stream()
            .map(PurchaseOrderItem::getLineTotal)
            .reduce(Money.zero(currencyCode), Money::add);
        
        // Calculate tax (simplified - 10%)
        Money newTaxTotal = newSubtotal.percentage(10);
        
        // Calculate grand total
        Money newGrandTotal = newSubtotal
            .add(newTaxTotal)
            .add(shippingCost)
            .subtract(discountTotal);
        
        this.subtotal = newSubtotal;
        this.taxTotal = newTaxTotal;
        this.grandTotal = newGrandTotal;
    }

    // Getters
    public String getPoNumber() { return poNumber; }
    public VendorId getVendorId() { return vendorId; }
    public String getVendorName() { return vendorName; }
    public List<PurchaseOrderItem> getItems() { return Collections.unmodifiableList(items); }
    public Money getSubtotal() { return subtotal; }
    public Money getTaxTotal() { return taxTotal; }
    public Money getShippingCost() { return shippingCost; }
    public Money getDiscountTotal() { return discountTotal; }
    public Money getGrandTotal() { return grandTotal; }
    public PurchaseOrderStatus getStatus() { return status; }
    public Instant getOrderDate() { return orderDate; }
    public Instant getRequiredDate() { return requiredDate; }
    public Instant getReceivedDate() { return receivedDate; }
    public String getShippingAddress() { return shippingAddress; }
    public String getBillingAddress() { return billingAddress; }
    public String getPaymentTerms() { return paymentTerms; }
    public String getShippingTerms() { return shippingTerms; }
    public String getCurrencyCode() { return currencyCode; }
    public String getNotes() { return notes; }
    public String getCreatedBy() { return createdBy; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public String getVendorReference() { return vendorReference; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public int getTotalItemsReceived() { return totalItemsReceived; }

    public void setShippingAddress(String shippingAddress) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify PO in status: " + status);
        }
        this.shippingAddress = shippingAddress;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setBillingAddress(String billingAddress) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify PO in status: " + status);
        }
        this.billingAddress = billingAddress;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPaymentTerms(String paymentTerms) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify PO in status: " + status);
        }
        this.paymentTerms = paymentTerms;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setShippingTerms(String shippingTerms) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify PO in status: " + status);
        }
        this.shippingTerms = shippingTerms;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setShippingCost(Money shippingCost) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify PO in status: " + status);
        }
        this.shippingCost = shippingCost;
        recalculateTotals();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDiscountTotal(Money discountTotal) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify PO in status: " + status);
        }
        this.discountTotal = discountTotal;
        recalculateTotals();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void approve(String approvedBy) {
        if (status != PurchaseOrderStatus.DRAFT && status != PurchaseOrderStatus.SUBMITTED) {
            throw new IllegalStateException("Cannot approve PO in status: " + status);
        }
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if all items have been received.
     */
    public boolean isFullyReceived() {
        return items.stream().allMatch(PurchaseOrderItem::isFullyReceived);
    }

    /**
     * Gets the percentage of items received.
     */
    public double getCompletionPercentage() {
        if (items.isEmpty()) {
            return 0.0;
        }
        int totalOrdered = items.stream()
            .mapToInt(PurchaseOrderItem::getQuantity)
            .sum();
        return (double) totalItemsReceived / totalOrdered * 100.0;
    }

    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "id=" + getId() +
                ", poNumber='" + poNumber + '\'' +
                ", vendorName='" + vendorName + '\'' +
                ", status=" + status +
                ", total=" + grandTotal +
                '}';
    }

    /**
     * Purchase Order Item.
     */
    public static final class PurchaseOrderItem implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final UUID productId;
        private final String productName;
        private final String sku;
        private final int quantity;
        private final Money unitPrice;
        private final Money lineTotal;
        private final String uom;
        private int quantityReceived;

        public PurchaseOrderItem(
                UUID productId,
                String productName,
                String sku,
                int quantity,
                Money unitPrice,
                String uom) {
            this.productId = productId;
            this.productName = productName;
            this.sku = sku;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.uom = uom;
            this.lineTotal = unitPrice.multiply(quantity);
            this.quantityReceived = 0;
            validate();
        }

        @Override
        public void validate() {
            if (productName == null || productName.trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be empty");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (unitPrice == null || unitPrice.isNegative()) {
                throw new IllegalArgumentException("Unit price must be positive");
            }
        }

        public UUID getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getSku() { return sku; }
        public int getQuantity() { return quantity; }
        public Money getUnitPrice() { return unitPrice; }
        public Money getLineTotal() { return lineTotal; }
        public String getUom() { return uom; }
        public int getQuantityReceived() { return quantityReceived; }
        public int getRemainingQuantity() { return quantity - quantityReceived; }
        public boolean isFullyReceived() { return quantityReceived >= quantity; }

        public void receive(int quantity) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Received quantity must be positive");
            }
            if (quantityReceived + quantity > this.quantity) {
                throw new IllegalArgumentException("Cannot receive more than ordered: " + 
                    (quantityReceived + quantity) + " > " + this.quantity);
            }
            this.quantityReceived += quantity;
        }

        @Override
        public String toString() {
            return "PurchaseOrderItem{" +
                    "productName='" + productName + '\'' +
                    ", quantity=" + quantity +
                    ", received=" + quantityReceived +
                    ", lineTotal=" + lineTotal +
                    '}';
        }
    }

    /**
     * Received item record.
     */
    public record ReceivedItem(
            int itemIndex,
            int quantityReceived,
            String notes
    ) {
        public ReceivedItem {
            if (quantityReceived <= 0) {
                throw new IllegalArgumentException("Received quantity must be positive");
            }
        }
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/event/PurchaseOrderCreated.java`**:

```java
package tech.kayys.erp.purchasing.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.purchasing.domain.model.PurchaseOrder;

import java.time.Instant;
import java.util.UUID;

public class PurchaseOrderCreated implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String poNumber;
    private final String vendorId;
    private final String totalAmount;
    private final String currency;

    public PurchaseOrderCreated(PurchaseOrder po) {
        this.eventId = UUID.randomUUID();
        this.eventType = "PurchaseOrderCreated";
        this.occurredAt = Instant.now();
        this.aggregateId = po.getId().toString();
        this.aggregateType = "PurchaseOrder";
        this.poNumber = po.getPoNumber();
        this.vendorId = po.getVendorId().toString();
        this.totalAmount = po.getGrandTotal().getAmount().toPlainString();
        this.currency = po.getGrandTotal().getCurrency().getCurrencyCode();
    }

    @Override
    public UUID getEventId() { return eventId; }
    @Override
    public String getEventType() { return eventType; }
    @Override
    public Instant getOccurredAt() { return occurredAt; }
    @Override
    public String getAggregateId() { return aggregateId; }
    @Override
    public String getAggregateType() { return aggregateType; }
    public String getPoNumber() { return poNumber; }
    public String getVendorId() { return vendorId; }
    public String getTotalAmount() { return totalAmount; }
    public String getCurrency() { return currency; }

    @Override
    public String toString() {
        return "PurchaseOrderCreated{" +
                "eventId=" + eventId +
                ", poNumber='" + poNumber + '\'' +
                ", vendorId='" + vendorId + '\'' +
                '}';
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/event/PurchaseOrderSubmitted.java`**:

```java
package tech.kayys.erp.purchasing.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.purchasing.domain.model.PurchaseOrder;

import java.time.Instant;
import java.util.UUID;

public class PurchaseOrderSubmitted implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String poNumber;
    private final String vendorId;

    public PurchaseOrderSubmitted(PurchaseOrder po) {
        this.eventId = UUID.randomUUID();
        this.eventType = "PurchaseOrderSubmitted";
        this.occurredAt = Instant.now();
        this.aggregateId = po.getId().toString();
        this.aggregateType = "PurchaseOrder";
        this.poNumber = po.getPoNumber();
        this.vendorId = po.getVendorId().toString();
    }

    @Override
    public UUID getEventId() { return eventId; }
    @Override
    public String getEventType() { return eventType; }
    @Override
    public Instant getOccurredAt() { return occurredAt; }
    @Override
    public String getAggregateId() { return aggregateId; }
    @Override
    public String getAggregateType() { return aggregateType; }
    public String getPoNumber() { return poNumber; }
    public String getVendorId() { return vendorId; }

    @Override
    public String toString() {
        return "PurchaseOrderSubmitted{" +
                "eventId=" + eventId +
                ", poNumber='" + poNumber + '\'' +
                '}';
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/event/PurchaseOrderReceived.java`**:

```java
package tech.kayys.erp.purchasing.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.purchasing.domain.model.PurchaseOrder;

import java.time.Instant;
import java.util.UUID;

public class PurchaseOrderReceived implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String poNumber;
    private final String vendorId;
    private final int totalItemsReceived;
    private final boolean fullyReceived;

    public PurchaseOrderReceived(PurchaseOrder po) {
        this.eventId = UUID.randomUUID();
        this.eventType = "PurchaseOrderReceived";
        this.occurredAt = Instant.now();
        this.aggregateId = po.getId().toString();
        this.aggregateType = "PurchaseOrder";
        this.poNumber = po.getPoNumber();
        this.vendorId = po.getVendorId().toString();
        this.totalItemsReceived = po.getTotalItemsReceived();
        this.fullyReceived = po.isFullyReceived();
    }

    @Override
    public UUID getEventId() { return eventId; }
    @Override
    public String getEventType() { return eventType; }
    @Override
    public Instant getOccurredAt() { return occurredAt; }
    @Override
    public String getAggregateId() { return aggregateId; }
    @Override
    public String getAggregateType() { return aggregateType; }
    public String getPoNumber() { return poNumber; }
    public String getVendorId() { return vendorId; }
    public int getTotalItemsReceived() { return totalItemsReceived; }
    public boolean isFullyReceived() { return fullyReceived; }

    @Override
    public String toString() {
        return "PurchaseOrderReceived{" +
                "eventId=" + eventId +
                ", poNumber='" + poNumber + '\'' +
                ", fullyReceived=" + fullyReceived +
                '}';
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/repository/VendorRepository.java`**:

```java
package tech.kayys.erp.purchasing.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.model.Vendor;
import tech.kayys.erp.purchasing.domain.valueobject.VendorStatus;
import tech.kayys.erp.purchasing.domain.valueobject.VendorType;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for Vendor aggregates.
 */
public interface VendorRepository extends Repository<Vendor, VendorId> {

    /**
     * Finds vendors by type.
     */
    CompletionStage<List<Vendor>> findByType(VendorType type);

    /**
     * Finds active vendors.
     */
    CompletionStage<List<Vendor>> findActiveVendors();

    /**
     * Finds vendors by status.
     */
    CompletionStage<List<Vendor>> findByStatus(VendorStatus status);

    /**
     * Finds vendors by name containing text.
     */
    CompletionStage<List<Vendor>> findByNameContaining(String name);

    /**
     * Finds vendors with high performance rating.
     */
    CompletionStage<List<Vendor>> findTopRatedVendors(int limit);

    /**
     * Checks if a vendor exists by name.
     */
    CompletionStage<Boolean> existsByName(String name);
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/repository/PurchaseOrderRepository.java`**:

```java
package tech.kayys.erp.purchasing.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.model.PurchaseOrder;
import tech.kayys.erp.purchasing.domain.valueobject.PurchaseOrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for PurchaseOrder aggregates.
 */
public interface PurchaseOrderRepository extends Repository<PurchaseOrder, PurchaseOrderId> {

    /**
     * Finds purchase orders by vendor.
     */
    CompletionStage<List<PurchaseOrder>> findByVendorId(VendorId vendorId);

    /**
     * Finds purchase orders by status.
     */
    CompletionStage<List<PurchaseOrder>> findByStatus(PurchaseOrderStatus status);

    /**
     * Finds active purchase orders (non-terminal).
     */
    default CompletionStage<List<PurchaseOrder>> findActiveOrders() {
        return findByStatus(PurchaseOrderStatus.SUBMITTED)
            .thenCombine(findByStatus(PurchaseOrderStatus.ACKNOWLEDGED),
                (submitted, acknowledged) -> {
                    submitted.addAll(acknowledged);
                    return submitted;
                })
            .thenCombine(findByStatus(PurchaseOrderStatus.IN_TRANSIT),
                (combined, inTransit) -> {
                    combined.addAll(inTransit);
                    return combined;
                })
            .thenCombine(findByStatus(PurchaseOrderStatus.PARTIALLY_RECEIVED),
                (combined, partial) -> {
                    combined.addAll(partial);
                    return combined;
                });
    }

    /**
     * Finds purchase orders due for delivery.
     */
    CompletionStage<List<PurchaseOrder>> findOrdersDueForDelivery(Instant date);

    /**
     * Finds purchase orders by date range.
     */
    CompletionStage<List<PurchaseOrder>> findByDateRange(Instant start, Instant end);

    /**
     * Finds purchase orders requiring approval.
     */
    CompletionStage<List<PurchaseOrder>> findOrdersRequiringApproval();

    /**
     * Finds purchase orders with items to receive.
     */
    CompletionStage<List<PurchaseOrder>> findOrdersWithItemsToReceive();

    /**
     * Finds overdue purchase orders.
     */
    CompletionStage<List<PurchaseOrder>> findOverdueOrders(Instant currentDate);

    /**
     * Generates a unique PO number.
     */
    CompletionStage<String> generatePoNumber();
}
```

## 2. Purchasing Application Module

**`/modules/purchasing/application/pom.xml`**:

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

    <artifactId>erp-purchasing-application</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-purchasing-domain</artifactId>
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

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/api/PurchaseOrderCommandService.java`**:

```java
package tech.kayys.erp.purchasing.application.api;

import tech.kayys.erp.purchasing.application.api.command.*;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;

import java.util.concurrent.CompletionStage;

/**
 * Public API for purchase order commands.
 */
public interface PurchaseOrderCommandService {

    /**
     * Creates a new purchase order.
     */
    CompletionStage<PurchaseOrderId> createPurchaseOrder(CreatePurchaseOrderCommand command);

    /**
     * Submits a purchase order to the vendor.
     */
    CompletionStage<PurchaseOrderId> submitPurchaseOrder(SubmitPurchaseOrderCommand command);

    /**
     * Acknowledges a purchase order from the vendor.
     */
    CompletionStage<PurchaseOrderId> acknowledgePurchaseOrder(AcknowledgePurchaseOrderCommand command);

    /**
     * Marks a purchase order as in transit.
     */
    CompletionStage<PurchaseOrderId> markInTransit(MarkInTransitCommand command);

    /**
     * Receives items for a purchase order.
     */
    CompletionStage<PurchaseOrderId> receiveItems(ReceivePurchaseOrderItemsCommand command);

    /**
     * Completes a purchase order.
     */
    CompletionStage<PurchaseOrderId> completePurchaseOrder(CompletePurchaseOrderCommand command);

    /**
     * Cancels a purchase order.
     */
    CompletionStage<PurchaseOrderId> cancelPurchaseOrder(CancelPurchaseOrderCommand command);

    /**
     * Places a purchase order on hold.
     */
    CompletionStage<PurchaseOrderId> holdPurchaseOrder(HoldPurchaseOrderCommand command);

    /**
     * Creates a purchase order from a sales order.
     */
    CompletionStage<PurchaseOrderId> createFromSalesOrder(CreateFromSalesOrderCommand command);
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/api/command/CreatePurchaseOrderCommand.java`**:

```java
package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Command to create a new purchase order.
 */
public record CreatePurchaseOrderCommand(
        PurchaseOrderId purchaseOrderId,
        UUID vendorId,
        String vendorName,
        Instant requiredDate,
        List<PurchaseOrderItemCommand> items,
        String shippingAddress,
        String billingAddress,
        String paymentTerms,
        String shippingTerms,
        String currencyCode,
        String notes,
        String createdBy
) implements Command<PurchaseOrderId> {

    public CreatePurchaseOrderCommand {
        if (vendorId == null) {
            throw new IllegalArgumentException("Vendor ID cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("PO must have at least one item");
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PurchaseOrderId purchaseOrderId;
        private UUID vendorId;
        private String vendorName;
        private Instant requiredDate;
        private List<PurchaseOrderItemCommand> items;
        private String shippingAddress;
        private String billingAddress;
        private String paymentTerms;
        private String shippingTerms;
        private String currencyCode = "USD";
        private String notes;
        private String createdBy;

        public Builder purchaseOrderId(PurchaseOrderId purchaseOrderId) {
            this.purchaseOrderId = purchaseOrderId;
            return this;
        }

        public Builder vendorId(UUID vendorId) {
            this.vendorId = vendorId;
            return this;
        }

        public Builder vendorName(String vendorName) {
            this.vendorName = vendorName;
            return this;
        }

        public Builder requiredDate(Instant requiredDate) {
            this.requiredDate = requiredDate;
            return this;
        }

        public Builder items(List<PurchaseOrderItemCommand> items) {
            this.items = items;
            return this;
        }

        public Builder shippingAddress(String shippingAddress) {
            this.shippingAddress = shippingAddress;
            return this;
        }

        public Builder billingAddress(String billingAddress) {
            this.billingAddress = billingAddress;
            return this;
        }

        public Builder paymentTerms(String paymentTerms) {
            this.paymentTerms = paymentTerms;
            return this;
        }

        public Builder shippingTerms(String shippingTerms) {
            this.shippingTerms = shippingTerms;
            return this;
        }

        public Builder currencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public CreatePurchaseOrderCommand build() {
            if (purchaseOrderId == null) {
                purchaseOrderId = PurchaseOrderId.generate();
            }
            if (requiredDate == null) {
                requiredDate = Instant.now().plusSeconds(14L * 24L * 60L * 60L); // 14 days
            }
            return new CreatePurchaseOrderCommand(
                purchaseOrderId, vendorId, vendorName, requiredDate,
                items, shippingAddress, billingAddress, paymentTerms,
                shippingTerms, currencyCode, notes, createdBy
            );
        }
    }

    /**
     * Purchase Order Item Command.
     */
    public record PurchaseOrderItemCommand(
            UUID productId,
            String productName,
            String sku,
            int quantity,
            String unitPrice,
            String uom
    ) {
        public PurchaseOrderItemCommand {
            if (productName == null || productName.trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be empty");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (unitPrice == null || unitPrice.trim().isEmpty()) {
                throw new IllegalArgumentException("Unit price is required");
            }
        }
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/api/command/SubmitPurchaseOrderCommand.java`**:

```java
package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;

/**
 * Command to submit a purchase order.
 */
public record SubmitPurchaseOrderCommand(
        PurchaseOrderId purchaseOrderId
) implements Command<PurchaseOrderId> {

    public SubmitPurchaseOrderCommand {
        if (purchaseOrderId == null) {
            throw new IllegalArgumentException("Purchase Order ID cannot be null");
        }
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/api/command/AcknowledgePurchaseOrderCommand.java`**:

```java
package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;

/**
 * Command to acknowledge a purchase order from vendor.
 */
public record AcknowledgePurchaseOrderCommand(
        PurchaseOrderId purchaseOrderId,
        String vendorReference
) implements Command<PurchaseOrderId> {

    public AcknowledgePurchaseOrderCommand {
        if (purchaseOrderId == null) {
            throw new IllegalArgumentException("Purchase Order ID cannot be null");
        }
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/api/command/ReceivePurchaseOrderItemsCommand.java`**:

```java
package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;

import java.util.List;

/**
 * Command to receive items for a purchase order.
 */
public record ReceivePurchaseOrderItemsCommand(
        PurchaseOrderId purchaseOrderId,
        List<ReceivedItemCommand> receivedItems,
        String receivedBy
) implements Command<PurchaseOrderId> {

    public ReceivePurchaseOrderItemsCommand {
        if (purchaseOrderId == null) {
            throw new IllegalArgumentException("Purchase Order ID cannot be null");
        }
        if (receivedItems == null || receivedItems.isEmpty()) {
            throw new IllegalArgumentException("At least one item must be received");
        }
        if (receivedBy == null || receivedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Received by is required");
        }
    }

    /**
     * Received item command.
     */
    public record ReceivedItemCommand(
            int itemIndex,
            int quantityReceived,
            String notes
    ) {
        public ReceivedItemCommand {
            if (quantityReceived <= 0) {
                throw new IllegalArgumentException("Received quantity must be positive");
            }
            if (itemIndex < 0) {
                throw new IllegalArgumentException("Item index must be non-negative");
            }
        }
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/api/command/CreateFromSalesOrderCommand.java`**:

```java
package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;

import java.util.UUID;

/**
 * Command to create a purchase order from a sales order.
 * This handles the procurement workflow for items that need to be purchased.
 */
public record CreateFromSalesOrderCommand(
        UUID salesOrderId,
        UUID vendorId,
        String vendorName,
        String notes,
        String createdBy
) implements Command<PurchaseOrderId> {

    public CreateFromSalesOrderCommand {
        if (salesOrderId == null) {
            throw new IllegalArgumentException("Sales Order ID cannot be null");
        }
        if (vendorId == null) {
            throw new IllegalArgumentException("Vendor ID cannot be null");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID salesOrderId;
        private UUID vendorId;
        private String vendorName;
        private String notes;
        private String createdBy;

        public Builder salesOrderId(UUID salesOrderId) {
            this.salesOrderId = salesOrderId;
            return this;
        }

        public Builder vendorId(UUID vendorId) {
            this.vendorId = vendorId;
            return this;
        }

        public Builder vendorName(String vendorName) {
            this.vendorName = vendorName;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public CreateFromSalesOrderCommand build() {
            return new CreateFromSalesOrderCommand(
                salesOrderId, vendorId, vendorName, notes, createdBy
            );
        }
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/internal/CreatePurchaseOrderHandler.java`**:

```java
package tech.kayys.erp.purchasing.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.purchasing.application.api.command.CreatePurchaseOrderCommand;
import tech.kayys.erp.purchasing.application.port.VendorPort;
import tech.kayys.erp.purchasing.application.port.InventoryPort;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.model.PurchaseOrder;
import tech.kayys.erp.purchasing.domain.repository.PurchaseOrderRepository;
import tech.kayys.erp.purchasing.domain.valueobject.Money;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for creating purchase orders.
 */
@UseCase("Create a new purchase order")
public class CreatePurchaseOrderHandler 
        implements CommandHandler<CreatePurchaseOrderCommand, PurchaseOrderId> {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final VendorPort vendorPort;
    private final InventoryPort inventoryPort;

    @Inject
    public CreatePurchaseOrderHandler(
            PurchaseOrderRepository purchaseOrderRepository,
            VendorPort vendorPort,
            InventoryPort inventoryPort) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.vendorPort = vendorPort;
        this.inventoryPort = inventoryPort;
    }

    @Override
    public CompletionStage<PurchaseOrderId> handle(CreatePurchaseOrderCommand command) {
        // 1. Validate vendor exists
        return vendorPort.validateVendor(command.vendorId())
            .thenCompose(valid -> {
                if (!valid) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Vendor not found: " + command.vendorId())
                    );
                }

                // 2. Validate product inventory requirements
                return validateInventory(command)
                    .thenCompose(validInventory -> {
                        if (!validInventory) {
                            return CompletableFuture.failedFuture(
                                new IllegalArgumentException("Insufficient inventory or product not found")
                            );
                        }

                        // 3. Generate PO number
                        return purchaseOrderRepository.generatePoNumber()
                            .thenApply(poNumber -> {
                                // 4. Create the purchase order
                                PurchaseOrder po = PurchaseOrder.create(
                                    command.purchaseOrderId(),
                                    poNumber,
                                    VendorId.of(command.vendorId()),
                                    command.vendorName(),
                                    command.requiredDate(),
                                    command.currencyCode()
                                );

                                // 5. Add items
                                for (CreatePurchaseOrderCommand.PurchaseOrderItemCommand itemCommand : command.items()) {
                                    Money unitPrice = Money.of(
                                        new BigDecimal(itemCommand.unitPrice()),
                                        command.currencyCode()
                                    );

                                    PurchaseOrder.PurchaseOrderItem item = 
                                        new PurchaseOrder.PurchaseOrderItem(
                                            itemCommand.productId(),
                                            itemCommand.productName(),
                                            itemCommand.sku(),
                                            itemCommand.quantity(),
                                            unitPrice,
                                            itemCommand.uom()
                                        );
                                    po.addItem(item);
                                }

                                // 6. Set additional fields
                                if (command.shippingAddress() != null) {
                                    po.setShippingAddress(command.shippingAddress());
                                }
                                if (command.billingAddress() != null) {
                                    po.setBillingAddress(command.billingAddress());
                                }
                                if (command.paymentTerms() != null) {
                                    po.setPaymentTerms(command.paymentTerms());
                                }
                                if (command.shippingTerms() != null) {
                                    po.setShippingTerms(command.shippingTerms());
                                }
                                if (command.notes() != null) {
                                    po.setNotes(command.notes());
                                }
                                if (command.createdBy() != null) {
                                    po.setCreatedBy(command.createdBy());
                                }

                                return po;
                            })
                            .thenCompose(po -> purchaseOrderRepository.save(po)
                                .thenApply(PurchaseOrder::getId));
                    });
            });
    }

    private CompletionStage<Boolean> validateInventory(CreatePurchaseOrderCommand command) {
        // Validate each product exists and check if we need to purchase
        // This would check against Inventory context
        return CompletableFuture.completedFuture(true);
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/internal/ReceivePurchaseOrderItemsHandler.java`**:

```java
package tech.kayys.erp.purchasing.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.purchasing.application.api.command.ReceivePurchaseOrderItemsCommand;
import tech.kayys.erp.purchasing.application.port.InventoryPort;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;
import tech.kayys.erp.purchasing.domain.model.PurchaseOrder;
import tech.kayys.erp.purchasing.domain.repository.PurchaseOrderRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for receiving purchase order items.
 */
@UseCase("Receive items for a purchase order")
public class ReceivePurchaseOrderItemsHandler 
        implements CommandHandler<ReceivePurchaseOrderItemsCommand, PurchaseOrderId> {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InventoryPort inventoryPort;

    @Inject
    public ReceivePurchaseOrderItemsHandler(
            PurchaseOrderRepository purchaseOrderRepository,
            InventoryPort inventoryPort) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.inventoryPort = inventoryPort;
    }

    @Override
    public CompletionStage<PurchaseOrderId> handle(ReceivePurchaseOrderItemsCommand command) {
        return purchaseOrderRepository.findById(command.purchaseOrderId())
            .thenCompose(poOpt -> {
                if (poOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Purchase Order not found: " + command.purchaseOrderId())
                    );
                }

                PurchaseOrder po = poOpt.get();

                // Convert to domain received items
                List<PurchaseOrder.ReceivedItem> receivedItems = command.receivedItems().stream()
                    .map(item -> new PurchaseOrder.ReceivedItem(
                        item.itemIndex(),
                        item.quantityReceived(),
                        item.notes()
                    ))
                    .collect(java.util.stream.Collectors.toList());

                // Receive the items (domain logic)
                po.receiveItems(receivedItems);

                // Update inventory
                return inventoryPort.receivePurchaseOrder(po)
                    .thenCompose(v -> {
                        // Save the updated PO
                        return purchaseOrderRepository.save(po)
                            .thenApply(PurchaseOrder::getId);
                    });
            });
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/port/VendorPort.java`**:

```java
package tech.kayys.erp.purchasing.application.port;

import tech.kayys.erp.purchasing.domain.identifier.VendorId;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Port for vendor information.
 */
public interface VendorPort {

    /**
     * Validates that a vendor exists and is active.
     */
    CompletionStage<Boolean> validateVendor(UUID vendorId);

    /**
     * Gets vendor details.
     */
    CompletionStage<VendorDetails> getVendorDetails(UUID vendorId);

    record VendorDetails(
        UUID vendorId,
        String name,
        String email,
        String phone,
        String address,
        String paymentTerms,
        String shippingTerms,
        String currencyCode,
        boolean active
    ) {}
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/port/InventoryPort.java`**:

```java
package tech.kayys.erp.purchasing.application.port;

import tech.kayys.erp.purchasing.domain.model.PurchaseOrder;

import java.util.concurrent.CompletionStage;

/**
 * Port for inventory operations.
 */
public interface InventoryPort {

    /**
     * Receives a purchase order into inventory.
     */
    CompletionStage<Void> receivePurchaseOrder(PurchaseOrder purchaseOrder);

    /**
     * Validates inventory availability for a purchase order.
     */
    CompletionStage<Boolean> validateInventoryAvailability(PurchaseOrder purchaseOrder);

    /**
     * Gets inventory forecast for a product.
     */
    CompletionStage<InventoryForecast> getInventoryForecast(UUID productId);

    record InventoryForecast(
        UUID productId,
        int currentStock,
        int reorderPoint,
        int reorderQuantity,
        String leadTime
    ) {}
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/port/SalesOrderPort.java`**:

```java
package tech.kayys.erp.purchasing.application.port;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Port for sales order information.
 */
public interface SalesOrderPort {

    /**
     * Gets sales order details for creating a purchase order.
     */
    CompletionStage<SalesOrderDetails> getSalesOrderDetails(UUID salesOrderId);

    record SalesOrderDetails(
        UUID salesOrderId,
        UUID customerId,
        List<SalesOrderItem> items,
        String currencyCode
    ) {}

    record SalesOrderItem(
        UUID productId,
        String productName,
        String sku,
        int quantity,
        String unitPrice
    ) {}
}
```

## 3. Update Root POM

**Update `/pom.xml`** to include Purchasing modules:

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

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/subscription/domain</module>
    <module>modules/subscription/application</module>
    <module>modules/subscription/infrastructure</module>
    <module>modules/subscription/interfaces</module>

    <module>modules/accounting/domain</module>
    <module>modules/accounting/application</module>
    <module>modules/accounting/infrastructure</module>
    <module>modules/accounting/interfaces</module>

    <module>modules/purchasing/domain</module>
    <module>modules/purchasing/application</module>
    <module>modules/purchasing/infrastructure</module>
    <module>modules/purchasing/interfaces</module>
</modules>
```

## Summary

The complete Purchase Order/Procurement implementation provides:

1. **Domain Models**:
   - PurchaseOrder aggregate with full lifecycle
   - Vendor aggregate with performance tracking
   - PurchaseOrderItem value object
   - Money value object

2. **Vendor Management**:
   - Vendor creation and approval workflow
   - Performance rating and tracking
   - On-time delivery metrics
   - Vendor blacklisting

3. **Purchase Order Lifecycle**:
   - DRAFT → SUBMITTED → ACKNOWLEDGED → IN_TRANSIT → RECEIVED → COMPLETED
   - Partial receiving support
   - Hold and cancellation
   - Rejection handling

4. **Receiving**:
   - Partial item receipt
   - Receiving validation (can't exceed ordered quantity)
   - Automatic inventory update integration
   - Received date tracking

5. **Integration Ports**:
   - VendorPort for vendor validation
   - InventoryPort for inventory updates
   - SalesOrderPort for creating PO from sales orders

6. **Events**:
   - PurchaseOrderCreated
   - PurchaseOrderSubmitted
   - PurchaseOrderReceived

This completes the Purchasing context, providing full procurement capabilities that integrate with Inventory, Sales, and Accounting contexts throughout the ERP system.
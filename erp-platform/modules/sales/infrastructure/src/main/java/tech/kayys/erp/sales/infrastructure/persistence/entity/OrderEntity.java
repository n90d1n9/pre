package tech.kayys.erp.sales.infrastructure.persistence.entity;

import tech.kayys.erp.foundation.persistence.BaseEntity;
import tech.kayys.erp.sales.domain.identifier.OrderId;
import tech.kayys.erp.sales.domain.model.Order;
import tech.kayys.erp.sales.domain.model.OrderItem;
import tech.kayys.erp.sales.domain.valueobject.Address;
import tech.kayys.erp.sales.domain.valueobject.Money;
import tech.kayys.erp.sales.domain.valueobject.OrderStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Order entity for Sales context.
 */
@Entity
@Table(name = "orders")
public class OrderEntity extends BaseEntity {

    @Column(name = "customer_id", columnDefinition = "UUID", nullable = false)
    public UUID customerId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<OrderItemEntity> items = new ArrayList<>();

    @Column(name = "subtotal", precision = 19, scale = 2, nullable = false)
    public BigDecimal subtotal;

    @Column(name = "tax_total", precision = 19, scale = 2, nullable = false)
    public BigDecimal taxTotal;

    @Column(name = "shipping_cost", precision = 19, scale = 2, nullable = false)
    public BigDecimal shippingCost;

    @Column(name = "discount_total", precision = 19, scale = 2, nullable = false)
    public BigDecimal discountTotal;

    @Column(name = "grand_total", precision = 19, scale = 2, nullable = false)
    public BigDecimal grandTotal;

    @Column(name = "currency", length = 3, nullable = false)
    public String currency;

    @Column(name = "status", length = 20, nullable = false)
    public String status;

    @Column(name = "shipping_address_street", length = 255)
    public String shippingStreet;

    @Column(name = "shipping_address_city", length = 100)
    public String shippingCity;

    @Column(name = "shipping_address_state", length = 100)
    public String shippingState;

    @Column(name = "shipping_address_postal_code", length = 20)
    public String shippingPostalCode;

    @Column(name = "shipping_address_country", length = 100)
    public String shippingCountry;

    @Column(name = "billing_address_street", length = 255)
    public String billingStreet;

    @Column(name = "billing_address_city", length = 100)
    public String billingCity;

    @Column(name = "billing_address_state", length = 100)
    public String billingState;

    @Column(name = "billing_address_postal_code", length = 20)
    public String billingPostalCode;

    @Column(name = "billing_address_country", length = 100)
    public String billingCountry;

    @Column(name = "customer_notes", length = 1000)
    public String customerNotes;

    @Column(name = "internal_notes", length = 1000)
    public String internalNotes;

    @Column(name = "submitted_at")
    public Instant submittedAt;

    @Column(name = "confirmed_at")
    public Instant confirmedAt;

    @Column(name = "shipped_at")
    public Instant shippedAt;

    @Column(name = "delivered_at")
    public Instant deliveredAt;

    @Column(name = "shipping_method", length = 50)
    public String shippingMethod;

    @Column(name = "tracking_number", length = 100)
    public String trackingNumber;

    public Order toDomain() {
        OrderId orderId = OrderId.of(id);
        // Create order with minimal fields
        Order order = Order.create(orderId, tech.kayys.erp.sales.domain.identifier.CustomerId.of(customerId));
        
        // Add items
        for (OrderItemEntity itemEntity : items) {
            OrderItem item = new OrderItem.Builder()
                .productId(itemEntity.productId)
                .productName(itemEntity.productName)
                .sku(itemEntity.sku)
                .quantity(itemEntity.quantity)
                .unitPrice(Money.of(itemEntity.unitPrice, currency))
                .taxAmount(Money.of(itemEntity.taxAmount, currency))
                .discountAmount(Money.of(itemEntity.discountAmount, currency))
                .build();
            order.addItem(item);
        }

        // Set addresses
        if (shippingStreet != null) {
            Address shippingAddress = Address.of(
                shippingStreet,
                shippingCity,
                shippingState,
                shippingPostalCode,
                shippingCountry
            );
            order.setShippingAddress(shippingAddress);
        }

        if (billingStreet != null) {
            Address billingAddress = Address.of(
                billingStreet,
                billingCity,
                billingState,
                billingPostalCode,
                billingCountry
            );
            order.setBillingAddress(billingAddress);
        }

        // Set status and timestamps
        // Note: Status is set via domain methods
        // This is a simplified mapping

        return order;
    }

    public static OrderEntity fromDomain(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.id = order.getId().getValue();
        entity.customerId = order.getCustomerId().getValue();
        entity.currency = order.getGrandTotal().getCurrency().getCurrencyCode();
        entity.subtotal = order.getSubtotal().getAmount();
        entity.taxTotal = order.getTaxTotal().getAmount();
        entity.shippingCost = order.getShippingCost().getAmount();
        entity.discountTotal = order.getDiscountTotal().getAmount();
        entity.grandTotal = order.getGrandTotal().getAmount();
        entity.status = order.getStatus().name();
        entity.createdAt = order.getCreatedAt();
        entity.updatedAt = order.getUpdatedAt();
        entity.version = order.getVersion();

        // Map addresses
        if (order.getShippingAddress() != null) {
            entity.shippingStreet = order.getShippingAddress().getStreet();
            entity.shippingCity = order.getShippingAddress().getCity();
            entity.shippingState = order.getShippingAddress().getState();
            entity.shippingPostalCode = order.getShippingAddress().getPostalCode();
            entity.shippingCountry = order.getShippingAddress().getCountry();
        }

        if (order.getBillingAddress() != null) {
            entity.billingStreet = order.getBillingAddress().getStreet();
            entity.billingCity = order.getBillingAddress().getCity();
            entity.billingState = order.getBillingAddress().getState();
            entity.billingPostalCode = order.getBillingAddress().getPostalCode();
            entity.billingCountry = order.getBillingAddress().getCountry();
        }

        // Map items
        for (OrderItem item : order.getItems()) {
            OrderItemEntity itemEntity = OrderItemEntity.fromDomain(item, entity);
            entity.items.add(itemEntity);
        }

        entity.customerNotes = order.getCustomerNotes();
        entity.internalNotes = order.getInternalNotes();
        entity.submittedAt = order.getSubmittedAt();
        entity.confirmedAt = order.getConfirmedAt();
        entity.shippedAt = order.getShippedAt();
        entity.deliveredAt = order.getDeliveredAt();
        entity.shippingMethod = order.getShippingMethod();
        entity.trackingNumber = order.getTrackingNumber();

        return entity;
    }
}
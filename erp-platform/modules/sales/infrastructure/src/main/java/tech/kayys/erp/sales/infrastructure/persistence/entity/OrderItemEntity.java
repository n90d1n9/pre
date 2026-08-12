package tech.kayys.erp.sales.infrastructure.persistence.entity;

import tech.kayys.erp.foundation.persistence.BaseEntity;
import tech.kayys.erp.sales.domain.model.OrderItem;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Order item entity.
 */
@Entity
@Table(name = "order_items")
public class OrderItemEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    public OrderEntity order;

    @Column(name = "product_id", columnDefinition = "UUID", nullable = false)
    public UUID productId;

    @Column(name = "product_name", length = 255, nullable = false)
    public String productName;

    @Column(name = "sku", length = 50)
    public String sku;

    @Column(name = "quantity", nullable = false)
    public int quantity;

    @Column(name = "unit_price", precision = 19, scale = 2, nullable = false)
    public BigDecimal unitPrice;

    @Column(name = "total_price", precision = 19, scale = 2, nullable = false)
    public BigDecimal totalPrice;

    @Column(name = "tax_amount", precision = 19, scale = 2, nullable = false)
    public BigDecimal taxAmount;

    @Column(name = "discount_amount", precision = 19, scale = 2, nullable = false)
    public BigDecimal discountAmount;

    public static OrderItemEntity fromDomain(OrderItem item, OrderEntity order) {
        OrderItemEntity entity = new OrderItemEntity();
        entity.id = UUID.randomUUID();
        entity.order = order;
        entity.productId = item.getProductId();
        entity.productName = item.getProductName();
        entity.sku = item.getSku();
        entity.quantity = item.getQuantity();
        entity.unitPrice = item.getUnitPrice().getAmount();
        entity.totalPrice = item.getTotalPrice().getAmount();
        entity.taxAmount = item.getTaxAmount().getAmount();
        entity.discountAmount = item.getDiscountAmount().getAmount();
        return entity;
    }
}
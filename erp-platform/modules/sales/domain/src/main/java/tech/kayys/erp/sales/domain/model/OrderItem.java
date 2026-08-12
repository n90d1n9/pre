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
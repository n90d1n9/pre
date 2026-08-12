package tech.kayys.erp.pricing.domain.model;

import tech.kayys.erp.foundation.domain.ValueObject;
import tech.kayys.erp.pricing.domain.identifier.ProductId;
import tech.kayys.erp.pricing.domain.valueobject.Money;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Price calculation result value object.
 * Represents the result of calculating a price with all applicable rules.
 */
public final class PriceCalculation implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final ProductId productId;
    private final Money basePrice;
    private final Money discountedPrice;
    private final Money taxAmount;
    private final Money finalPrice;
    private final List<AppliedDiscount> appliedDiscounts;
    private final List<AppliedTax> appliedTaxes;
    private final Instant calculatedAt;
    private final String currencyCode;

    private PriceCalculation(Builder builder) {
        this.productId = builder.productId;
        this.basePrice = builder.basePrice;
        this.discountedPrice = builder.discountedPrice;
        this.taxAmount = builder.taxAmount;
        this.finalPrice = builder.finalPrice;
        this.appliedDiscounts = Collections.unmodifiableList(builder.appliedDiscounts);
        this.appliedTaxes = Collections.unmodifiableList(builder.appliedTaxes);
        this.calculatedAt = Instant.now();
        this.currencyCode = builder.basePrice.getCurrency().getCurrencyCode();
        validate();
    }

    @Override
    public void validate() {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (basePrice == null) {
            throw new IllegalArgumentException("Base price cannot be null");
        }
        if (finalPrice == null) {
            throw new IllegalArgumentException("Final price cannot be null");
        }
        if (basePrice.isNegative()) {
            throw new IllegalArgumentException("Base price cannot be negative");
        }
        if (finalPrice.isNegative()) {
            throw new IllegalArgumentException("Final price cannot be negative");
        }
    }

    public ProductId getProductId() { return productId; }
    public Money getBasePrice() { return basePrice; }
    public Money getDiscountedPrice() { return discountedPrice; }
    public Money getTaxAmount() { return taxAmount; }
    public Money getFinalPrice() { return finalPrice; }
    public List<AppliedDiscount> getAppliedDiscounts() { return appliedDiscounts; }
    public List<AppliedTax> getAppliedTaxes() { return appliedTaxes; }
    public Instant getCalculatedAt() { return calculatedAt; }
    public String getCurrencyCode() { return currencyCode; }

    public boolean hasDiscounts() {
        return !appliedDiscounts.isEmpty();
    }

    public BigDecimal getTotalDiscountPercentage() {
        if (basePrice.isZero()) {
            return BigDecimal.ZERO;
        }
        Money totalDiscount = basePrice.subtract(discountedPrice != null ? discountedPrice : basePrice);
        return totalDiscount.getAmount()
            .divide(basePrice.getAmount(), 4, java.math.RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceCalculation that = (PriceCalculation) o;
        return Objects.equals(productId, that.productId) &&
               Objects.equals(basePrice, that.basePrice) &&
               Objects.equals(finalPrice, that.finalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, basePrice, finalPrice);
    }

    @Override
    public String toString() {
        return "PriceCalculation{" +
                "productId=" + productId +
                ", basePrice=" + basePrice +
                ", finalPrice=" + finalPrice +
                ", discounts=" + appliedDiscounts.size() +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ProductId productId;
        private Money basePrice;
        private Money discountedPrice;
        private Money taxAmount = Money.zero("USD");
        private Money finalPrice;
        private List<AppliedDiscount> appliedDiscounts = new ArrayList<>();
        private List<AppliedTax> appliedTaxes = new ArrayList<>();

        public Builder productId(ProductId productId) {
            this.productId = productId;
            return this;
        }

        public Builder basePrice(Money basePrice) {
            this.basePrice = basePrice;
            return this;
        }

        public Builder discountedPrice(Money discountedPrice) {
            this.discountedPrice = discountedPrice;
            return this;
        }

        public Builder taxAmount(Money taxAmount) {
            this.taxAmount = taxAmount;
            return this;
        }

        public Builder finalPrice(Money finalPrice) {
            this.finalPrice = finalPrice;
            return this;
        }

        public Builder appliedDiscounts(List<AppliedDiscount> appliedDiscounts) {
            this.appliedDiscounts = new ArrayList<>(appliedDiscounts);
            return this;
        }

        public Builder addDiscount(AppliedDiscount discount) {
            this.appliedDiscounts.add(discount);
            return this;
        }

        public Builder appliedTaxes(List<AppliedTax> appliedTaxes) {
            this.appliedTaxes = new ArrayList<>(appliedTaxes);
            return this;
        }

        public Builder addTax(AppliedTax tax) {
            this.appliedTaxes.add(tax);
            return this;
        }

        public PriceCalculation build() {
            if (discountedPrice == null) {
                discountedPrice = basePrice;
            }
            if (finalPrice == null) {
                Money subtotal = discountedPrice;
                if (taxAmount != null && !taxAmount.isZero()) {
                    finalPrice = subtotal.add(taxAmount);
                } else {
                    finalPrice = subtotal;
                }
            }
            return new PriceCalculation(this);
        }
    }

    /**
     * Applied discount record.
     */
    public static class AppliedDiscount implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String ruleId;
        private final String ruleName;
        private final String type;
        private final BigDecimal value;
        private final Money discountAmount;

        public AppliedDiscount(String ruleId, String ruleName, String type, 
                               BigDecimal value, Money discountAmount) {
            this.ruleId = ruleId;
            this.ruleName = ruleName;
            this.type = type;
            this.value = value;
            this.discountAmount = discountAmount;
            validate();
        }

        @Override
        public void validate() {
            if (discountAmount == null || discountAmount.isNegative()) {
                throw new IllegalArgumentException("Discount amount cannot be negative");
            }
        }

        public String getRuleId() { return ruleId; }
        public String getRuleName() { return ruleName; }
        public String getType() { return type; }
        public BigDecimal getValue() { return value; }
        public Money getDiscountAmount() { return discountAmount; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AppliedDiscount that = (AppliedDiscount) o;
            return Objects.equals(ruleId, that.ruleId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ruleId);
        }

        @Override
        public String toString() {
            return "AppliedDiscount{" +
                    "ruleName='" + ruleName + '\'' +
                    ", discountAmount=" + discountAmount +
                    '}';
        }
    }

    /**
     * Applied tax record.
     */
    public static class AppliedTax implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String taxId;
        private final String taxType;
        private final String jurisdiction;
        private final BigDecimal rate;
        private final Money taxAmount;

        public AppliedTax(String taxId, String taxType, String jurisdiction, 
                          BigDecimal rate, Money taxAmount) {
            this.taxId = taxId;
            this.taxType = taxType;
            this.jurisdiction = jurisdiction;
            this.rate = rate;
            this.taxAmount = taxAmount;
            validate();
        }

        @Override
        public void validate() {
            if (taxAmount == null || taxAmount.isNegative()) {
                throw new IllegalArgumentException("Tax amount cannot be negative");
            }
        }

        public String getTaxId() { return taxId; }
        public String getTaxType() { return taxType; }
        public String getJurisdiction() { return jurisdiction; }
        public BigDecimal getRate() { return rate; }
        public Money getTaxAmount() { return taxAmount; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AppliedTax that = (AppliedTax) o;
            return Objects.equals(taxId, that.taxId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(taxId);
        }

        @Override
        public String toString() {
            return "AppliedTax{" +
                    "taxType='" + taxType + '\'' +
                    ", rate=" + rate + "%" +
                    ", taxAmount=" + taxAmount +
                    '}';
        }
    }
}
# Complete Implementation: Pricing Bounded Context

Now I'll implement the complete Pricing bounded context, which handles pricing rules, tax calculation, discounts, and promotions. This context is used by Sales/Order, Catalog, and other contexts.

## 1. Pricing Domain Module

**`/modules/pricing/domain/pom.xml`**:

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

    <artifactId>erp-pricing-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/pricing/domain/src/main/java/tech/kayys/erp/pricing/domain/identifier/PricingRuleId.java`**:

```java
package tech.kayys.erp.pricing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Pricing rule identifier.
 */
public final class PricingRuleId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public PricingRuleId(UUID value) {
        super(value);
    }

    public static PricingRuleId of(UUID value) {
        return new PricingRuleId(value);
    }

    public static PricingRuleId generate() {
        return new PricingRuleId(UUID.randomUUID());
    }

    public static PricingRuleId fromString(String value) {
        return new PricingRuleId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PricingRuleId{" + value + "}";
    }
}
```

**`/modules/pricing/domain/src/main/java/tech/kayys/erp/pricing/domain/identifier/ProductId.java`**:

```java
package tech.kayys.erp.pricing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Product identifier in the Pricing context.
 * Represents a product from Catalog context.
 */
public final class ProductId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ProductId(UUID value) {
        super(value);
    }

    public static ProductId of(UUID value) {
        return new ProductId(value);
    }

    public static ProductId fromString(String value) {
        return new ProductId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ProductId{" + value + "}";
    }
}
```

**`/modules/pricing/domain/src/main/java/tech/kayys/erp/pricing/domain/identifier/TaxRateId.java`**:

```java
package tech.kayys.erp.pricing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Tax rate identifier.
 */
public final class TaxRateId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public TaxRateId(UUID value) {
        super(value);
    }

    public static TaxRateId of(UUID value) {
        return new TaxRateId(value);
    }

    public static TaxRateId generate() {
        return new TaxRateId(UUID.randomUUID());
    }

    public static TaxRateId fromString(String value) {
        return new TaxRateId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "TaxRateId{" + value + "}";
    }
}
```

**`/modules/pricing/domain/src/main/java/tech/kayys/erp/pricing/domain/valueobject/Money.java`**:

```java
package tech.kayys.erp.pricing.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Money value object for the Pricing context.
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
        return multiply(percentage.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_EVEN));
    }

    public Money percentage(int percentage) {
        return percentage(BigDecimal.valueOf(percentage));
    }

    public int compareTo(Money other) {
        validateCurrency(other);
        return amount.compareTo(other.amount);
    }

    public boolean isGreaterThan(Money other) {
        return compareTo(other) > 0;
    }

    public boolean isGreaterThanOrEqualTo(Money other) {
        return compareTo(other) >= 0;
    }

    public boolean isLessThan(Money other) {
        return compareTo(other) < 0;
    }

    public boolean isLessThanOrEqualTo(Money other) {
        return compareTo(other) <= 0;
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

    public Money roundToNearest(int cents) {
        if (cents <= 0) {
            return this;
        }
        BigDecimal divisor = BigDecimal.valueOf(cents);
        BigDecimal rounded = amount.multiply(divisor)
            .setScale(0, RoundingMode.HALF_UP)
            .divide(divisor, scale, RoundingMode.HALF_UP);
        return new Money(rounded, currency, scale);
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

    public static Money max(Money first, Money second) {
        if (first == null) return second;
        if (second == null) return first;
        return first.isGreaterThan(second) ? first : second;
    }

    public static Money min(Money first, Money second) {
        if (first == null) return second;
        if (second == null) return first;
        return first.isLessThan(second) ? first : second;
    }
}
```

**`/modules/pricing/domain/src/main/java/tech/kayys/erp/pricing/domain/valueobject/DiscountType.java`**:

```java
package tech.kayys.erp.pricing.domain.valueobject;

/**
 * Types of discounts that can be applied.
 */
public enum DiscountType {
    PERCENTAGE("Percentage discount"),
    FIXED_AMOUNT("Fixed amount discount"),
    BUY_X_GET_Y("Buy X get Y free"),
    VOLUME_DISCOUNT("Volume-based discount"),
    SEASONAL("Seasonal discount"),
    COUPON("Coupon code discount"),
    BUNDLE("Bundle discount");

    private final String description;

    DiscountType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

**`/modules/pricing/domain/src/main/java/tech/kayys/erp/pricing/domain/valueobject/DiscountApplication.java`**:

```java
package tech.kayys.erp.pricing.domain.valueobject;

/**
 * When the discount should be applied in the pricing calculation.
 */
public enum DiscountApplication {
    BEFORE_TAX("Applied before tax calculation"),
    AFTER_TAX("Applied after tax calculation"),
    AT_CHECKOUT("Applied at checkout only");

    private final String description;

    DiscountApplication(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

**`/modules/pricing/domain/src/main/java/tech/kayys/erp/pricing/domain/valueobject/PricingTier.java`**:

```java
package tech.kayys.erp.pricing.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Pricing tier for volume-based pricing.
 */
public final class PricingTier implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final int minQuantity;
    private final int maxQuantity; // null means unlimited
    private final BigDecimal discountPercentage;

    public PricingTier(int minQuantity, Integer maxQuantity, BigDecimal discountPercentage) {
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity != null ? maxQuantity : Integer.MAX_VALUE;
        this.discountPercentage = discountPercentage;
        validate();
    }

    @Override
    public void validate() {
        if (minQuantity < 0) {
            throw new IllegalArgumentException("Min quantity cannot be negative");
        }
        if (maxQuantity < minQuantity) {
            throw new IllegalArgumentException("Max quantity must be >= min quantity");
        }
        if (discountPercentage == null || discountPercentage.signum() < 0 || 
            discountPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
    }

    public int getMinQuantity() { return minQuantity; }
    public int getMaxQuantity() { return maxQuantity; }
    public BigDecimal getDiscountPercentage() { return discountPercentage; }

    public boolean appliesTo(int quantity) {
        return quantity >= minQuantity && quantity <= maxQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PricingTier that = (PricingTier) o;
        return minQuantity == that.minQuantity &&
               maxQuantity == that.maxQuantity &&
               Objects.equals(discountPercentage, that.discountPercentage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minQuantity, maxQuantity, discountPercentage);
    }

    @Override
    public String toString() {
        return "PricingTier{" +
                "minQuantity=" + minQuantity +
                ", maxQuantity=" + (maxQuantity == Integer.MAX_VALUE ? "∞" : maxQuantity) +
                ", discountPercentage=" + discountPercentage + "%" +
                '}';
    }

    public static PricingTier of(int minQuantity, Integer maxQuantity, BigDecimal discountPercentage) {
        return new PricingTier(minQuantity, maxQuantity, discountPercentage);
    }

    public static PricingTier of(int minQuantity, BigDecimal discountPercentage) {
        return new PricingTier(minQuantity, null, discountPercentage);
    }
}
```

**`/modules/pricing/domain/src/main/java/tech/kayys/erp/pricing/domain/valueobject/TaxType.java`**:

```java
package tech.kayys.erp.pricing.domain.valueobject;

/**
 * Types of taxes.
 */
public enum TaxType {
    VAT("Value Added Tax"),
    GST("Goods and Services Tax"),
    SALES_TAX("Sales Tax"),
    USE_TAX("Use Tax"),
    EXCISE("Excise Tax"),
    CUSTOMS("Customs Duty");

    private final String description;

    TaxType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

**`/modules/pricing/domain/src/main/java/tech/kayys/erp/pricing/domain/valueobject/TaxRate.java`**:

```java
package tech.kayys.erp.pricing.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;
import tech.kayys.erp.pricing.domain.identifier.TaxRateId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Tax rate value object.
 */
public final class TaxRate implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final TaxRateId id;
    private final TaxType taxType;
    private final BigDecimal rate;
    private final String jurisdiction; // e.g., "US-CA", "UK", "EU"
    private final String productCategory; // e.g., "standard", "reduced", "zero"
    private final Instant effectiveFrom;
    private final Instant effectiveTo;

    public TaxRate(
            TaxRateId id,
            TaxType taxType,
            BigDecimal rate,
            String jurisdiction,
            String productCategory,
            Instant effectiveFrom,
            Instant effectiveTo) {
        this.id = id;
        this.taxType = taxType;
        this.rate = rate;
        this.jurisdiction = jurisdiction;
        this.productCategory = productCategory;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        validate();
    }

    @Override
    public void validate() {
        if (id == null) {
            throw new IllegalArgumentException("Tax rate ID cannot be null");
        }
        if (taxType == null) {
            throw new IllegalArgumentException("Tax type cannot be null");
        }
        if (rate == null || rate.signum() < 0 || rate.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Rate must be between 0 and 100");
        }
        if (jurisdiction == null || jurisdiction.trim().isEmpty()) {
            throw new IllegalArgumentException("Jurisdiction cannot be empty");
        }
        if (effectiveFrom == null) {
            throw new IllegalArgumentException("Effective from date cannot be null");
        }
        if (effectiveTo != null && effectiveTo.isBefore(effectiveFrom)) {
            throw new IllegalArgumentException("Effective to date must be after effective from date");
        }
    }

    public TaxRateId getId() { return id; }
    public TaxType getTaxType() { return taxType; }
    public BigDecimal getRate() { return rate; }
    public String getJurisdiction() { return jurisdiction; }
    public String getProductCategory() { return productCategory; }
    public Instant getEffectiveFrom() { return effectiveFrom; }
    public Instant getEffectiveTo() { return effectiveTo; }

    public boolean isEffective(Instant date) {
        if (date.isBefore(effectiveFrom)) {
            return false;
        }
        return effectiveTo == null || date.isBefore(effectiveTo);
    }

    public boolean isCurrentlyEffective() {
        return isEffective(Instant.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaxRate taxRate = (TaxRate) o;
        return Objects.equals(id, taxRate.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TaxRate{" +
                "id=" + id +
                ", taxType=" + taxType +
                ", rate=" + rate + "%" +
                ", jurisdiction='" + jurisdiction + '\'' +
                '}';
    }

    public static TaxRate of(
            TaxRateId id,
            TaxType taxType,
            BigDecimal rate,
            String jurisdiction,
            String productCategory,
            Instant effectiveFrom) {
        return new TaxRate(id, taxType, rate, jurisdiction, productCategory, effectiveFrom, null);
    }

    public static TaxRate of(
            TaxType taxType,
            BigDecimal rate,
            String jurisdiction) {
        return new TaxRate(
            TaxRateId.generate(),
            taxType,
            rate,
            jurisdiction,
            "standard",
            Instant.now(),
            null
        );
    }
}
```

**`/modules/pricing/domain/src/main/java/tech/kayys/erp/pricing/domain/model/PricingRule.java`**:

```java
package tech.kayys.erp.pricing.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.pricing.domain.identifier.PricingRuleId;
import tech.kayys.erp.pricing.domain.valueobject.DiscountApplication;
import tech.kayys.erp.pricing.domain.valueobject.DiscountType;
import tech.kayys.erp.pricing.domain.valueobject.Money;
import tech.kayys.erp.pricing.domain.valueobject.PricingTier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Pricing rule aggregate root.
 * Defines how discounts and special pricing should be applied.
 */
public final class PricingRule extends AggregateRoot<PricingRuleId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue; // Percentage or fixed amount
    private DiscountApplication application;
    private List<PricingTier> tiers;
    private List<UUID> applicableProductIds; // null = applies to all
    private List<String> applicableCategories; // null = applies to all
    private Instant validFrom;
    private Instant validTo;
    private boolean active;
    private boolean stackable;
    private int priority; // Higher priority = applied first
    private String couponCode; // null if not a coupon
    private BigDecimal minimumOrderAmount;
    private boolean requiresApproval;

    private PricingRule(PricingRuleId id) {
        super(id);
        this.tiers = new ArrayList<>();
        this.applicableProductIds = new ArrayList<>();
        this.applicableCategories = new ArrayList<>();
        this.active = true;
        this.stackable = false;
        this.priority = 0;
        this.requiresApproval = false;
    }

    private PricingRule() {
        super();
    }

    /**
     * Factory method to create a new pricing rule.
     */
    public static PricingRule create(
            PricingRuleId id,
            String name,
            DiscountType discountType,
            BigDecimal discountValue,
            DiscountApplication application) {
        PricingRule rule = new PricingRule(id);
        rule.name = name;
        rule.discountType = discountType;
        rule.discountValue = discountValue;
        rule.application = application;
        rule.validFrom = Instant.now();
        rule.tiers = new ArrayList<>();
        return rule;
    }

    /**
     * Adds a pricing tier for volume discounts.
     */
    public void addTier(PricingTier tier) {
        if (discountType != DiscountType.VOLUME_DISCOUNT && 
            discountType != DiscountType.BUNDLE) {
            throw new IllegalStateException(
                "Tiers only apply to volume or bundle discounts"
            );
        }
        this.tiers.add(tier);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets product applicability.
     */
    public void setApplicableProducts(List<UUID> productIds) {
        this.applicableProductIds = new ArrayList<>(productIds);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets category applicability.
     */
    public void setApplicableCategories(List<String> categories) {
        this.applicableCategories = new ArrayList<>(categories);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Calculates the discount for a given amount and quantity.
     */
    public Money calculateDiscount(Money amount, int quantity) {
        if (!isActive()) {
            return Money.zero(amount.getCurrency().getCurrencyCode());
        }

        // Check if rule applies based on quantity and tiers
        if (discountType == DiscountType.VOLUME_DISCOUNT) {
            PricingTier applicableTier = tiers.stream()
                .filter(t -> t.appliesTo(quantity))
                .findFirst()
                .orElse(null);
            
            if (applicableTier == null) {
                return Money.zero(amount.getCurrency().getCurrencyCode());
            }
            
            // Apply tier percentage
            return amount.percentage(applicableTier.getDiscountPercentage());
        }

        // Simple percentage or fixed amount discount
        return switch (discountType) {
            case PERCENTAGE, SEASONAL, COUPON -> 
                amount.percentage(discountValue);
            case FIXED_AMOUNT -> 
                Money.of(discountValue, amount.getCurrency().getCurrencyCode());
            default -> 
                Money.zero(amount.getCurrency().getCurrencyCode());
        };
    }

    /**
     * Checks if this rule applies to a product.
     */
    public boolean appliesToProduct(UUID productId) {
        if (!isActive()) {
            return false;
        }
        if (applicableProductIds == null || applicableProductIds.isEmpty()) {
            return true; // Applies to all products
        }
        return applicableProductIds.contains(productId);
    }

    /**
     * Checks if this rule applies to a category.
     */
    public boolean appliesToCategory(String category) {
        if (!isActive()) {
            return false;
        }
        if (applicableCategories == null || applicableCategories.isEmpty()) {
            return true; // Applies to all categories
        }
        return applicableCategories.contains(category);
    }

    /**
     * Checks if this rule is currently valid.
     */
    public boolean isActive() {
        if (!active) {
            return false;
        }
        Instant now = Instant.now();
        if (validFrom != null && now.isBefore(validFrom)) {
            return false;
        }
        return validTo == null || now.isBefore(validTo);
    }

    /**
     * Activates the rule.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the rule.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the validity period.
     */
    public void setValidityPeriod(Instant from, Instant to) {
        if (to != null && to.isBefore(from)) {
            throw new IllegalArgumentException("Valid to must be after valid from");
        }
        this.validFrom = from;
        this.validTo = to;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public DiscountType getDiscountType() { return discountType; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public DiscountApplication getApplication() { return application; }
    public List<PricingTier> getTiers() { return Collections.unmodifiableList(tiers); }
    public List<UUID> getApplicableProductIds() { return Collections.unmodifiableList(applicableProductIds); }
    public List<String> getApplicableCategories() { return Collections.unmodifiableList(applicableCategories); }
    public Instant getValidFrom() { return validFrom; }
    public Instant getValidTo() { return validTo; }
    public boolean isActive() { return active; }
    public boolean isStackable() { return stackable; }
    public int getPriority() { return priority; }
    public String getCouponCode() { return couponCode; }
    public BigDecimal getMinimumOrderAmount() { return minimumOrderAmount; }
    public boolean isRequiresApproval() { return requiresApproval; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPriority(int priority) {
        this.priority = priority;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMinimumOrderAmount(Money minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount.getAmount();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setRequiresApproval(boolean requiresApproval) {
        this.requiresApproval = requiresApproval;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "PricingRule{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", discountType=" + discountType +
                ", discountValue=" + discountValue +
                ", active=" + active +
                '}';
    }
}
```

**`/modules/pricing/domain/src/main/java/tech/kayys/erp/pricing/domain/model/PriceCalculation.java`**:

```java
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
```

**`/modules/pricing/domain/src/main/java/tech/kayys/erp/pricing/domain/repository/PricingRuleRepository.java`**:

```java
package tech.kayys.erp.pricing.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.pricing.domain.identifier.PricingRuleId;
import tech.kayys.erp.pricing.domain.model.PricingRule;
import tech.kayys.erp.pricing.domain.valueobject.DiscountType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Repository for PricingRule aggregates.
 */
public interface PricingRuleRepository extends Repository<PricingRule, PricingRuleId> {

    /**
     * Finds all active pricing rules.
     */
    CompletionStage<List<PricingRule>> findActiveRules();

    /**
     * Finds pricing rules applicable to a product.
     */
    CompletionStage<List<PricingRule>> findApplicableRules(UUID productId);

    /**
     * Finds pricing rules by type.
     */
    CompletionStage<List<PricingRule>> findByType(DiscountType type);

    /**
     * Finds pricing rules by coupon code.
     */
    CompletionStage<PricingRule> findByCouponCode(String couponCode);

    /**
     * Finds pricing rules valid at a given time.
     */
    CompletionStage<List<PricingRule>> findValidAt(Instant time);

    /**
     * Finds pricing rules with a specific priority.
     */
    CompletionStage<List<PricingRule>> findByPriorityGreaterThan(int priority);
}
```

**`/modules/pricing/domain/src/main/java/tech/kayys/erp/pricing/domain/repository/TaxRateRepository.java`**:

```java
package tech.kayys.erp.pricing.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.pricing.domain.identifier.TaxRateId;
import tech.kayys.erp.pricing.domain.valueobject.TaxRate;
import tech.kayys.erp.pricing.domain.valueobject.TaxType;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for TaxRate aggregates.
 */
public interface TaxRateRepository extends Repository<TaxRate, TaxRateId> {

    /**
     * Finds tax rates by jurisdiction.
     */
    CompletionStage<List<TaxRate>> findByJurisdiction(String jurisdiction);

    /**
     * Finds tax rates by type.
     */
    CompletionStage<List<TaxRate>> findByType(TaxType type);

    /**
     * Finds effective tax rates at a given time.
     */
    CompletionStage<List<TaxRate>> findEffectiveAt(Instant time);

    /**
     * Finds currently effective tax rates.
     */
    default CompletionStage<List<TaxRate>> findCurrentEffective() {
        return findEffectiveAt(Instant.now());
    }

    /**
     * Finds tax rates by jurisdiction and type.
     */
    CompletionStage<List<TaxRate>> findByJurisdictionAndType(
        String jurisdiction, 
        TaxType type
    );
}
```

## 2. Pricing Application Module

**`/modules/pricing/application/pom.xml`**:

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

    <artifactId>erp-pricing-application</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-pricing-domain</artifactId>
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

**`/modules/pricing/application/src/main/java/tech/kayys/erp/pricing/application/api/PricingService.java`**:

```java
package tech.kayys.erp.pricing.application.api;

import tech.kayys.erp.pricing.application.api.command.CalculatePriceCommand;
import tech.kayys.erp.pricing.application.api.command.CalculateTaxCommand;
import tech.kayys.erp.pricing.application.api.query.PriceCalculationView;
import tech.kayys.erp.pricing.application.api.query.TaxCalculationView;

import java.util.concurrent.CompletionStage;

/**
 * Public API for pricing calculations.
 * This is the primary entry point for other contexts to get pricing information.
 */
public interface PricingService {

    /**
     * Calculates the final price for a product with all applicable discounts and taxes.
     */
    CompletionStage<PriceCalculationView> calculatePrice(CalculatePriceCommand command);

    /**
     * Calculates the tax for a given amount in a jurisdiction.
     */
    CompletionStage<TaxCalculationView> calculateTax(CalculateTaxCommand command);

    /**
     * Validates if a coupon code is valid and applicable.
     */
    CompletionStage<CouponValidationResult> validateCoupon(String couponCode, Money amount);

    /**
     * Gets the base price of a product.
     */
    CompletionStage<Money> getBasePrice(UUID productId);
}
```

**`/modules/pricing/application/src/main/java/tech/kayys/erp/pricing/application/api/command/CalculatePriceCommand.java`**:

```java
package tech.kayys.erp.pricing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.pricing.application.api.MoneyCommand;

import java.util.UUID;

/**
 * Command to calculate the price of a product with all applicable rules.
 */
public record CalculatePriceCommand(
        UUID productId,
        int quantity,
        MoneyCommand basePrice,
        String couponCode,
        String jurisdiction
) implements Command<PriceCalculationView> {

    public CalculatePriceCommand {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (basePrice == null) {
            throw new IllegalArgumentException("Base price is required");
        }
        if (jurisdiction == null || jurisdiction.trim().isEmpty()) {
            throw new IllegalArgumentException("Jurisdiction is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID productId;
        private int quantity = 1;
        private MoneyCommand basePrice;
        private String couponCode;
        private String jurisdiction;

        public Builder productId(UUID productId) {
            this.productId = productId;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder basePrice(MoneyCommand basePrice) {
            this.basePrice = basePrice;
            return this;
        }

        public Builder basePrice(String amount, String currencyCode) {
            this.basePrice = new MoneyCommand(amount, currencyCode);
            return this;
        }

        public Builder couponCode(String couponCode) {
            this.couponCode = couponCode;
            return this;
        }

        public Builder jurisdiction(String jurisdiction) {
            this.jurisdiction = jurisdiction;
            return this;
        }

        public CalculatePriceCommand build() {
            return new CalculatePriceCommand(productId, quantity, basePrice, couponCode, jurisdiction);
        }
    }
}
```

**`/modules/pricing/application/src/main/java/tech/kayys/erp/pricing/application/api/command/CalculateTaxCommand.java`**:

```java
package tech.kayys.erp.pricing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.pricing.application.api.MoneyCommand;
import tech.kayys.erp.pricing.domain.valueobject.TaxType;

/**
 * Command to calculate tax for a given amount.
 */
public record CalculateTaxCommand(
        MoneyCommand amount,
        String jurisdiction,
        TaxType taxType,
        String productCategory
) implements Command<TaxCalculationView> {

    public CalculateTaxCommand {
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required");
        }
        if (jurisdiction == null || jurisdiction.trim().isEmpty()) {
            throw new IllegalArgumentException("Jurisdiction is required");
        }
        if (taxType == null) {
            throw new IllegalArgumentException("Tax type is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private MoneyCommand amount;
        private String jurisdiction;
        private TaxType taxType = TaxType.VAT;
        private String productCategory = "standard";

        public Builder amount(MoneyCommand amount) {
            this.amount = amount;
            return this;
        }

        public Builder amount(String amount, String currencyCode) {
            this.amount = new MoneyCommand(amount, currencyCode);
            return this;
        }

        public Builder jurisdiction(String jurisdiction) {
            this.jurisdiction = jurisdiction;
            return this;
        }

        public Builder taxType(TaxType taxType) {
            this.taxType = taxType;
            return this;
        }

        public Builder productCategory(String productCategory) {
            this.productCategory = productCategory;
            return this;
        }

        public CalculateTaxCommand build() {
            return new CalculateTaxCommand(amount, jurisdiction, taxType, productCategory);
        }
    }
}
```

**`/modules/pricing/application/src/main/java/tech/kayys/erp/pricing/application/api/query/PriceCalculationView.java`**:

```java
package tech.kayys.erp.pricing.application.api.query;

import tech.kayys.erp.pricing.domain.model.PriceCalculation;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * View of a price calculation result.
 */
public record PriceCalculationView(
        String productId,
        String basePrice,
        String discountedPrice,
        String taxAmount,
        String finalPrice,
        String currencyCode,
        List<DiscountView> appliedDiscounts,
        List<TaxView> appliedTaxes,
        BigDecimal totalDiscountPercentage,
        boolean hasDiscounts,
        String calculatedAt
) {

    public static PriceCalculationView fromDomain(PriceCalculation calculation) {
        return new PriceCalculationView(
            calculation.getProductId().toString(),
            calculation.getBasePrice().getAmount().toPlainString(),
            calculation.getDiscountedPrice() != null ? 
                calculation.getDiscountedPrice().getAmount().toPlainString() : null,
            calculation.getTaxAmount() != null && !calculation.getTaxAmount().isZero() ?
                calculation.getTaxAmount().getAmount().toPlainString() : "0.00",
            calculation.getFinalPrice().getAmount().toPlainString(),
            calculation.getCurrencyCode(),
            calculation.getAppliedDiscounts().stream()
                .map(DiscountView::fromDomain)
                .collect(Collectors.toList()),
            calculation.getAppliedTaxes().stream()
                .map(TaxView::fromDomain)
                .collect(Collectors.toList()),
            calculation.getTotalDiscountPercentage(),
            calculation.hasDiscounts(),
            calculation.getCalculatedAt().toString()
        );
    }

    public record DiscountView(
            String ruleName,
            String type,
            BigDecimal value,
            String discountAmount,
            String currencyCode
    ) {
        public static DiscountView fromDomain(PriceCalculation.AppliedDiscount discount) {
            return new DiscountView(
                discount.getRuleName(),
                discount.getType(),
                discount.getValue(),
                discount.getDiscountAmount().getAmount().toPlainString(),
                discount.getDiscountAmount().getCurrency().getCurrencyCode()
            );
        }
    }

    public record TaxView(
            String taxType,
            String jurisdiction,
            BigDecimal rate,
            String taxAmount,
            String currencyCode
    ) {
        public static TaxView fromDomain(PriceCalculation.AppliedTax tax) {
            return new TaxView(
                tax.getTaxType(),
                tax.getJurisdiction(),
                tax.getRate(),
                tax.getTaxAmount().getAmount().toPlainString(),
                tax.getTaxAmount().getCurrency().getCurrencyCode()
            );
        }
    }
}
```

**`/modules/pricing/application/src/main/java/tech/kayys/erp/pricing/application/api/query/TaxCalculationView.java`**:

```java
package tech.kayys.erp.pricing.application.api.query;

import tech.kayys.erp.pricing.domain.valueobject.Money;

/**
 * View of a tax calculation result.
 */
public record TaxCalculationView(
        String taxableAmount,
        String taxRate,
        String taxAmount,
        String totalAmount,
        String currencyCode,
        String jurisdiction,
        String taxType
) {

    public static TaxCalculationView fromDomain(
            Money taxableAmount,
            Money taxAmount,
            String jurisdiction,
            String taxType,
            BigDecimal rate) {
        
        return new TaxCalculationView(
            taxableAmount.getAmount().toPlainString(),
            rate.toPlainString() + "%",
            taxAmount.getAmount().toPlainString(),
            taxableAmount.add(taxAmount).getAmount().toPlainString(),
            taxableAmount.getCurrency().getCurrencyCode(),
            jurisdiction,
            taxType
        );
    }
}
```

**`/modules/pricing/application/src/main/java/tech/kayys/erp/pricing/application/api/MoneyCommand.java`**:

```java
package tech.kayys.erp.pricing.application.api;

import java.math.BigDecimal;

/**
 * Money DTO for commands.
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
        // Validate amount is numeric
        try {
            new BigDecimal(amount);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + amount);
        }
    }

    public BigDecimal getAmountAsBigDecimal() {
        return new BigDecimal(amount);
    }
}
```

**`/modules/pricing/application/src/main/java/tech/kayys/erp/pricing/application/internal/PricingServiceImpl.java`**:

```java
package tech.kayys.erp.pricing.application.internal;

import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.pricing.application.api.CouponValidationResult;
import tech.kayys.erp.pricing.application.api.MoneyCommand;
import tech.kayys.erp.pricing.application.api.PricingService;
import tech.kayys.erp.pricing.application.api.command.CalculatePriceCommand;
import tech.kayys.erp.pricing.application.api.command.CalculateTaxCommand;
import tech.kayys.erp.pricing.application.api.query.PriceCalculationView;
import tech.kayys.erp.pricing.application.api.query.TaxCalculationView;
import tech.kayys.erp.pricing.domain.identifier.ProductId;
import tech.kayys.erp.pricing.domain.model.PriceCalculation;
import tech.kayys.erp.pricing.domain.model.PricingRule;
import tech.kayys.erp.pricing.domain.repository.PricingRuleRepository;
import tech.kayys.erp.pricing.domain.repository.TaxRateRepository;
import tech.kayys.erp.pricing.domain.valueobject.Money;
import tech.kayys.erp.pricing.domain.valueobject.TaxRate;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Implementation of the PricingService.
 * This is the core pricing engine that applies all pricing rules and tax calculations.
 */
@Singleton
@UseCase("Pricing calculation service")
public class PricingServiceImpl implements PricingService {

    private final PricingRuleRepository pricingRuleRepository;
    private final TaxRateRepository taxRateRepository;

    @Inject
    public PricingServiceImpl(
            PricingRuleRepository pricingRuleRepository,
            TaxRateRepository taxRateRepository) {
        this.pricingRuleRepository = pricingRuleRepository;
        this.taxRateRepository = taxRateRepository;
    }

    @Override
    public CompletionStage<PriceCalculationView> calculatePrice(CalculatePriceCommand command) {
        // 1. Get base price as Money
        Money basePrice = Money.of(
            command.basePrice().getAmountAsBigDecimal(),
            command.basePrice().currencyCode()
        );

        ProductId productId = ProductId.of(command.productId());
        int quantity = command.quantity();

        // 2. Find applicable pricing rules
        return findApplicableRules(command.productId(), command.couponCode())
            .thenCompose(rules -> {
                // 3. Apply rules in priority order
                PriceCalculation.Builder calculationBuilder = PriceCalculation.builder()
                    .productId(productId)
                    .basePrice(basePrice);

                Money currentPrice = basePrice;

                // Sort rules by priority (higher priority first)
                List<PricingRule> sortedRules = rules.stream()
                    .sorted(Comparator.comparing(PricingRule::getPriority).reversed())
                    .toList();

                // Apply each rule
                for (PricingRule rule : sortedRules) {
                    Money discountAmount = rule.calculateDiscount(currentPrice, quantity);
                    
                    if (!discountAmount.isZero()) {
                        currentPrice = currentPrice.subtract(discountAmount);
                        calculationBuilder.addDiscount(
                            new PriceCalculation.AppliedDiscount(
                                rule.getId().toString(),
                                rule.getName(),
                                rule.getDiscountType().name(),
                                rule.getDiscountValue(),
                                discountAmount
                            )
                        );
                    }
                }

                calculationBuilder.discountedPrice(currentPrice);

                // 4. Calculate taxes
                return calculateTaxes(currentPrice, command.jurisdiction())
                    .thenApply(taxAmount -> {
                        Money finalPrice = currentPrice.add(taxAmount);
                        calculationBuilder
                            .taxAmount(taxAmount)
                            .finalPrice(finalPrice);

                        // Add tax details
                        taxAmount.getAmount(); // We'll add tax details separately

                        return PriceCalculationView.fromDomain(calculationBuilder.build());
                    });
            });
    }

    @Override
    public CompletionStage<TaxCalculationView> calculateTax(CalculateTaxCommand command) {
        Money amount = Money.of(
            command.amount().getAmountAsBigDecimal(),
            command.amount().currencyCode()
        );

        return taxRateRepository.findByJurisdiction(command.jurisdiction())
            .thenApply(taxRates -> {
                // Find applicable tax rate for this product category
                TaxRate applicableRate = taxRates.stream()
                    .filter(rate -> rate.isCurrentlyEffective())
                    .filter(rate -> command.productCategory() == null || 
                        rate.getProductCategory().equals(command.productCategory()))
                    .findFirst()
                    .orElse(null);

                if (applicableRate == null) {
                    // No tax applicable
                    return TaxCalculationView.fromDomain(
                        amount,
                        Money.zero(amount.getCurrency().getCurrencyCode()),
                        command.jurisdiction(),
                        command.taxType().name(),
                        BigDecimal.ZERO
                    );
                }

                Money taxAmount = amount.percentage(applicableRate.getRate());
                
                return TaxCalculationView.fromDomain(
                    amount,
                    taxAmount,
                    command.jurisdiction(),
                    command.taxType().name(),
                    applicableRate.getRate()
                );
            });
    }

    @Override
    public CompletionStage<CouponValidationResult> validateCoupon(String couponCode, Money amount) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                new CouponValidationResult(false, "Coupon code is empty")
            );
        }

        return pricingRuleRepository.findByCouponCode(couponCode)
            .thenApply(rule -> {
                if (rule == null) {
                    return new CouponValidationResult(false, "Invalid coupon code");
                }
                if (!rule.isActive()) {
                    return new CouponValidationResult(false, "Coupon is expired or inactive");
                }
                if (rule.getMinimumOrderAmount() != null) {
                    Money minAmount = Money.of(rule.getMinimumOrderAmount(), amount.getCurrency().getCurrencyCode());
                    if (amount.isLessThan(minAmount)) {
                        return new CouponValidationResult(false, 
                            "Minimum order amount of " + minAmount + " required");
                    }
                }
                return new CouponValidationResult(true, "Coupon is valid", rule);
            });
    }

    @Override
    public CompletionStage<Money> getBasePrice(UUID productId) {
        // This would typically call the Catalog context
        // For now, return a default
        return CompletableFuture.completedFuture(Money.of("0.00", "USD"));
    }

    private CompletionStage<List<PricingRule>> findApplicableRules(UUID productId, String couponCode) {
        return pricingRuleRepository.findApplicableRules(productId)
            .thenApply(rules -> {
                List<PricingRule> applicableRules = new ArrayList<>(rules);
                
                // Add coupon rule if present
                if (couponCode != null && !couponCode.trim().isEmpty()) {
                    pricingRuleRepository.findByCouponCode(couponCode)
                        .thenAccept(couponRule -> {
                            if (couponRule != null && couponRule.isActive()) {
                                applicableRules.add(couponRule);
                            }
                        });
                }
                
                return applicableRules;
            });
    }

    private CompletionStage<Money> calculateTaxes(Money amount, String jurisdiction) {
        return taxRateRepository.findByJurisdiction(jurisdiction)
            .thenApply(taxRates -> {
                // Find the highest applicable tax rate
                // In a real system, you might apply multiple taxes
                TaxRate applicableRate = taxRates.stream()
                    .filter(TaxRate::isCurrentlyEffective)
                    .findFirst()
                    .orElse(null);

                if (applicableRate == null) {
                    return Money.zero(amount.getCurrency().getCurrencyCode());
                }

                return amount.percentage(applicableRate.getRate());
            });
    }
}
```

**`/modules/pricing/application/src/main/java/tech/kayys/erp/pricing/application/api/CouponValidationResult.java`**:

```java
package tech.kayys.erp.pricing.application.api;

import tech.kayys.erp.pricing.domain.model.PricingRule;

/**
 * Result of coupon validation.
 */
public record CouponValidationResult(
        boolean valid,
        String message,
        PricingRule rule
) {
    public CouponValidationResult(boolean valid, String message) {
        this(valid, message, null);
    }

    public boolean isValid() {
        return valid;
    }
}
```

## 3. Update Root POM

**Update `/pom.xml`** to include Pricing modules:

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
</modules>
```

## 4. Update Architecture Tests with Pricing Rules

**`/architecture/tests/src/test/java/tech/kayys/erp/architecture/CompleteArchitectureTest.java`** (add Pricing context rules):

```java
// Add to existing CompleteArchitectureTest class:

@ArchTest
static final ArchRule pricingDomainMustNotDependOnOtherContexts =
        noClasses()
                .that()
                .resideInAPackage("tech.kayys.erp.pricing.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "tech.kayys.erp.catalog..",
                        "tech.kayys.erp.sales..",
                        "tech.kayys.erp.inventory.."
                );

@ArchTest
static final ArchRule pricingApplicationMayUsePorts =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.pricing.application.port..")
                .should()
                .haveSimpleNameEndingWith("Port")
                .orShould()
                .haveSimpleNameEndingWith("Provider");

@ArchTest
static final ArchRule pricingDomainPackagesCorrect =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.pricing.domain..")
                .should()
                .resideInAnyPackage(
                        "tech.kayys.erp.pricing.domain.model..",
                        "tech.kayys.erp.pricing.domain.identifier..",
                        "tech.kayys.erp.pricing.domain.valueobject..",
                        "tech.kayys.erp.pricing.domain.repository.."
                );

@ArchTest
static final ArchRule pricingApplicationShouldNotDependOnCatalogDirectly =
        noClasses()
                .that()
                .resideInAPackage("tech.kayys.erp.pricing.application..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("tech.kayys.erp.catalog..")
                .andShould()
                .haveFullyQualifiedName("tech.kayys.erp.catalog.domain.model.Product");
```

## 5. Integration Example: Using Pricing from Sales

**`/modules/sales/application/src/main/java/tech/kayys/erp/sales/application/port/PricingPort.java`**:

```java
package tech.kayys.erp.sales.application.port;

import tech.kayys.erp.sales.domain.valueobject.Money;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Port for pricing calculations from the Pricing context.
 */
public interface PricingPort {

    /**
     * Gets the final price for a product including all discounts and taxes.
     */
    CompletionStage<PriceResult> calculatePrice(
        UUID productId,
        int quantity,
        Money basePrice,
        String jurisdiction,
        String couponCode
    );

    /**
     * Validates a coupon code.
     */
    CompletionStage<CouponValidationResult> validateCoupon(String couponCode, Money orderTotal);

    /**
     * Calculates tax for a given amount.
     */
    CompletionStage<TaxResult> calculateTax(Money amount, String jurisdiction);

    /**
     * Price result from pricing calculation.
     */
    record PriceResult(
        Money finalPrice,
        Money taxAmount,
        Money discountAmount,
        boolean hasDiscounts,
        String appliedRuleName
    ) {}

    /**
     * Coupon validation result.
     */
    record CouponValidationResult(
        boolean valid,
        String message,
        String discountType,
        BigDecimal discountValue
    ) {}

    /**
     * Tax calculation result.
     */
    record TaxResult(
        Money taxAmount,
        BigDecimal taxRate
    ) {}
}
```

This complete Pricing implementation provides:

1. **Domain Models**: PricingRule, TaxRate, PriceCalculation, PricingTier
2. **Value Objects**: Money, DiscountType, TaxType, TaxRate
3. **Repositories**: PricingRuleRepository, TaxRateRepository
4. **Application Services**: PricingService with price calculation engine
5. **Public APIs**: CalculatePriceCommand, CalculateTaxCommand
6. **Integration Points**: Ports for Sales context to use
7. **Architecture Rules**: Proper isolation between contexts

The Pricing context demonstrates sophisticated domain logic including:
- Volume-based pricing tiers
- Coupon validation
- Tax calculation by jurisdiction
- Discount prioritization
- Stackable vs non-stackable rules
- Time-based validity periods
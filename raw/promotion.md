# Complete Implementation: Promotion / Marketing Bounded Context

Now I'll implement the complete Promotion/Marketing bounded context, which handles promotional campaigns, discount rules, loyalty programs, and marketing automation. This context is used by Sales, Pricing, and CRM contexts.

## 1. Promotion Domain Module

**`/modules/promotion/domain/pom.xml`**:

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

    <artifactId>erp-promotion-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/promotion/domain/src/main/java/tech/kayys/erp/promotion/domain/identifier/PromotionId.java`**:

```java
package tech.kayys.erp.promotion.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Promotion identifier.
 */
public final class PromotionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public PromotionId(UUID value) {
        super(value);
    }

    public static PromotionId of(UUID value) {
        return new PromotionId(value);
    }

    public static PromotionId generate() {
        return new PromotionId(UUID.randomUUID());
    }

    public static PromotionId fromString(String value) {
        return new PromotionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PromotionId{" + value + "}";
    }
}
```

**`/modules/promotion/domain/src/main/java/tech/kayys/erp/promotion/domain/identifier/CampaignId.java`**:

```java
package tech.kayys.erp.promotion.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Marketing campaign identifier.
 */
public final class CampaignId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public CampaignId(UUID value) {
        super(value);
    }

    public static CampaignId of(UUID value) {
        return new CampaignId(value);
    }

    public static CampaignId generate() {
        return new CampaignId(UUID.randomUUID());
    }

    public static CampaignId fromString(String value) {
        return new CampaignId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CampaignId{" + value + "}";
    }
}
```

**`/modules/promotion/domain/src/main/java/tech/kayys/erp/promotion/domain/identifier/LoyaltyId.java`**:

```java
package tech.kayys.erp.promotion.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Loyalty program identifier.
 */
public final class LoyaltyId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public LoyaltyId(UUID value) {
        super(value);
    }

    public static LoyaltyId of(UUID value) {
        return new LoyaltyId(value);
    }

    public static LoyaltyId generate() {
        return new LoyaltyId(UUID.randomUUID());
    }

    public static LoyaltyId fromString(String value) {
        return new LoyaltyId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "LoyaltyId{" + value + "}";
    }
}
```

**`/modules/promotion/domain/src/main/java/tech/kayys/erp/promotion/domain/identifier/CustomerId.java`**:

```java
package tech.kayys.erp.promotion.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Customer identifier in the Promotion context.
 */
public final class CustomerId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public CustomerId(UUID value) {
        super(value);
    }

    public static CustomerId of(UUID value) {
        return new CustomerId(value);
    }

    public static CustomerId fromString(String value) {
        return new CustomerId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CustomerId{" + value + "}";
    }
}
```

**`/modules/promotion/domain/src/main/java/tech/kayys/erp/promotion/domain/valueobject/Money.java`**:

```java
package tech.kayys.erp.promotion.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Money value object for the Promotion context.
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

**`/modules/promotion/domain/src/main/java/tech/kayys/erp/promotion/domain/valueobject/PromotionType.java`**:

```java
package tech.kayys.erp.promotion.domain.valueobject;

/**
 * Types of promotions.
 */
public enum PromotionType {
    PERCENTAGE_DISCOUNT("Percentage Discount"),
    FIXED_AMOUNT_DISCOUNT("Fixed Amount Discount"),
    BUY_ONE_GET_ONE("Buy One Get One"),
    BUY_X_GET_Y("Buy X Get Y Free"),
    FREE_SHIPPING("Free Shipping"),
    GIFT_WITH_PURCHASE("Gift With Purchase"),
    BUNDLE("Bundle Discount"),
    COUPON("Coupon Code"),
    VOLUME_DISCOUNT("Volume Discount"),
    SEASONAL("Seasonal Promotion"),
    FLASH_SALE("Flash Sale"),
    LOYALTY_REWARD("Loyalty Reward"),
    REFERRAL("Referral Promotion");

    private final String displayName;

    PromotionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isDiscount() {
        return this == PERCENTAGE_DISCOUNT || this == FIXED_AMOUNT_DISCOUNT || 
               this == VOLUME_DISCOUNT || this == LOYALTY_REWARD;
    }

    public boolean isBogo() {
        return this == BUY_ONE_GET_ONE || this == BUY_X_GET_Y;
    }

    public boolean isShippingRelated() {
        return this == FREE_SHIPPING;
    }
}
```

**`/modules/promotion/domain/src/main/java/tech/kayys/erp/promotion/domain/valueobject/PromotionStatus.java`**:

```java
package tech.kayys.erp.promotion.domain.valueobject;

/**
 * Status of a promotion.
 */
public enum PromotionStatus {
    DRAFT("Draft - being created"),
    SCHEDULED("Scheduled - waiting to start"),
    ACTIVE("Active - currently running"),
    PAUSED("Paused - temporarily inactive"),
    COMPLETED("Completed - ended naturally"),
    CANCELLED("Cancelled - ended early"),
    EXPIRED("Expired - passed end date");

    private final String description;

    PromotionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isSchedulable() {
        return this == DRAFT || this == SCHEDULED;
    }

    public boolean canTransitionTo(PromotionStatus target) {
        return switch (this) {
            case DRAFT -> target == SCHEDULED || target == CANCELLED;
            case SCHEDULED -> target == ACTIVE || target == CANCELLED;
            case ACTIVE -> target == PAUSED || target == COMPLETED || target == EXPIRED;
            case PAUSED -> target == ACTIVE || target == COMPLETED || target == EXPIRED || target == CANCELLED;
            case COMPLETED, EXPIRED, CANCELLED -> false;
        };
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == EXPIRED || this == CANCELLED;
    }
}
```

**`/modules/promotion/domain/src/main/java/tech/kayys/erp/promotion/domain/valueobject/TargetAudience.java`**:

```java
package tech.kayys.erp.promotion.domain.valueobject;

/**
 * Target audience for promotions.
 */
public enum TargetAudience {
    ALL_CUSTOMERS("All Customers"),
    NEW_CUSTOMERS("New Customers"),
    RETURNING_CUSTOMERS("Returning Customers"),
    VIP_CUSTOMERS("VIP Customers"),
    LOYALTY_MEMBERS("Loyalty Members"),
    CART_ABANDONERS("Cart Abandoners"),
    SEGMENT_A("Segment A"),
    SEGMENT_B("Segment B"),
    SEGMENT_C("Segment C"),
    BUSINESS_CUSTOMERS("Business Customers"),
    RETAIL_CUSTOMERS("Retail Customers"),
    REGISTERED_ONLY("Registered Only"),
    FIRST_PURCHASE("First Purchase"),
    HIGH_VALUE("High Value Customers");

    private final String displayName;

    TargetAudience(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean matchesCustomer(CustomerSegment segment) {
        return switch (this) {
            case ALL_CUSTOMERS -> true;
            case NEW_CUSTOMERS -> segment == CustomerSegment.NEW;
            case RETURNING_CUSTOMERS -> segment == CustomerSegment.RETURNING;
            case VIP_CUSTOMERS -> segment == CustomerSegment.VIP;
            case LOYALTY_MEMBERS -> segment == CustomerSegment.LOYALTY;
            case HIGH_VALUE -> segment == CustomerSegment.HIGH_VALUE;
            default -> false;
        };
    }

    public enum CustomerSegment {
        NEW, RETURNING, VIP, LOYALTY, HIGH_VALUE, REGULAR
    }
}
```

**`/modules/promotion/domain/src/main/java/tech/kayys/erp/promotion/domain/model/Promotion.java`**:

```java
package tech.kayys.erp.promotion.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.promotion.domain.identifier.PromotionId;
import tech.kayys.erp.promotion.domain.valueobject.Money;
import tech.kayys.erp.promotion.domain.valueobject.PromotionStatus;
import tech.kayys.erp.promotion.domain.valueobject.PromotionType;
import tech.kayys.erp.promotion.domain.valueobject.TargetAudience;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Promotion aggregate root.
 * Represents a marketing promotion with discount rules and targeting.
 */
public final class Promotion extends AggregateRoot<PromotionId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private String promoCode;
    private PromotionType promotionType;
    private PromotionStatus status;
    private BigDecimal discountValue; // Percentage or fixed amount
    private Money discountAmount;
    private String currencyCode;
    private Money minimumOrderAmount;
    private Money maximumDiscountAmount;
    private TargetAudience targetAudience;
    private List<UUID> applicableProductIds; // null = applies to all
    private List<String> applicableCategories; // null = applies to all
    private Instant startDate;
    private Instant endDate;
    private int usageLimitPerCustomer;
    private int totalUsageLimit;
    private int currentUsageCount;
    private boolean stackable;
    private int priority;
    private boolean requiresCoupon;
    private boolean active;
    private String termsAndConditions;
    private String createdBy;
    private String approvedBy;
    private Instant approvedAt;
    private List<PromotionRedemption> redemptions;

    private Promotion(PromotionId id) {
        super(id);
        this.status = PromotionStatus.DRAFT;
        this.active = true;
        this.stackable = false;
        this.priority = 0;
        this.requiresCoupon = false;
        this.usageLimitPerCustomer = 1;
        this.totalUsageLimit = Integer.MAX_VALUE;
        this.currentUsageCount = 0;
        this.redemptions = new ArrayList<>();
        this.applicableProductIds = new ArrayList<>();
        this.applicableCategories = new ArrayList<>();
    }

    private Promotion() {
        super();
    }

    /**
     * Factory method to create a new promotion.
     */
    public static Promotion create(
            PromotionId id,
            String name,
            PromotionType promotionType,
            BigDecimal discountValue,
            String currencyCode,
            Instant startDate,
            Instant endDate) {
        Promotion promotion = new Promotion(id);
        promotion.name = name;
        promotion.promotionType = promotionType;
        promotion.discountValue = discountValue;
        promotion.currencyCode = currencyCode;
        promotion.startDate = startDate;
        promotion.endDate = endDate;
        return promotion;
    }

    /**
     * Activates the promotion.
     */
    public void activate() {
        if (status != PromotionStatus.SCHEDULED && status != PromotionStatus.DRAFT) {
            throw new IllegalStateException("Cannot activate promotion in status: " + status);
        }
        if (Instant.now().isAfter(endDate)) {
            throw new IllegalStateException("Cannot activate expired promotion");
        }
        
        this.status = PromotionStatus.ACTIVE;
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Pauses the promotion.
     */
    public void pause() {
        if (status != PromotionStatus.ACTIVE) {
            throw new IllegalStateException("Cannot pause promotion in status: " + status);
        }
        
        this.status = PromotionStatus.PAUSED;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Schedules the promotion.
     */
    public void schedule() {
        if (status != PromotionStatus.DRAFT) {
            throw new IllegalStateException("Cannot schedule promotion in status: " + status);
        }
        
        this.status = PromotionStatus.SCHEDULED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Completes the promotion.
     */
    public void complete() {
        if (status != PromotionStatus.ACTIVE && status != PromotionStatus.PAUSED) {
            throw new IllegalStateException("Cannot complete promotion in status: " + status);
        }
        
        this.status = PromotionStatus.COMPLETED;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the promotion.
     */
    public void cancel(String reason) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Promotion is already terminated");
        }
        
        this.status = PromotionStatus.CANCELLED;
        this.active = false;
        this.description = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if the promotion is currently valid.
     */
    public boolean isValid() {
        if (!active || status != PromotionStatus.ACTIVE) {
            return false;
        }
        Instant now = Instant.now();
        if (now.isBefore(startDate) || now.isAfter(endDate)) {
            return false;
        }
        if (currentUsageCount >= totalUsageLimit) {
            return false;
        }
        return true;
    }

    /**
     * Redemption validation result.
     */
    public record RedemptionResult(
        boolean valid,
        String message,
        Money discountAmount
    ) {}

    /**
     * Validates if a redemption is valid for a customer and amount.
     */
    public RedemptionResult validateRedemption(
            UUID customerId,
            Money orderAmount,
            int customerRedemptionCount) {
        
        if (!isValid()) {
            return new RedemptionResult(false, "Promotion is not currently active", Money.zero(currencyCode));
        }
        
        if (customerRedemptionCount >= usageLimitPerCustomer) {
            return new RedemptionResult(false, "Usage limit per customer exceeded", Money.zero(currencyCode));
        }
        
        if (minimumOrderAmount != null && orderAmount.isLessThan(minimumOrderAmount)) {
            return new RedemptionResult(false, 
                "Minimum order amount of " + minimumOrderAmount + " required", 
                Money.zero(currencyCode)
            );
        }
        
        // Calculate discount
        Money discount = calculateDiscount(orderAmount);
        
        if (maximumDiscountAmount != null && discount.isGreaterThan(maximumDiscountAmount)) {
            discount = maximumDiscountAmount;
        }
        
        if (discount.isZero()) {
            return new RedemptionResult(false, "No discount applicable", discount);
        }
        
        return new RedemptionResult(true, "Redemption valid", discount);
    }

    /**
     * Calculates the discount for a given amount.
     */
    public Money calculateDiscount(Money amount) {
        return switch (promotionType) {
            case PERCENTAGE_DISCOUNT, LOYALTY_REWARD -> amount.percentage(discountValue);
            case FIXED_AMOUNT_DISCOUNT -> Money.of(discountValue, currencyCode);
            case FREE_SHIPPING -> Money.of(discountValue, currencyCode); // Shipping cost
            default -> Money.zero(currencyCode);
        };
    }

    /**
     * Records a redemption.
     */
    public void recordRedemption(UUID customerId, UUID orderId, Money discountAmount) {
        if (!isValid()) {
            throw new IllegalStateException("Promotion is not valid");
        }
        
        PromotionRedemption redemption = new PromotionRedemption(
            customerId,
            orderId,
            discountAmount,
            Instant.now()
        );
        
        redemptions.add(redemption);
        currentUsageCount++;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the number of redemptions for a customer.
     */
    public int getCustomerRedemptionCount(UUID customerId) {
        return (int) redemptions.stream()
            .filter(r -> r.customerId().equals(customerId))
            .count();
    }

    /**
     * Checks if the promotion applies to a product.
     */
    public boolean appliesToProduct(UUID productId) {
        if (applicableProductIds.isEmpty()) {
            return true;
        }
        return applicableProductIds.contains(productId);
    }

    /**
     * Checks if the promotion applies to a category.
     */
    public boolean appliesToCategory(String category) {
        if (applicableCategories.isEmpty()) {
            return true;
        }
        return applicableCategories.contains(category);
    }

    /**
     * Gets the remaining usage count.
     */
    public int getRemainingUsage() {
        return totalUsageLimit - currentUsageCount;
    }

    /**
     * Gets the usage percentage.
     */
    public double getUsagePercentage() {
        if (totalUsageLimit == Integer.MAX_VALUE) {
            return 0.0;
        }
        return (double) currentUsageCount / totalUsageLimit * 100.0;
    }

    // Getters and Setters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPromoCode() { return promoCode; }
    public PromotionType getPromotionType() { return promotionType; }
    public PromotionStatus getStatus() { return status; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public Money getDiscountAmount() { return discountAmount; }
    public String getCurrencyCode() { return currencyCode; }
    public Money getMinimumOrderAmount() { return minimumOrderAmount; }
    public Money getMaximumDiscountAmount() { return maximumDiscountAmount; }
    public TargetAudience getTargetAudience() { return targetAudience; }
    public List<UUID> getApplicableProductIds() { return Collections.unmodifiableList(applicableProductIds); }
    public List<String> getApplicableCategories() { return Collections.unmodifiableList(applicableCategories); }
    public Instant getStartDate() { return startDate; }
    public Instant getEndDate() { return endDate; }
    public int getUsageLimitPerCustomer() { return usageLimitPerCustomer; }
    public int getTotalUsageLimit() { return totalUsageLimit; }
    public int getCurrentUsageCount() { return currentUsageCount; }
    public boolean isStackable() { return stackable; }
    public int getPriority() { return priority; }
    public boolean isRequiresCoupon() { return requiresCoupon; }
    public boolean isActive() { return active; }
    public String getTermsAndConditions() { return termsAndConditions; }
    public String getCreatedBy() { return createdBy; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public List<PromotionRedemption> getRedemptions() { return Collections.unmodifiableList(redemptions); }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDiscountAmount(Money discountAmount) {
        this.discountAmount = discountAmount;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMinimumOrderAmount(Money minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMaximumDiscountAmount(Money maximumDiscountAmount) {
        this.maximumDiscountAmount = maximumDiscountAmount;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTargetAudience(TargetAudience targetAudience) {
        this.targetAudience = targetAudience;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setApplicableProductIds(List<UUID> applicableProductIds) {
        this.applicableProductIds = new ArrayList<>(applicableProductIds);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setApplicableCategories(List<String> applicableCategories) {
        this.applicableCategories = new ArrayList<>(applicableCategories);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setUsageLimitPerCustomer(int usageLimitPerCustomer) {
        this.usageLimitPerCustomer = usageLimitPerCustomer;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTotalUsageLimit(int totalUsageLimit) {
        this.totalUsageLimit = totalUsageLimit;
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

    public void setRequiresCoupon(boolean requiresCoupon) {
        this.requiresCoupon = requiresCoupon;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void approve(String approvedBy) {
        if (status != PromotionStatus.DRAFT && status != PromotionStatus.SCHEDULED) {
            throw new IllegalStateException("Cannot approve promotion in status: " + status);
        }
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", type=" + promotionType +
                ", status=" + status +
                ", usage=" + currentUsageCount + "/" + totalUsageLimit +
                '}';
    }

    /**
     * Promotion redemption record.
     */
    public record PromotionRedemption(
            UUID customerId,
            UUID orderId,
            Money discountAmount,
            Instant redeemedAt
    ) {
        public PromotionRedemption {
            if (customerId == null) {
                throw new IllegalArgumentException("Customer ID cannot be null");
            }
            if (discountAmount == null) {
                throw new IllegalArgumentException("Discount amount cannot be null");
            }
            if (redeemedAt == null) {
                throw new IllegalArgumentException("Redeemed at cannot be null");
            }
        }
    }
}
```

**`/modules/promotion/domain/src/main/java/tech/kayys/erp/promotion/domain/model/Campaign.java`**:

```java
package tech.kayys.erp.promotion.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.promotion.domain.identifier.CampaignId;
import tech.kayys.erp.promotion.domain.valueobject.Money;
import tech.kayys.erp.promotion.domain.valueobject.TargetAudience;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Marketing campaign aggregate root.
 * Represents a coordinated marketing effort with multiple promotions.
 */
public final class Campaign extends AggregateRoot<CampaignId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private String campaignCode;
    private CampaignType campaignType;
    private CampaignStatus status;
    private TargetAudience targetAudience;
    private List<PromotionId> promotionIds;
    private List<String> channels; // Email, SMS, Social, etc.
    private Money budget;
    private Money spent;
    private Money roi;
    private Instant startDate;
    private Instant endDate;
    private String createdBy;
    private String approvedBy;
    private Instant approvedAt;
    private CampaignMetrics metrics;
    private boolean active;

    private Campaign(CampaignId id) {
        super(id);
        this.promotionIds = new ArrayList<>();
        this.channels = new ArrayList<>();
        this.status = CampaignStatus.DRAFT;
        this.active = true;
        this.metrics = new CampaignMetrics(0, 0, 0, 0, 0);
        this.spent = Money.zero("USD");
        this.roi = Money.zero("USD");
    }

    private Campaign() {
        super();
    }

    /**
     * Factory method to create a new campaign.
     */
    public static Campaign create(
            CampaignId id,
            String name,
            CampaignType campaignType,
            Instant startDate,
            Instant endDate,
            String currencyCode) {
        Campaign campaign = new Campaign(id);
        campaign.name = name;
        campaign.campaignType = campaignType;
        campaign.startDate = startDate;
        campaign.endDate = endDate;
        campaign.budget = Money.zero(currencyCode);
        return campaign;
    }

    /**
     * Adds a promotion to the campaign.
     */
    public void addPromotion(PromotionId promotionId) {
        if (status != CampaignStatus.DRAFT && status != CampaignStatus.PLANNED) {
            throw new IllegalStateException("Cannot modify campaign in status: " + status);
        }
        if (!promotionIds.contains(promotionId)) {
            promotionIds.add(promotionId);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a promotion from the campaign.
     */
    public void removePromotion(PromotionId promotionId) {
        if (status != CampaignStatus.DRAFT && status != CampaignStatus.PLANNED) {
            throw new IllegalStateException("Cannot modify campaign in status: " + status);
        }
        promotionIds.remove(promotionId);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Launches the campaign.
     */
    public void launch() {
        if (status != CampaignStatus.PLANNED && status != CampaignStatus.DRAFT) {
            throw new IllegalStateException("Cannot launch campaign in status: " + status);
        }
        if (promotionIds.isEmpty()) {
            throw new IllegalStateException("Campaign must have at least one promotion");
        }
        
        this.status = CampaignStatus.ACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Pauses the campaign.
     */
    public void pause() {
        if (status != CampaignStatus.ACTIVE) {
            throw new IllegalStateException("Cannot pause campaign in status: " + status);
        }
        
        this.status = CampaignStatus.PAUSED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Ends the campaign.
     */
    public void end() {
        if (status != CampaignStatus.ACTIVE && status != CampaignStatus.PAUSED) {
            throw new IllegalStateException("Cannot end campaign in status: " + status);
        }
        
        this.status = CampaignStatus.ENDED;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records campaign metrics.
     */
    public void recordMetrics(int impressions, int clicks, int conversions, int revenue, int cost) {
        this.metrics = new CampaignMetrics(
            impressions,
            clicks,
            conversions,
            revenue,
            cost
        );
        calculateROI();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates campaign spend.
     */
    public void recordSpend(Money amount) {
        this.spent = spent.add(amount);
        calculateROI();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private void calculateROI() {
        if (spent.isZero()) {
            roi = Money.zero(spent.getCurrency().getCurrencyCode());
            return;
        }
        // Revenue from conversions minus spend
        Money revenue = Money.of(metrics.revenue(), spent.getCurrency().getCurrencyCode());
        roi = revenue.subtract(spent);
    }

    /**
     * Gets the campaign conversion rate.
     */
    public double getConversionRate() {
        if (metrics.clicks() == 0) {
            return 0.0;
        }
        return (double) metrics.conversions() / metrics.clicks() * 100.0;
    }

    /**
     * Gets the campaign ROI percentage.
     */
    public double getROIPercentage() {
        if (spent.isZero()) {
            return 0.0;
        }
        Money revenue = Money.of(metrics.revenue(), spent.getCurrency().getCurrencyCode());
        Money profit = revenue.subtract(spent);
        return profit.getAmount()
            .divide(spent.getAmount(), 4, java.math.RoundingMode.HALF_UP)
            .multiply(java.math.BigDecimal.valueOf(100))
            .doubleValue();
    }

    /**
     * Gets the cost per acquisition.
     */
    public Money getCostPerAcquisition() {
        if (metrics.conversions() == 0) {
            return Money.zero(spent.getCurrency().getCurrencyCode());
        }
        return spent.divide(java.math.BigDecimal.valueOf(metrics.conversions()));
    }

    // Getters and Setters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCampaignCode() { return campaignCode; }
    public CampaignType getCampaignType() { return campaignType; }
    public CampaignStatus getStatus() { return status; }
    public TargetAudience getTargetAudience() { return targetAudience; }
    public List<PromotionId> getPromotionIds() { return Collections.unmodifiableList(promotionIds); }
    public List<String> getChannels() { return Collections.unmodifiableList(channels); }
    public Money getBudget() { return budget; }
    public Money getSpent() { return spent; }
    public Money getRoi() { return roi; }
    public Instant getStartDate() { return startDate; }
    public Instant getEndDate() { return endDate; }
    public String getCreatedBy() { return createdBy; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public CampaignMetrics getMetrics() { return metrics; }
    public boolean isActive() { return active && status == CampaignStatus.ACTIVE; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCampaignCode(String campaignCode) {
        this.campaignCode = campaignCode;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTargetAudience(TargetAudience targetAudience) {
        this.targetAudience = targetAudience;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setChannels(List<String> channels) {
        this.channels = new ArrayList<>(channels);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setBudget(Money budget) {
        this.budget = budget;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void approve(String approvedBy) {
        if (status != CampaignStatus.DRAFT) {
            throw new IllegalStateException("Cannot approve campaign in status: " + status);
        }
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        this.status = CampaignStatus.PLANNED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Campaign{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", type=" + campaignType +
                ", status=" + status +
                ", promotions=" + promotionIds.size() +
                '}';
    }

    /**
     * Campaign type enum.
     */
    public enum CampaignType {
        SEASONAL("Seasonal"),
        PRODUCT_LAUNCH("Product Launch"),
        BRAND_AWARENESS("Brand Awareness"),
        LOYALTY("Loyalty"),
        RETENTION("Retention"),
        ACQUISITION("Acquisition"),
        CROSS_SELL("Cross-Sell"),
        UPSELL("Upsell"),
        REFERRAL("Referral");

        private final String displayName;

        CampaignType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Campaign status enum.
     */
    public enum CampaignStatus {
        DRAFT("Draft"),
        PLANNED("Planned"),
        ACTIVE("Active"),
        PAUSED("Paused"),
        ENDED("Ended"),
        CANCELLED("Cancelled");

        private final String displayName;

        CampaignStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isActive() {
            return this == ACTIVE || this == PLANNED;
        }

        public boolean isTerminal() {
            return this == ENDED || this == CANCELLED;
        }
    }

    /**
     * Campaign metrics record.
     */
    public record CampaignMetrics(
            int impressions,
            int clicks,
            int conversions,
            int revenue,
            int cost
    ) {
        public double getClickThroughRate() {
            if (impressions == 0) {
                return 0.0;
            }
            return (double) clicks / impressions * 100.0;
        }
    }
}
```

## 2. Promotion Application Module

**`/modules/promotion/application/pom.xml`**:

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

    <artifactId>erp-promotion-application</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-promotion-domain</artifactId>
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

**`/modules/promotion/application/src/main/java/tech/kayys/erp/promotion/application/api/PromotionService.java`**:

```java
package tech.kayys.erp.promotion.application.api;

import tech.kayys.erp.promotion.application.api.command.*;
import tech.kayys.erp.promotion.application.api.query.PromotionView;
import tech.kayys.erp.promotion.application.api.query.PromotionValidationResult;
import tech.kayys.erp.promotion.domain.identifier.PromotionId;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Public API for promotion operations.
 */
public interface PromotionService {

    // ============ Promotion Commands ============

    /**
     * Creates a new promotion.
     */
    CompletionStage<PromotionId> createPromotion(CreatePromotionCommand command);

    /**
     * Activates a promotion.
     */
    CompletionStage<PromotionId> activatePromotion(ActivatePromotionCommand command);

    /**
     * Pauses a promotion.
     */
    CompletionStage<PromotionId> pausePromotion(PausePromotionCommand command);

    /**
     * Schedules a promotion.
     */
    CompletionStage<PromotionId> schedulePromotion(SchedulePromotionCommand command);

    /**
     * Completes a promotion.
     */
    CompletionStage<PromotionId> completePromotion(CompletePromotionCommand command);

    /**
     * Cancels a promotion.
     */
    CompletionStage<PromotionId> cancelPromotion(CancelPromotionCommand command);

    /**
     * Redeems a promotion.
     */
    CompletionStage<PromotionId> redeemPromotion(RedeemPromotionCommand command);

    // ============ Promotion Queries ============

    /**
     * Validates if a promotion is applicable for a customer.
     */
    CompletionStage<PromotionValidationResult> validatePromotion(
        String promoCode, UUID customerId, String orderAmount
    );

    /**
     * Gets active promotions for a customer.
     */
    CompletionStage<List<PromotionView>> getActivePromotions(UUID customerId);

    /**
     * Gets promotion by ID.
     */
    CompletionStage<PromotionView> getPromotion(PromotionId promotionId);

    /**
     * Gets all applicable promotions for an order.
     */
    CompletionStage<List<PromotionView>> getApplicablePromotions(
        UUID customerId, List<UUID> productIds, String orderAmount
    );

    // ============ Campaign Commands ============

    /**
     * Creates a new marketing campaign.
     */
    CompletionStage<CampaignId> createCampaign(CreateCampaignCommand command);

    /**
     * Launches a campaign.
     */
    CompletionStage<CampaignId> launchCampaign(LaunchCampaignCommand command);

    /**
     * Ends a campaign.
     */
    CompletionStage<CampaignId> endCampaign(EndCampaignCommand command);

    /**
     * Records campaign metrics.
     */
    CompletionStage<CampaignId> recordCampaignMetrics(RecordCampaignMetricsCommand command);
}
```

**`/modules/promotion/application/src/main/java/tech/kayys/erp/promotion/application/api/command/CreatePromotionCommand.java`**:

```java
package tech.kayys.erp.promotion.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.promotion.domain.identifier.PromotionId;
import tech.kayys.erp.promotion.domain.valueobject.PromotionType;
import tech.kayys.erp.promotion.domain.valueobject.TargetAudience;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Command to create a new promotion.
 */
public record CreatePromotionCommand(
        PromotionId promotionId,
        String name,
        String description,
        String promoCode,
        PromotionType promotionType,
        String discountValue,
        String currencyCode,
        String minimumOrderAmount,
        String maximumDiscountAmount,
        TargetAudience targetAudience,
        List<UUID> applicableProductIds,
        List<String> applicableCategories,
        Instant startDate,
        Instant endDate,
        int usageLimitPerCustomer,
        int totalUsageLimit,
        boolean stackable,
        int priority,
        boolean requiresCoupon,
        String termsAndConditions,
        String createdBy
) implements Command<PromotionId> {

    public CreatePromotionCommand {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Promotion name cannot be empty");
        }
        if (promotionType == null) {
            throw new IllegalArgumentException("Promotion type is required");
        }
        if (discountValue == null || discountValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Discount value is required");
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code is required");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date is required");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PromotionId promotionId;
        private String name;
        private String description;
        private String promoCode;
        private PromotionType promotionType;
        private String discountValue;
        private String currencyCode = "USD";
        private String minimumOrderAmount;
        private String maximumDiscountAmount;
        private TargetAudience targetAudience = TargetAudience.ALL_CUSTOMERS;
        private List<UUID> applicableProductIds;
        private List<String> applicableCategories;
        private Instant startDate;
        private Instant endDate;
        private int usageLimitPerCustomer = 1;
        private int totalUsageLimit = Integer.MAX_VALUE;
        private boolean stackable = false;
        private int priority = 0;
        private boolean requiresCoupon = false;
        private String termsAndConditions;
        private String createdBy;

        public Builder promotionId(PromotionId promotionId) {
            this.promotionId = promotionId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder promoCode(String promoCode) {
            this.promoCode = promoCode;
            return this;
        }

        public Builder promotionType(PromotionType promotionType) {
            this.promotionType = promotionType;
            return this;
        }

        public Builder discountValue(String discountValue) {
            this.discountValue = discountValue;
            return this;
        }

        public Builder currencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder minimumOrderAmount(String minimumOrderAmount) {
            this.minimumOrderAmount = minimumOrderAmount;
            return this;
        }

        public Builder maximumDiscountAmount(String maximumDiscountAmount) {
            this.maximumDiscountAmount = maximumDiscountAmount;
            return this;
        }

        public Builder targetAudience(TargetAudience targetAudience) {
            this.targetAudience = targetAudience;
            return this;
        }

        public Builder applicableProductIds(List<UUID> applicableProductIds) {
            this.applicableProductIds = applicableProductIds;
            return this;
        }

        public Builder applicableCategories(List<String> applicableCategories) {
            this.applicableCategories = applicableCategories;
            return this;
        }

        public Builder startDate(Instant startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(Instant endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder usageLimitPerCustomer(int usageLimitPerCustomer) {
            this.usageLimitPerCustomer = usageLimitPerCustomer;
            return this;
        }

        public Builder totalUsageLimit(int totalUsageLimit) {
            this.totalUsageLimit = totalUsageLimit;
            return this;
        }

        public Builder stackable(boolean stackable) {
            this.stackable = stackable;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder requiresCoupon(boolean requiresCoupon) {
            this.requiresCoupon = requiresCoupon;
            return this;
        }

        public Builder termsAndConditions(String termsAndConditions) {
            this.termsAndConditions = termsAndConditions;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public CreatePromotionCommand build() {
            if (promotionId == null) {
                promotionId = PromotionId.generate();
            }
            if (startDate == null) {
                startDate = Instant.now();
            }
            if (endDate == null) {
                endDate = startDate.plusSeconds(30L * 24L * 60L * 60L); // 30 days
            }
            return new CreatePromotionCommand(
                promotionId, name, description, promoCode, promotionType,
                discountValue, currencyCode, minimumOrderAmount, maximumDiscountAmount,
                targetAudience, applicableProductIds, applicableCategories,
                startDate, endDate, usageLimitPerCustomer, totalUsageLimit,
                stackable, priority, requiresCoupon, termsAndConditions, createdBy
            );
        }
    }
}
```

**`/modules/promotion/application/src/main/java/tech/kayys/erp/promotion/application/api/command/RedeemPromotionCommand.java`**:

```java
package tech.kayys.erp.promotion.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.promotion.domain.identifier.PromotionId;

import java.util.UUID;

/**
 * Command to redeem a promotion.
 */
public record RedeemPromotionCommand(
        PromotionId promotionId,
        UUID customerId,
        UUID orderId,
        String orderAmount,
        String currencyCode
) implements Command<PromotionId> {

    public RedeemPromotionCommand {
        if (promotionId == null) {
            throw new IllegalArgumentException("Promotion ID cannot be null");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (orderAmount == null || orderAmount.trim().isEmpty()) {
            throw new IllegalArgumentException("Order amount is required");
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PromotionId promotionId;
        private UUID customerId;
        private UUID orderId;
        private String orderAmount;
        private String currencyCode = "USD";

        public Builder promotionId(PromotionId promotionId) {
            this.promotionId = promotionId;
            return this;
        }

        public Builder customerId(UUID customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder orderId(UUID orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder orderAmount(String orderAmount) {
            this.orderAmount = orderAmount;
            return this;
        }

        public Builder currencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public RedeemPromotionCommand build() {
            return new RedeemPromotionCommand(
                promotionId, customerId, orderId, orderAmount, currencyCode
            );
        }
    }
}
```

**`/modules/promotion/application/src/main/java/tech/kayys/erp/promotion/application/api/query/PromotionView.java`**:

```java
package tech.kayys.erp.promotion.application.api.query;

import tech.kayys.erp.promotion.domain.model.Promotion;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * View of a promotion.
 */
public record PromotionView(
        String promotionId,
        String name,
        String description,
        String promoCode,
        String promotionType,
        String status,
        String discountValue,
        String discountAmount,
        String currencyCode,
        String minimumOrderAmount,
        String maximumDiscountAmount,
        String targetAudience,
        List<String> applicableProducts,
        List<String> applicableCategories,
        String startDate,
        String endDate,
        int usageLimitPerCustomer,
        int totalUsageLimit,
        int currentUsageCount,
        int remainingUsage,
        double usagePercentage,
        boolean stackable,
        int priority,
        boolean requiresCoupon,
        boolean active,
        boolean isValid
) {

    public static PromotionView fromDomain(Promotion promotion) {
        return new PromotionView(
            promotion.getId().toString(),
            promotion.getName(),
            promotion.getDescription(),
            promotion.getPromoCode(),
            promotion.getPromotionType().name(),
            promotion.getStatus().name(),
            promotion.getDiscountValue().toPlainString(),
            promotion.getDiscountAmount() != null ? 
                promotion.getDiscountAmount().getAmount().toPlainString() : null,
            promotion.getCurrencyCode(),
            promotion.getMinimumOrderAmount() != null ?
                promotion.getMinimumOrderAmount().getAmount().toPlainString() : null,
            promotion.getMaximumDiscountAmount() != null ?
                promotion.getMaximumDiscountAmount().getAmount().toPlainString() : null,
            promotion.getTargetAudience() != null ?
                promotion.getTargetAudience().name() : null,
            promotion.getApplicableProductIds().stream()
                .map(UUID::toString)
                .collect(Collectors.toList()),
            promotion.getApplicableCategories(),
            promotion.getStartDate().toString(),
            promotion.getEndDate().toString(),
            promotion.getUsageLimitPerCustomer(),
            promotion.getTotalUsageLimit(),
            promotion.getCurrentUsageCount(),
            promotion.getRemainingUsage(),
            promotion.getUsagePercentage(),
            promotion.isStackable(),
            promotion.getPriority(),
            promotion.isRequiresCoupon(),
            promotion.isActive(),
            promotion.isValid()
        );
    }
}
```

**`/modules/promotion/application/src/main/java/tech/kayys/erp/promotion/application/internal/CreatePromotionHandler.java`**:

```java
package tech.kayys.erp.promotion.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.promotion.application.api.command.CreatePromotionCommand;
import tech.kayys.erp.promotion.domain.identifier.PromotionId;
import tech.kayys.erp.promotion.domain.model.Promotion;
import tech.kayys.erp.promotion.domain.repository.PromotionRepository;
import tech.kayys.erp.promotion.domain.valueobject.Money;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for creating promotions.
 */
@UseCase("Create a new promotion")
public class CreatePromotionHandler implements CommandHandler<CreatePromotionCommand, PromotionId> {

    private final PromotionRepository promotionRepository;

    @Inject
    public CreatePromotionHandler(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public CompletionStage<PromotionId> handle(CreatePromotionCommand command) {
        // Check if promo code is unique
        if (command.promoCode() != null && !command.promoCode().trim().isEmpty()) {
            return promotionRepository.findByPromoCode(command.promoCode())
                .thenCompose(existing -> {
                    if (existing != null) {
                        return CompletableFuture.failedFuture(
                            new IllegalArgumentException("Promo code already exists: " + command.promoCode())
                        );
                    }
                    return createPromotion(command);
                });
        }
        return createPromotion(command);
    }

    private CompletionStage<PromotionId> createPromotion(CreatePromotionCommand command) {
        // Create the promotion
        Promotion promotion = Promotion.create(
            command.promotionId(),
            command.name(),
            command.promotionType(),
            new BigDecimal(command.discountValue()),
            command.currencyCode(),
            command.startDate(),
            command.endDate()
        );

        // Set optional fields
        if (command.description() != null) {
            promotion.setDescription(command.description());
        }
        if (command.promoCode() != null) {
            promotion.setPromoCode(command.promoCode());
        }
        if (command.minimumOrderAmount() != null) {
            promotion.setMinimumOrderAmount(
                Money.of(command.minimumOrderAmount(), command.currencyCode())
            );
        }
        if (command.maximumDiscountAmount() != null) {
            promotion.setMaximumDiscountAmount(
                Money.of(command.maximumDiscountAmount(), command.currencyCode())
            );
        }
        if (command.targetAudience() != null) {
            promotion.setTargetAudience(command.targetAudience());
        }
        if (command.applicableProductIds() != null) {
            promotion.setApplicableProductIds(command.applicableProductIds());
        }
        if (command.applicableCategories() != null) {
            promotion.setApplicableCategories(command.applicableCategories());
        }
        
        promotion.setUsageLimitPerCustomer(command.usageLimitPerCustomer());
        promotion.setTotalUsageLimit(command.totalUsageLimit());
        promotion.setStackable(command.stackable());
        promotion.setPriority(command.priority());
        promotion.setRequiresCoupon(command.requiresCoupon());
        
        if (command.termsAndConditions() != null) {
            promotion.setTermsAndConditions(command.termsAndConditions());
        }
        if (command.createdBy() != null) {
            promotion.setCreatedBy(command.createdBy());
        }

        // If start date is in the future, schedule it
        if (command.startDate().isAfter(Instant.now())) {
            promotion.schedule();
        }

        // Save the promotion
        return promotionRepository.save(promotion)
            .thenApply(Promotion::getId);
    }
}
```

**`/modules/promotion/application/src/main/java/tech/kayys/erp/promotion/application/port/CustomerSegmentPort.java`**:

```java
package tech.kayys.erp.promotion.application.port;

import tech.kayys.erp.promotion.domain.valueobject.TargetAudience;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Port for customer segmentation information.
 */
public interface CustomerSegmentPort {

    /**
     * Gets the customer segment for a customer.
     */
    CompletionStage<TargetAudience.CustomerSegment> getCustomerSegment(UUID customerId);

    /**
     * Checks if a customer is eligible for a target audience.
     */
    default CompletionStage<Boolean> isCustomerEligible(UUID customerId, TargetAudience audience) {
        return getCustomerSegment(customerId)
            .thenApply(segment -> audience.matchesCustomer(segment));
    }

    /**
     * Gets customer loyalty tier.
     */
    CompletionStage<LoyaltyTier> getCustomerLoyaltyTier(UUID customerId);

    record LoyaltyTier(
        String tier,
        int points,
        double discountRate
    ) {}
}
```

**`/modules/promotion/application/src/main/java/tech/kayys/erp/promotion/application/port/ProductCatalogPort.java`**:

```java
package tech.kayys.erp.promotion.application.port;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Port for product catalog information.
 */
public interface ProductCatalogPort {

    /**
     * Gets product details.
     */
    CompletionStage<ProductDetails> getProductDetails(UUID productId);

    /**
     * Gets product categories.
     */
    CompletionStage<List<String>> getProductCategories(UUID productId);

    /**
     * Checks if products belong to a category.
     */
    CompletionStage<Boolean> productsInCategory(List<UUID> productIds, String category);

    record ProductDetails(
        UUID productId,
        String name,
        String sku,
        String price,
        String currencyCode,
        List<String> categories,
        boolean active
    ) {}
}
```

## 3. Update Root POM

**Update `/pom.xml`** to include Promotion modules:

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

    <module>modules/promotion/domain</module>
    <module>modules/promotion/application</module>
    <module>modules/promotion/infrastructure</module>
    <module>modules/promotion/interfaces</module>
</modules>
```

## Summary

The complete Promotion/Marketing bounded context provides:

1. **Promotion Management**:
   - Multiple promotion types (percentage, fixed, BOGO, free shipping, etc.)
   - Full lifecycle management (Draft → Scheduled → Active → Completed/Cancelled)
   - Usage tracking and limits
   - Stackable promotions with priority

2. **Targeting and Segmentation**:
   - Target audience definitions
   - Product and category applicability
   - Customer segment matching
   - Loyalty program integration

3. **Campaign Management**:
   - Multi-promotion campaigns
   - Budget and spend tracking
   - ROI calculation
   - Channel management
   - Performance metrics (impressions, clicks, conversions)

4. **Redemption Processing**:
   - Validation against rules
   - Customer usage limits
   - Minimum order amounts
   - Maximum discount limits
   - Redemption tracking

5. **Integration Ports**:
   - CustomerSegmentPort for segmentation
   - ProductCatalogPort for product data
   - Pricing context integration

6. **Architecture Rules**:
   - Clean domain model
   - Proper value objects
   - Event-driven design
   - Context isolation

This completes the Promotion context with comprehensive marketing and promotional capabilities that integrate with Sales, Pricing, and CRM contexts.
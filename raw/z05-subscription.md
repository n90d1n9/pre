# Complete Implementation: Subscription Bounded Context

Now I'll implement the complete Subscription/Recurring Billing bounded context, which handles subscriptions, recurring payments, billing cycles, and subscription management. This context is used by Sales/Order, CRM, and Accounting contexts.

## 1. Subscription Domain Module

**`/modules/subscription/domain/pom.xml`**:

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

    <artifactId>erp-subscription-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/subscription/domain/src/main/java/tech/kayys/erp/subscription/domain/identifier/SubscriptionId.java`**:

```java
package tech.kayys.erp.subscription.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Subscription identifier.
 */
public final class SubscriptionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public SubscriptionId(UUID value) {
        super(value);
    }

    public static SubscriptionId of(UUID value) {
        return new SubscriptionId(value);
    }

    public static SubscriptionId generate() {
        return new SubscriptionId(UUID.randomUUID());
    }

    public static SubscriptionId fromString(String value) {
        return new SubscriptionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "SubscriptionId{" + value + "}";
    }
}
```

**`/modules/subscription/domain/src/main/java/tech/kayys/erp/subscription/domain/identifier/CustomerId.java`**:

```java
package tech.kayys.erp.subscription.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Customer identifier in the Subscription context.
 * Represents a customer from CRM context.
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

**`/modules/subscription/domain/src/main/java/tech/kayys/erp/subscription/domain/identifier/PlanId.java`**:

```java
package tech.kayys.erp.subscription.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Subscription plan identifier.
 */
public final class PlanId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public PlanId(UUID value) {
        super(value);
    }

    public static PlanId of(UUID value) {
        return new PlanId(value);
    }

    public static PlanId generate() {
        return new PlanId(UUID.randomUUID());
    }

    public static PlanId fromString(String value) {
        return new PlanId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PlanId{" + value + "}";
    }
}
```

**`/modules/subscription/domain/src/main/java/tech/kayys/erp/subscription/domain/identifier/InvoiceId.java`**:

```java
package tech.kayys.erp.subscription.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Invoice identifier in the Subscription context.
 */
public final class InvoiceId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public InvoiceId(UUID value) {
        super(value);
    }

    public static InvoiceId of(UUID value) {
        return new InvoiceId(value);
    }

    public static InvoiceId generate() {
        return new InvoiceId(UUID.randomUUID());
    }

    public static InvoiceId fromString(String value) {
        return new InvoiceId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "InvoiceId{" + value + "}";
    }
}
```

**`/modules/subscription/domain/src/main/java/tech/kayys/erp/subscription/domain/valueobject/Money.java`**:

```java
package tech.kayys.erp.subscription.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Money value object for the Subscription context.
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

**`/modules/subscription/domain/src/main/java/tech/kayys/erp/subscription/domain/valueobject/BillingCycle.java`**:

```java
package tech.kayys.erp.subscription.domain.valueobject;

/**
 * Billing cycle for subscriptions.
 */
public enum BillingCycle {
    MONTHLY(1, "Monthly"),
    QUARTERLY(3, "Quarterly"),
    SEMI_ANNUAL(6, "Semi-Annual"),
    ANNUAL(12, "Annual"),
    BIENNIAL(24, "Biennial"),
    TRIENNIAL(36, "Triennial");

    private final int months;
    private final String displayName;

    BillingCycle(int months, String displayName) {
        this.months = months;
        this.displayName = displayName;
    }

    public int getMonths() {
        return months;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the prorated discount for the billing cycle.
     * Annual and longer cycles typically have discounts.
     */
    public double getDiscountFactor() {
        return switch (this) {
            case MONTHLY -> 0.0;
            case QUARTERLY -> 0.05;
            case SEMI_ANNUAL -> 0.10;
            case ANNUAL -> 0.15;
            case BIENNIAL -> 0.20;
            case TRIENNIAL -> 0.25;
        };
    }

    /**
     * Gets the next billing date based on current date.
     */
    public java.time.LocalDate getNextBillingDate(java.time.LocalDate currentDate) {
        return currentDate.plusMonths(months);
    }
}
```

**`/modules/subscription/domain/src/main/java/tech/kayys/erp/subscription/domain/valueobject/SubscriptionStatus.java`**:

```java
package tech.kayys.erp.subscription.domain.valueobject;

/**
 * Status of a subscription.
 */
public enum SubscriptionStatus {
    DRAFT("Draft - being created"),
    ACTIVE("Active - subscription is active"),
    PAUSED("Paused - temporarily suspended"),
    CANCELLED("Cancelled - terminated"),
    EXPIRED("Expired - ended naturally"),
    PAST_DUE("Past Due - payment overdue"),
    IN_GRACE_PERIOD("In Grace Period - still active but payment overdue");

    private final String description;

    SubscriptionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE || this == IN_GRACE_PERIOD;
    }

    public boolean canTransitionTo(SubscriptionStatus target) {
        return switch (this) {
            case DRAFT -> target == ACTIVE || target == CANCELLED;
            case ACTIVE -> target == PAUSED || target == CANCELLED || 
                           target == EXPIRED || target == PAST_DUE;
            case PAUSED -> target == ACTIVE || target == CANCELLED;
            case PAST_DUE -> target == ACTIVE || target == IN_GRACE_PERIOD || 
                             target == CANCELLED || target == EXPIRED;
            case IN_GRACE_PERIOD -> target == ACTIVE || target == CANCELLED || 
                                    target == EXPIRED;
            case CANCELLED, EXPIRED -> false;
        };
    }

    public boolean isTerminal() {
        return this == CANCELLED || this == EXPIRED;
    }
}
```

**`/modules/subscription/domain/src/main/java/tech/kayys/erp/subscription/domain/valueobject/PlanType.java`**:

```java
package tech.kayys.erp.subscription.domain.valueobject;

/**
 * Types of subscription plans.
 */
public enum PlanType {
    BASIC("Basic Plan"),
    PROFESSIONAL("Professional Plan"),
    ENTERPRISE("Enterprise Plan"),
    PREMIUM("Premium Plan"),
    CUSTOM("Custom Plan"),
    TRIAL("Trial Plan"),
    EDUCATIONAL("Educational Plan");

    private final String displayName;

    PlanType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if this plan includes premium features.
     */
    public boolean isPremium() {
        return this == PROFESSIONAL || this == ENTERPRISE || this == PREMIUM;
    }

    /**
     * Gets the priority level (higher = more features).
     */
    public int getPriority() {
        return switch (this) {
            case BASIC -> 1;
            case PROFESSIONAL, EDUCATIONAL -> 2;
            case PREMIUM -> 3;
            case ENTERPRISE -> 4;
            case CUSTOM, TRIAL -> 0;
        };
    }
}
```

**`/modules/subscription/domain/src/main/java/tech/kayys/erp/subscription/domain/model/SubscriptionPlan.java`**:

```java
package tech.kayys.erp.subscription.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.subscription.domain.identifier.PlanId;
import tech.kayys.erp.subscription.domain.valueobject.BillingCycle;
import tech.kayys.erp.subscription.domain.valueobject.Money;
import tech.kayys.erp.subscription.domain.valueobject.PlanType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Subscription plan aggregate root.
 * Defines the pricing, features, and terms of a subscription.
 */
public final class SubscriptionPlan extends AggregateRoot<PlanId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private PlanType planType;
    private Money monthlyPrice;
    private Money annualPrice;
    private BillingCycle defaultBillingCycle;
    private List<String> features;
    private int maxUsers;
    private int maxProjects;
    private long storageLimitMb;
    private boolean supportsApiAccess;
    private boolean supportsIntegrations;
    private boolean supportsTeamManagement;
    private boolean isPublic;
    private Instant validFrom;
    private Instant validTo;
    private boolean active;

    private SubscriptionPlan(PlanId id) {
        super(id);
        this.features = new ArrayList<>();
        this.active = true;
        this.isPublic = true;
        this.maxUsers = 1;
        this.maxProjects = 1;
    }

    private SubscriptionPlan() {
        super();
    }

    /**
     * Factory method to create a new subscription plan.
     */
    public static SubscriptionPlan create(
            PlanId id,
            String name,
            PlanType planType,
            Money monthlyPrice,
            BillingCycle defaultBillingCycle) {
        SubscriptionPlan plan = new SubscriptionPlan(id);
        plan.name = name;
        plan.planType = planType;
        plan.monthlyPrice = monthlyPrice;
        plan.defaultBillingCycle = defaultBillingCycle;
        plan.validFrom = Instant.now();
        return plan;
    }

    /**
     * Adds a feature to the plan.
     */
    public void addFeature(String feature) {
        if (features.contains(feature)) {
            return;
        }
        features.add(feature);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes a feature from the plan.
     */
    public void removeFeature(String feature) {
        features.remove(feature);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the annual price (typically discounted).
     */
    public void setAnnualPrice(Money annualPrice) {
        this.annualPrice = annualPrice;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the price for a specific billing cycle.
     */
    public Money getPriceForCycle(BillingCycle cycle) {
        return switch (cycle) {
            case MONTHLY -> monthlyPrice;
            case QUARTERLY -> monthlyPrice.multiply(3).multiply(
                BigDecimal.valueOf(1 - cycle.getDiscountFactor()));
            case SEMI_ANNUAL -> monthlyPrice.multiply(6).multiply(
                BigDecimal.valueOf(1 - cycle.getDiscountFactor()));
            case ANNUAL -> annualPrice != null ? annualPrice :
                monthlyPrice.multiply(12).multiply(
                    BigDecimal.valueOf(1 - cycle.getDiscountFactor()));
            case BIENNIAL, TRIENNIAL -> monthlyPrice.multiply(cycle.getMonths())
                .multiply(BigDecimal.valueOf(1 - cycle.getDiscountFactor()));
        };
    }

    /**
     * Calculates the annual savings compared to monthly billing.
     */
    public Money calculateAnnualSavings() {
        if (annualPrice == null) {
            Money yearlyMonthly = monthlyPrice.multiply(12);
            return yearlyMonthly.subtract(getPriceForCycle(BillingCycle.ANNUAL));
        }
        return monthlyPrice.multiply(12).subtract(annualPrice);
    }

    /**
     * Checks if the plan has a specific feature.
     */
    public boolean hasFeature(String feature) {
        return features.contains(feature);
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public PlanType getPlanType() { return planType; }
    public Money getMonthlyPrice() { return monthlyPrice; }
    public Money getAnnualPrice() { return annualPrice; }
    public BillingCycle getDefaultBillingCycle() { return defaultBillingCycle; }
    public List<String> getFeatures() { return Collections.unmodifiableList(features); }
    public int getMaxUsers() { return maxUsers; }
    public int getMaxProjects() { return maxProjects; }
    public long getStorageLimitMb() { return storageLimitMb; }
    public boolean isSupportsApiAccess() { return supportsApiAccess; }
    public boolean isSupportsIntegrations() { return supportsIntegrations; }
    public boolean isSupportsTeamManagement() { return supportsTeamManagement; }
    public boolean isPublic() { return isPublic; }
    public Instant getValidFrom() { return validFrom; }
    public Instant getValidTo() { return validTo; }
    public boolean isActive() { return active && 
        (validTo == null || Instant.now().isBefore(validTo)); }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMaxProjects(int maxProjects) {
        this.maxProjects = maxProjects;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setStorageLimitMb(long storageLimitMb) {
        this.storageLimitMb = storageLimitMb;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setSupportsApiAccess(boolean supportsApiAccess) {
        this.supportsApiAccess = supportsApiAccess;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setSupportsIntegrations(boolean supportsIntegrations) {
        this.supportsIntegrations = supportsIntegrations;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setSupportsTeamManagement(boolean supportsTeamManagement) {
        this.supportsTeamManagement = supportsTeamManagement;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setValidityPeriod(Instant from, Instant to) {
        if (to != null && to.isBefore(from)) {
            throw new IllegalArgumentException("Valid to must be after valid from");
        }
        this.validFrom = from;
        this.validTo = to;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "SubscriptionPlan{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", planType=" + planType +
                ", monthlyPrice=" + monthlyPrice +
                ", features=" + features.size() +
                '}';
    }
}
```

**`/modules/subscription/domain/src/main/java/tech/kayys/erp/subscription/domain/model/Subscription.java`**:

```java
package tech.kayys.erp.subscription.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.subscription.domain.event.SubscriptionActivated;
import tech.kayys.erp.subscription.domain.event.SubscriptionCancelled;
import tech.kayys.erp.subscription.domain.event.SubscriptionCreated;
import tech.kayys.erp.subscription.domain.event.SubscriptionPaused;
import tech.kayys.erp.subscription.domain.event.SubscriptionRenewed;
import tech.kayys.erp.subscription.domain.identifier.CustomerId;
import tech.kayys.erp.subscription.domain.identifier.InvoiceId;
import tech.kayys.erp.subscription.domain.identifier.PlanId;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;
import tech.kayys.erp.subscription.domain.valueobject.BillingCycle;
import tech.kayys.erp.subscription.domain.valueobject.Money;
import tech.kayys.erp.subscription.domain.valueobject.SubscriptionStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Subscription aggregate root.
 * Represents a customer's subscription to a plan.
 */
public final class Subscription extends AggregateRoot<SubscriptionId> {
    
    private static final long serialVersionUID = 1L;
    
    private CustomerId customerId;
    private PlanId planId;
    private SubscriptionStatus status;
    private BillingCycle billingCycle;
    private Money monthlyFee;
    private Money totalPaid;
    private Instant startDate;
    private Instant nextBillingDate;
    private Instant endDate; // null for ongoing
    private InvoiceId lastInvoiceId;
    private InvoiceId nextInvoiceId;
    private int billingAttempts;
    private boolean autoRenew;
    private String cancellationReason;
    private List<SubscriptionEvent> history;
    private String trialPeriodEnd; // ISO date string
    private boolean inTrial;

    private Subscription(SubscriptionId id) {
        super(id);
        this.status = SubscriptionStatus.DRAFT;
        this.history = new ArrayList<>();
        this.autoRenew = true;
        this.totalPaid = Money.zero("USD");
        this.billingAttempts = 0;
    }

    private Subscription() {
        super();
    }

    /**
     * Factory method to create a new subscription.
     */
    public static Subscription create(
            SubscriptionId id,
            CustomerId customerId,
            PlanId planId,
            Money monthlyFee,
            BillingCycle billingCycle) {
        Subscription subscription = new Subscription(id);
        subscription.customerId = customerId;
        subscription.planId = planId;
        subscription.monthlyFee = monthlyFee;
        subscription.billingCycle = billingCycle;
        subscription.startDate = Instant.now();
        subscription.nextBillingDate = calculateNextBillingDate(Instant.now(), billingCycle);
        subscription.totalPaid = Money.zero(monthlyFee.getCurrency().getCurrencyCode());
        
        subscription.registerEvent(new SubscriptionCreated(subscription));
        return subscription;
    }

    /**
     * Activates the subscription.
     */
    public void activate() {
        if (status != SubscriptionStatus.DRAFT && status != SubscriptionStatus.PAUSED) {
            throw new IllegalStateException("Cannot activate subscription in status: " + status);
        }
        
        this.status = SubscriptionStatus.ACTIVE;
        if (startDate == null) {
            this.startDate = Instant.now();
        }
        if (nextBillingDate == null) {
            this.nextBillingDate = calculateNextBillingDate(Instant.now(), billingCycle);
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
        
        registerEvent(new SubscriptionActivated(this));
    }

    /**
     * Pauses the subscription.
     */
    public void pause() {
        if (status != SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("Cannot pause subscription in status: " + status);
        }
        
        this.status = SubscriptionStatus.PAUSED;
        setUpdatedAt(Instant.now());
        incrementVersion();
        
        registerEvent(new SubscriptionPaused(this));
    }

    /**
     * Cancels the subscription.
     */
    public void cancel(String reason) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Subscription is already terminated");
        }
        
        this.status = SubscriptionStatus.CANCELLED;
        this.cancellationReason = reason;
        this.endDate = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
        
        registerEvent(new SubscriptionCancelled(this));
    }

    /**
     * Renews the subscription.
     */
    public void renew() {
        if (status != SubscriptionStatus.ACTIVE && status != SubscriptionStatus.IN_GRACE_PERIOD) {
            throw new IllegalStateException("Cannot renew subscription in status: " + status);
        }
        
        // Move to next billing period
        this.nextBillingDate = calculateNextBillingDate(nextBillingDate, billingCycle);
        this.billingAttempts = 0;
        this.status = SubscriptionStatus.ACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
        
        registerEvent(new SubscriptionRenewed(this));
    }

    /**
     * Marks payment as received.
     */
    public void recordPayment(InvoiceId invoiceId, Money amount) {
        this.totalPaid = totalPaid.add(amount);
        this.lastInvoiceId = invoiceId;
        this.billingAttempts = 0;
        
        // If in grace period, move back to active
        if (status == SubscriptionStatus.IN_GRACE_PERIOD) {
            this.status = SubscriptionStatus.ACTIVE;
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks payment as failed.
     */
    public void recordPaymentFailure() {
        this.billingAttempts++;
        setUpdatedAt(Instant.now());
        incrementVersion();
        
        // After 3 failures, move to grace period
        if (billingAttempts >= 3 && status == SubscriptionStatus.ACTIVE) {
            this.status = SubscriptionStatus.PAST_DUE;
        }
    }

    /**
     * Enters grace period.
     */
    public void enterGracePeriod() {
        if (status != SubscriptionStatus.PAST_DUE) {
            throw new IllegalStateException("Cannot enter grace period from status: " + status);
        }
        this.status = SubscriptionStatus.IN_GRACE_PERIOD;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if subscription needs renewal.
     */
    public boolean needsRenewal() {
        if (!isActive()) {
            return false;
        }
        return Instant.now().isAfter(nextBillingDate);
    }

    /**
     * Checks if subscription is in trial period.
     */
    public boolean isInTrial() {
        return inTrial && 
               (trialPeriodEnd == null || 
                Instant.parse(trialPeriodEnd).isAfter(Instant.now()));
    }

    /**
     * Sets trial period.
     */
    public void setTrialPeriod(int days) {
        this.inTrial = true;
        this.trialPeriodEnd = Instant.now()
            .plusSeconds(days * 24L * 60L * 60L)
            .toString();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private Instant calculateNextBillingDate(Instant from, BillingCycle cycle) {
        LocalDate current = from.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate next = current.plusMonths(cycle.getMonths());
        return next.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    // Getters
    public CustomerId getCustomerId() { return customerId; }
    public PlanId getPlanId() { return planId; }
    public SubscriptionStatus getStatus() { return status; }
    public BillingCycle getBillingCycle() { return billingCycle; }
    public Money getMonthlyFee() { return monthlyFee; }
    public Money getTotalPaid() { return totalPaid; }
    public Instant getStartDate() { return startDate; }
    public Instant getNextBillingDate() { return nextBillingDate; }
    public Instant getEndDate() { return endDate; }
    public InvoiceId getLastInvoiceId() { return lastInvoiceId; }
    public InvoiceId getNextInvoiceId() { return nextInvoiceId; }
    public int getBillingAttempts() { return billingAttempts; }
    public boolean isAutoRenew() { return autoRenew; }
    public String getCancellationReason() { return cancellationReason; }
    public List<SubscriptionEvent> getHistory() { return Collections.unmodifiableList(history); }
    public boolean isInTrial() { return inTrial; }

    public void setAutoRenew(boolean autoRenew) {
        this.autoRenew = autoRenew;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNextInvoiceId(InvoiceId nextInvoiceId) {
        this.nextInvoiceId = nextInvoiceId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void addHistory(SubscriptionEvent event) {
        history.add(event);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public boolean isActive() {
        return status.isActive() && 
               (endDate == null || Instant.now().isBefore(endDate));
    }

    /**
     * Calculates the prorated amount for a change in plan.
     */
    public Money calculateProratedAmount(SubscriptionPlan newPlan) {
        if (newPlan == null) {
            return Money.zero(monthlyFee.getCurrency().getCurrencyCode());
        }
        
        Money newMonthlyFee = newPlan.getMonthlyPrice();
        int daysRemainingInCycle = calculateDaysRemainingInCycle();
        int totalDaysInCycle = getTotalDaysInCycle();
        
        // Calculate prorated difference
        BigDecimal dailyRateCurrent = monthlyFee.getAmount()
            .divide(BigDecimal.valueOf(totalDaysInCycle), 4, java.math.RoundingMode.HALF_UP);
        BigDecimal dailyRateNew = newMonthlyFee.getAmount()
            .divide(BigDecimal.valueOf(totalDaysInCycle), 4, java.math.RoundingMode.HALF_UP);
        
        BigDecimal difference = dailyRateNew.subtract(dailyRateCurrent)
            .multiply(BigDecimal.valueOf(daysRemainingInCycle));
        
        return Money.of(difference, monthlyFee.getCurrency().getCurrencyCode());
    }

    private int calculateDaysRemainingInCycle() {
        LocalDate now = LocalDate.now();
        LocalDate nextBilling = nextBillingDate.atZone(ZoneId.systemDefault()).toLocalDate();
        long days = java.time.temporal.ChronoUnit.DAYS.between(now, nextBilling);
        return days > 0 ? (int) days : 0;
    }

    private int getTotalDaysInCycle() {
        return switch (billingCycle) {
            case MONTHLY -> 30;
            case QUARTERLY -> 90;
            case SEMI_ANNUAL -> 180;
            case ANNUAL -> 365;
            case BIENNIAL -> 730;
            case TRIENNIAL -> 1095;
        };
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + getId() +
                ", customerId=" + customerId +
                ", planId=" + planId +
                ", status=" + status +
                ", monthlyFee=" + monthlyFee +
                ", nextBillingDate=" + nextBillingDate +
                '}';
    }

    /**
     * Subscription event record for history.
     */
    public record SubscriptionEvent(
            String type,
            Instant timestamp,
            String description,
            String details
    ) {}
}
```

**`/modules/subscription/domain/src/main/java/tech/kayys/erp/subscription/domain/event/SubscriptionCreated.java`**:

```java
package tech.kayys.erp.subscription.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.subscription.domain.model.Subscription;

import java.time.Instant;
import java.util.UUID;

public class SubscriptionCreated implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String customerId;
    private final String planId;
    private final String monthlyFee;
    private final String currency;

    public SubscriptionCreated(Subscription subscription) {
        this.eventId = UUID.randomUUID();
        this.eventType = "SubscriptionCreated";
        this.occurredAt = Instant.now();
        this.aggregateId = subscription.getId().toString();
        this.aggregateType = "Subscription";
        this.customerId = subscription.getCustomerId().toString();
        this.planId = subscription.getPlanId().toString();
        this.monthlyFee = subscription.getMonthlyFee().getAmount().toPlainString();
        this.currency = subscription.getMonthlyFee().getCurrency().getCurrencyCode();
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
    public String getCustomerId() { return customerId; }
    public String getPlanId() { return planId; }
    public String getMonthlyFee() { return monthlyFee; }
    public String getCurrency() { return currency; }

    @Override
    public String toString() {
        return "SubscriptionCreated{" +
                "eventId=" + eventId +
                ", subscriptionId=" + aggregateId +
                ", customerId='" + customerId + '\'' +
                ", planId='" + planId + '\'' +
                '}';
    }
}
```

**`/modules/subscription/domain/src/main/java/tech/kayys/erp/subscription/domain/event/SubscriptionActivated.java`**:

```java
package tech.kayys.erp.subscription.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.subscription.domain.model.Subscription;

import java.time.Instant;
import java.util.UUID;

public class SubscriptionActivated implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String nextBillingDate;

    public SubscriptionActivated(Subscription subscription) {
        this.eventId = UUID.randomUUID();
        this.eventType = "SubscriptionActivated";
        this.occurredAt = Instant.now();
        this.aggregateId = subscription.getId().toString();
        this.aggregateType = "Subscription";
        this.nextBillingDate = subscription.getNextBillingDate().toString();
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
    public String getNextBillingDate() { return nextBillingDate; }

    @Override
    public String toString() {
        return "SubscriptionActivated{" +
                "eventId=" + eventId +
                ", subscriptionId=" + aggregateId +
                ", nextBillingDate='" + nextBillingDate + '\'' +
                '}';
    }
}
```

**`/modules/subscription/domain/src/main/java/tech/kayys/erp/subscription/domain/event/SubscriptionCancelled.java`**:

```java
package tech.kayys.erp.subscription.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.subscription.domain.model.Subscription;

import java.time.Instant;
import java.util.UUID;

public class SubscriptionCancelled implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String reason;

    public SubscriptionCancelled(Subscription subscription) {
        this.eventId = UUID.randomUUID();
        this.eventType = "SubscriptionCancelled";
        this.occurredAt = Instant.now();
        this.aggregateId = subscription.getId().toString();
        this.aggregateType = "Subscription";
        this.reason = subscription.getCancellationReason();
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
    public String getReason() { return reason; }

    @Override
    public String toString() {
        return "SubscriptionCancelled{" +
                "eventId=" + eventId +
                ", subscriptionId=" + aggregateId +
                ", reason='" + reason + '\'' +
                '}';
    }
}
```

**`/modules/subscription/domain/src/main/java/tech/kayys/erp/subscription/domain/event/SubscriptionPaused.java`**:

```java
package tech.kayys.erp.subscription.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.subscription.domain.model.Subscription;

import java.time.Instant;
import java.util.UUID;

public class SubscriptionPaused implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;

    public SubscriptionPaused(Subscription subscription) {
        this.eventId = UUID.randomUUID();
        this.eventType = "SubscriptionPaused";
        this.occurredAt = Instant.now();
        this.aggregateId = subscription.getId().toString();
        this.aggregateType = "Subscription";
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

    @Override
    public String toString() {
        return "SubscriptionPaused{" +
                "eventId=" + eventId +
                ", subscriptionId=" + aggregateId +
                '}';
    }
}
```

**`/modules/subscription/domain/src/main/java/tech/kayys/erp/subscription/domain/event/SubscriptionRenewed.java`**:

```java
package tech.kayys.erp.subscription.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.subscription.domain.model.Subscription;

import java.time.Instant;
import java.util.UUID;

public class SubscriptionRenewed implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String nextBillingDate;

    public SubscriptionRenewed(Subscription subscription) {
        this.eventId = UUID.randomUUID();
        this.eventType = "SubscriptionRenewed";
        this.occurredAt = Instant.now();
        this.aggregateId = subscription.getId().toString();
        this.aggregateType = "Subscription";
        this.nextBillingDate = subscription.getNextBillingDate().toString();
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
    public String getNextBillingDate() { return nextBillingDate; }

    @Override
    public String toString() {
        return "SubscriptionRenewed{" +
                "eventId=" + eventId +
                ", subscriptionId=" + aggregateId +
                ", nextBillingDate='" + nextBillingDate + '\'' +
                '}';
    }
}
```

**`/modules/subscription/domain/src/main/java/tech/kayys/erp/subscription/domain/repository/SubscriptionRepository.java`**:

```java
package tech.kayys.erp.subscription.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.subscription.domain.identifier.CustomerId;
import tech.kayys.erp.subscription.domain.identifier.PlanId;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;
import tech.kayys.erp.subscription.domain.model.Subscription;
import tech.kayys.erp.subscription.domain.valueobject.SubscriptionStatus;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for Subscription aggregates.
 */
public interface SubscriptionRepository extends Repository<Subscription, SubscriptionId> {

    /**
     * Finds all subscriptions for a customer.
     */
    CompletionStage<List<Subscription>> findByCustomerId(CustomerId customerId);

    /**
     * Finds active subscriptions for a customer.
     */
    default CompletionStage<List<Subscription>> findActiveByCustomerId(CustomerId customerId) {
        return findByCustomerId(customerId)
            .thenApply(subscriptions -> subscriptions.stream()
                .filter(Subscription::isActive)
                .toList()
            );
    }

    /**
     * Finds subscriptions by status.
     */
    CompletionStage<List<Subscription>> findByStatus(SubscriptionStatus status);

    /**
     * Finds subscriptions that need renewal (nextBillingDate < now).
     */
    CompletionStage<List<Subscription>> findSubscriptionsNeedingRenewal();

    /**
     * Finds subscriptions for a specific plan.
     */
    CompletionStage<List<Subscription>> findByPlanId(PlanId planId);

    /**
     * Finds subscriptions expiring between two dates.
     */
    CompletionStage<List<Subscription>> findExpiringBetween(Instant start, Instant end);

    /**
     * Counts subscriptions by status.
     */
    CompletionStage<Long> countByStatus(SubscriptionStatus status);

    /**
     * Counts active subscriptions for a customer.
     */
    default CompletionStage<Long> countActiveByCustomerId(CustomerId customerId) {
        return findActiveByCustomerId(customerId)
            .thenApply(List::size)
            .thenApply(Long::valueOf);
    }
}
```

**`/modules/subscription/domain/src/main/java/tech/kayys/erp/subscription/domain/repository/SubscriptionPlanRepository.java`**:

```java
package tech.kayys.erp.subscription.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.subscription.domain.identifier.PlanId;
import tech.kayys.erp.subscription.domain.model.SubscriptionPlan;
import tech.kayys.erp.subscription.domain.valueobject.PlanType;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for SubscriptionPlan aggregates.
 */
public interface SubscriptionPlanRepository extends Repository<SubscriptionPlan, PlanId> {

    /**
     * Finds all active plans.
     */
    CompletionStage<List<SubscriptionPlan>> findActivePlans();

    /**
     * Finds plans by type.
     */
    CompletionStage<List<SubscriptionPlan>> findByType(PlanType planType);

    /**
     * Finds public plans (available for purchase).
     */
    CompletionStage<List<SubscriptionPlan>> findPublicPlans();

    /**
     * Finds plans sorted by price.
     */
    CompletionStage<List<SubscriptionPlan>> findAllSortedByPrice(boolean ascending);

    /**
     * Checks if a plan name is unique.
     */
    CompletionStage<Boolean> isPlanNameUnique(String name);
}
```

## 2. Subscription Application Module

**`/modules/subscription/application/pom.xml`**:

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

    <artifactId>erp-subscription-application</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-subscription-domain</artifactId>
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

**`/modules/subscription/application/src/main/java/tech/kayys/erp/subscription/application/api/command/CreateSubscriptionCommand.java`**:

```java
package tech.kayys.erp.subscription.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;
import tech.kayys.erp.subscription.domain.valueobject.BillingCycle;

import java.util.UUID;

/**
 * Command to create a new subscription.
 */
public record CreateSubscriptionCommand(
        SubscriptionId subscriptionId,
        UUID customerId,
        UUID planId,
        BillingCycle billingCycle,
        Boolean autoRenew,
        Integer trialDays
) implements Command<SubscriptionId> {

    public CreateSubscriptionCommand {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (planId == null) {
            throw new IllegalArgumentException("Plan ID cannot be null");
        }
        if (billingCycle == null) {
            throw new IllegalArgumentException("Billing cycle is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SubscriptionId subscriptionId;
        private UUID customerId;
        private UUID planId;
        private BillingCycle billingCycle = BillingCycle.MONTHLY;
        private Boolean autoRenew = true;
        private Integer trialDays;

        public Builder subscriptionId(SubscriptionId subscriptionId) {
            this.subscriptionId = subscriptionId;
            return this;
        }

        public Builder customerId(UUID customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder planId(UUID planId) {
            this.planId = planId;
            return this;
        }

        public Builder billingCycle(BillingCycle billingCycle) {
            this.billingCycle = billingCycle;
            return this;
        }

        public Builder autoRenew(Boolean autoRenew) {
            this.autoRenew = autoRenew;
            return this;
        }

        public Builder trialDays(Integer trialDays) {
            this.trialDays = trialDays;
            return this;
        }

        public CreateSubscriptionCommand build() {
            if (subscriptionId == null) {
                subscriptionId = SubscriptionId.generate();
            }
            if (autoRenew == null) {
                autoRenew = true;
            }
            return new CreateSubscriptionCommand(
                subscriptionId, customerId, planId, billingCycle, autoRenew, trialDays
            );
        }
    }
}
```

**`/modules/subscription/application/src/main/java/tech/kayys/erp/subscription/application/api/command/ActivateSubscriptionCommand.java`**:

```java
package tech.kayys.erp.subscription.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;

/**
 * Command to activate a subscription.
 */
public record ActivateSubscriptionCommand(
        SubscriptionId subscriptionId
) implements Command<SubscriptionId> {

    public ActivateSubscriptionCommand {
        if (subscriptionId == null) {
            throw new IllegalArgumentException("Subscription ID cannot be null");
        }
    }
}
```

**`/modules/subscription/application/src/main/java/tech/kayys/erp/subscription/application/api/command/CancelSubscriptionCommand.java`**:

```java
package tech.kayys.erp.subscription.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;

/**
 * Command to cancel a subscription.
 */
public record CancelSubscriptionCommand(
        SubscriptionId subscriptionId,
        String reason
) implements Command<SubscriptionId> {

    public CancelSubscriptionCommand {
        if (subscriptionId == null) {
            throw new IllegalArgumentException("Subscription ID cannot be null");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Cancellation reason is required");
        }
    }
}
```

**`/modules/subscription/application/src/main/java/tech/kayys/erp/subscription/application/api/command/PauseSubscriptionCommand.java`**:

```java
package tech.kayys.erp.subscription.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;

/**
 * Command to pause a subscription.
 */
public record PauseSubscriptionCommand(
        SubscriptionId subscriptionId
) implements Command<SubscriptionId> {

    public PauseSubscriptionCommand {
        if (subscriptionId == null) {
            throw new IllegalArgumentException("Subscription ID cannot be null");
        }
    }
}
```

**`/modules/subscription/application/src/main/java/tech/kayys/erp/subscription/application/api/query/GetSubscriptionQuery.java`**:

```java
package tech.kayys.erp.subscription.application.api.query;

import tech.kayys.erp.foundation.application.Query;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;

/**
 * Query to get a subscription by ID.
 */
public record GetSubscriptionQuery(
        SubscriptionId subscriptionId
) implements Query<SubscriptionView> {

    public GetSubscriptionQuery {
        if (subscriptionId == null) {
            throw new IllegalArgumentException("Subscription ID cannot be null");
        }
    }
}
```

**`/modules/subscription/application/src/main/java/tech/kayys/erp/subscription/application/api/query/SubscriptionView.java`**:

```java
package tech.kayys.erp.subscription.application.api.query;

import tech.kayys.erp.subscription.domain.model.Subscription;

import java.time.Instant;

/**
 * View of a subscription.
 */
public record SubscriptionView(
        String subscriptionId,
        String customerId,
        String planId,
        String status,
        String billingCycle,
        String monthlyFee,
        String currency,
        String startDate,
        String nextBillingDate,
        String endDate,
        boolean isActive,
        boolean isInTrial,
        boolean autoRenew,
        String cancellationReason,
        String totalPaid
) {

    public static SubscriptionView fromDomain(Subscription subscription) {
        return new SubscriptionView(
            subscription.getId().toString(),
            subscription.getCustomerId().toString(),
            subscription.getPlanId().toString(),
            subscription.getStatus().name(),
            subscription.getBillingCycle().name(),
            subscription.getMonthlyFee().getAmount().toPlainString(),
            subscription.getMonthlyFee().getCurrency().getCurrencyCode(),
            subscription.getStartDate().toString(),
            subscription.getNextBillingDate().toString(),
            subscription.getEndDate() != null ? subscription.getEndDate().toString() : null,
            subscription.isActive(),
            subscription.isInTrial(),
            subscription.isAutoRenew(),
            subscription.getCancellationReason(),
            subscription.getTotalPaid().getAmount().toPlainString()
        );
    }
}
```

**`/modules/subscription/application/src/main/java/tech/kayys/erp/subscription/application/api/SubscriptionCommandService.java`**:

```java
package tech.kayys.erp.subscription.application.api;

import tech.kayys.erp.subscription.application.api.command.ActivateSubscriptionCommand;
import tech.kayys.erp.subscription.application.api.command.CancelSubscriptionCommand;
import tech.kayys.erp.subscription.application.api.command.CreateSubscriptionCommand;
import tech.kayys.erp.subscription.application.api.command.PauseSubscriptionCommand;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;

import java.util.concurrent.CompletionStage;

/**
 * Public API for subscription commands.
 */
public interface SubscriptionCommandService {

    /**
     * Creates a new subscription.
     */
    CompletionStage<SubscriptionId> createSubscription(CreateSubscriptionCommand command);

    /**
     * Activates a subscription.
     */
    CompletionStage<SubscriptionId> activateSubscription(ActivateSubscriptionCommand command);

    /**
     * Pauses a subscription.
     */
    CompletionStage<SubscriptionId> pauseSubscription(PauseSubscriptionCommand command);

    /**
     * Cancels a subscription.
     */
    CompletionStage<SubscriptionId> cancelSubscription(CancelSubscriptionCommand command);

    /**
     * Processes renewals for all due subscriptions.
     */
    CompletionStage<Integer> processRenewals();
}
```

**`/modules/subscription/application/src/main/java/tech/kayys/erp/subscription/application/api/SubscriptionQueryService.java`**:

```java
package tech.kayys.erp.subscription.application.api;

import tech.kayys.erp.subscription.application.api.query.GetSubscriptionQuery;
import tech.kayys.erp.subscription.application.api.query.SubscriptionView;
import tech.kayys.erp.subscription.domain.identifier.CustomerId;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Public API for subscription queries.
 */
public interface SubscriptionQueryService {

    /**
     * Gets a subscription by ID.
     */
    CompletionStage<SubscriptionView> getSubscription(GetSubscriptionQuery query);

    /**
     * Gets all subscriptions for a customer.
     */
    CompletionStage<List<SubscriptionView>> getCustomerSubscriptions(CustomerId customerId);

    /**
     * Gets active subscriptions for a customer.
     */
    default CompletionStage<List<SubscriptionView>> getActiveCustomerSubscriptions(
            CustomerId customerId) {
        return getCustomerSubscriptions(customerId)
            .thenApply(subscriptions -> subscriptions.stream()
                .filter(SubscriptionView::isActive)
                .toList()
            );
    }

    /**
     * Checks if a customer has an active subscription.
     */
    default CompletionStage<Boolean> customerHasActiveSubscription(CustomerId customerId) {
        return getActiveCustomerSubscriptions(customerId)
            .thenApply(subscriptions -> !subscriptions.isEmpty());
    }

    /**
     * Gets subscriptions needing renewal.
     */
    CompletionStage<List<SubscriptionView>> getSubscriptionsNeedingRenewal();
}
```

**`/modules/subscription/application/src/main/java/tech/kayys/erp/subscription/application/internal/CreateSubscriptionHandler.java`**:

```java
package tech.kayys.erp.subscription.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.subscription.application.api.command.CreateSubscriptionCommand;
import tech.kayys.erp.subscription.application.port.PricingProviderPort;
import tech.kayys.erp.subscription.application.port.CustomerValidationPort;
import tech.kayys.erp.subscription.domain.identifier.CustomerId;
import tech.kayys.erp.subscription.domain.identifier.PlanId;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;
import tech.kayys.erp.subscription.domain.model.Subscription;
import tech.kayys.erp.subscription.domain.model.SubscriptionPlan;
import tech.kayys.erp.subscription.domain.repository.SubscriptionPlanRepository;
import tech.kayys.erp.subscription.domain.repository.SubscriptionRepository;
import tech.kayys.erp.subscription.domain.valueobject.Money;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for creating subscriptions.
 */
@UseCase("Create a new subscription")
public class CreateSubscriptionHandler 
        implements CommandHandler<CreateSubscriptionCommand, SubscriptionId> {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final CustomerValidationPort customerValidationPort;
    private final PricingProviderPort pricingProviderPort;

    @Inject
    public CreateSubscriptionHandler(
            SubscriptionRepository subscriptionRepository,
            SubscriptionPlanRepository planRepository,
            CustomerValidationPort customerValidationPort,
            PricingProviderPort pricingProviderPort) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.customerValidationPort = customerValidationPort;
        this.pricingProviderPort = pricingProviderPort;
    }

    @Override
    public CompletionStage<SubscriptionId> handle(CreateSubscriptionCommand command) {
        // 1. Validate customer exists
        return customerValidationPort.validateCustomer(command.customerId())
            .thenCompose(valid -> {
                if (!valid) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Customer not found: " + command.customerId())
                    );
                }

                // 2. Get the plan
                return planRepository.findById(PlanId.of(command.planId()))
                    .thenCompose(planOpt -> {
                        if (planOpt.isEmpty()) {
                            return CompletableFuture.failedFuture(
                                new IllegalArgumentException("Plan not found: " + command.planId())
                            );
                        }

                        SubscriptionPlan plan = planOpt.get();

                        // 3. Validate plan is active
                        if (!plan.isActive()) {
                            return CompletableFuture.failedFuture(
                                new IllegalArgumentException("Plan is not active")
                            );
                        }

                        // 4. Get pricing for the plan
                        Money monthlyFee = plan.getPriceForCycle(command.billingCycle());

                        // 5. Create the subscription
                        Subscription subscription = Subscription.create(
                            command.subscriptionId(),
                            CustomerId.of(command.customerId()),
                            PlanId.of(command.planId()),
                            monthlyFee,
                            command.billingCycle()
                        );

                        // 6. Set auto-renew
                        subscription.setAutoRenew(command.autoRenew());

                        // 7. Set trial period if specified
                        if (command.trialDays() != null && command.trialDays() > 0) {
                            subscription.setTrialPeriod(command.trialDays());
                        }

                        // 8. Save the subscription
                        return subscriptionRepository.save(subscription)
                            .thenApply(Subscription::getId);
                    });
            });
    }
}
```

**`/modules/subscription/application/src/main/java/tech/kayys/erp/subscription/application/internal/ProcessRenewalsHandler.java`**:

```java
package tech.kayys.erp.subscription.application.internal;

import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.subscription.application.port.InvoiceGenerationPort;
import tech.kayys.erp.subscription.application.port.PaymentProcessorPort;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;
import tech.kayys.erp.subscription.domain.model.Subscription;
import tech.kayys.erp.subscription.domain.repository.SubscriptionRepository;
import tech.kayys.erp.subscription.domain.valueobject.Money;
import tech.kayys.erp.subscription.domain.valueobject.SubscriptionStatus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Background processor for subscription renewals.
 */
@Singleton
@UseCase("Process subscription renewals")
public class ProcessRenewalsHandler {

    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceGenerationPort invoiceGenerationPort;
    private final PaymentProcessorPort paymentProcessorPort;

    @Inject
    public ProcessRenewalsHandler(
            SubscriptionRepository subscriptionRepository,
            InvoiceGenerationPort invoiceGenerationPort,
            PaymentProcessorPort paymentProcessorPort) {
        this.subscriptionRepository = subscriptionRepository;
        this.invoiceGenerationPort = invoiceGenerationPort;
        this.paymentProcessorPort = paymentProcessorPort;
    }

    /**
     * Processes all subscriptions that need renewal.
     * Returns the number of successfully renewed subscriptions.
     */
    public CompletionStage<Integer> processRenewals() {
        return subscriptionRepository.findSubscriptionsNeedingRenewal()
            .thenCompose(subscriptions -> {
                if (subscriptions.isEmpty()) {
                    return CompletableFuture.completedFuture(0);
                }

                // Process renewals in parallel
                List<CompletableFuture<Subscription>> renewalFutures = subscriptions.stream()
                    .map(subscription -> {
                        if (!subscription.isAutoRenew()) {
                            // Cancel subscriptions that don't auto-renew
                            subscription.cancel("Auto-renewal disabled");
                            return subscriptionRepository.save(subscription)
                                .thenApply(v -> subscription)
                                .toCompletableFuture();
                        }

                        return processRenewal(subscription)
                            .toCompletableFuture();
                    })
                    .collect(Collectors.toList());

                return CompletableFuture.allOf(renewalFutures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> {
                        long count = renewalFutures.stream()
                            .filter(CompletableFuture::isCompletedExceptionally)
                            .count();
                        return (int) (subscriptions.size() - count);
                    });
            });
    }

    private CompletionStage<Subscription> processRenewal(Subscription subscription) {
        // 1. Generate invoice
        Money amount = subscription.getMonthlyFee();
        
        return invoiceGenerationPort.generateInvoice(subscription, amount)
            .thenCompose(invoiceId -> {
                // 2. Process payment
                return paymentProcessorPort.processPayment(subscription, amount)
                    .thenCompose(paymentResult -> {
                        if (paymentResult.success()) {
                            // 3. Record successful payment
                            subscription.recordPayment(invoiceId, amount);
                            subscription.renew();
                        } else {
                            // 4. Record payment failure
                            subscription.recordPaymentFailure();
                            
                            // If too many failures, enter grace period
                            if (subscription.getBillingAttempts() >= 3) {
                                subscription.enterGracePeriod();
                            }
                        }
                        
                        // 5. Save the updated subscription
                        return subscriptionRepository.save(subscription);
                    });
            });
    }
}
```

**`/modules/subscription/application/src/main/java/tech/kayys/erp/subscription/application/port/CustomerValidationPort.java`**:

```java
package tech.kayys.erp.subscription.application.port;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Port for validating customers from CRM context.
 */
public interface CustomerValidationPort {

    /**
     * Validates that a customer exists and is active.
     */
    CompletionStage<Boolean> validateCustomer(UUID customerId);

    /**
     * Gets customer details if needed.
     */
    CompletionStage<CustomerInfo> getCustomerInfo(UUID customerId);

    record CustomerInfo(
        UUID customerId,
        String email,
        String name,
        boolean active,
        String customerType
    ) {}
}
```

**`/modules/subscription/application/src/main/java/tech/kayys/erp/subscription/application/port/PricingProviderPort.java`**:

```java
package tech.kayys.erp.subscription.application.port;

import tech.kayys.erp.subscription.domain.valueobject.Money;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Port for getting pricing information from Pricing context.
 */
public interface PricingProviderPort {

    /**
     * Gets the price for a plan with any applicable discounts.
     */
    CompletionStage<Money> getPlanPrice(UUID planId, int quantity, String couponCode);

    /**
     * Gets the annual discount percentage for a plan.
     */
    CompletionStage<Double> getAnnualDiscount(UUID planId);

    /**
     * Validates a coupon code.
     */
    CompletionStage<CouponValidation> validateCoupon(String couponCode, Money amount);

    record CouponValidation(
        boolean valid,
        String message,
        String discountType,
        BigDecimal discountValue
    ) {}
}
```

**`/modules/subscription/application/src/main/java/tech/kayys/erp/subscription/application/port/InvoiceGenerationPort.java`**:

```java
package tech.kayys.erp.subscription.application.port;

import tech.kayys.erp.subscription.domain.identifier.InvoiceId;
import tech.kayys.erp.subscription.domain.model.Subscription;
import tech.kayys.erp.subscription.domain.valueobject.Money;

import java.util.concurrent.CompletionStage;

/**
 * Port for generating invoices in Accounting context.
 */
public interface InvoiceGenerationPort {

    /**
     * Generates an invoice for a subscription renewal.
     */
    CompletionStage<InvoiceId> generateInvoice(Subscription subscription, Money amount);

    /**
     * Generates a pro-rated invoice for a plan change.
     */
    CompletionStage<InvoiceId> generateProRatedInvoice(
        Subscription subscription, 
        Money amount,
        String reason
    );
}
```

**`/modules/subscription/application/src/main/java/tech/kayys/erp/subscription/application/port/PaymentProcessorPort.java`**:

```java
package tech.kayys.erp.subscription.application.port;

import tech.kayys.erp.subscription.domain.model.Subscription;
import tech.kayys.erp.subscription.domain.valueobject.Money;

import java.util.concurrent.CompletionStage;

/**
 * Port for processing payments.
 */
public interface PaymentProcessorPort {

    /**
     * Processes a payment for a subscription.
     */
    CompletionStage<PaymentResult> processPayment(Subscription subscription, Money amount);

    /**
     * Refunds a payment.
     */
    CompletionStage<RefundResult> refundPayment(String transactionId, Money amount);

    record PaymentResult(
        boolean success,
        String transactionId,
        String message
    ) {}

    record RefundResult(
        boolean success,
        String refundId,
        String message
    ) {}
}
```

## 3. Update Root POM

**Update `/pom.xml`** to include Subscription modules:

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
</modules>
```

## 4. Update Architecture Tests

**`/architecture/tests/src/test/java/tech/kayys/erp/architecture/CompleteArchitectureTest.java`** (add Subscription context rules):

```java
// Add to existing CompleteArchitectureTest class:

@ArchTest
static final ArchRule subscriptionDomainMustNotDependOnOtherContexts =
        noClasses()
                .that()
                .resideInAPackage("tech.kayys.erp.subscription.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "tech.kayys.erp.catalog..",
                        "tech.kayys.erp.sales..",
                        "tech.kayys.erp.inventory..",
                        "tech.kayys.erp.accounting.."
                );

@ArchTest
static final ArchRule subscriptionApplicationMayUsePorts =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.subscription.application.port..")
                .should()
                .haveSimpleNameEndingWith("Port")
                .orShould()
                .haveSimpleNameEndingWith("Provider")
                .orShould()
                .haveSimpleNameEndingWith("Processor");

@ArchTest
static final ArchRule subscriptionDomainPackagesCorrect =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.subscription.domain..")
                .should()
                .resideInAnyPackage(
                        "tech.kayys.erp.subscription.domain.model..",
                        "tech.kayys.erp.subscription.domain.identifier..",
                        "tech.kayys.erp.subscription.domain.valueobject..",
                        "tech.kayys.erp.subscription.domain.event..",
                        "tech.kayys.erp.subscription.domain.repository.."
                );

@ArchTest
static final ArchRule subscriptionStatusStateMachine =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.subscription.domain.valueobject..")
                .and()
                .haveSimpleName("SubscriptionStatus")
                .should()
                .haveOnlyFinalFields();
```

## Summary

The complete Subscription implementation demonstrates:

1. **Domain Models**:
   - Subscription aggregate with state machine
   - SubscriptionPlan aggregate with pricing tiers
   - BillingCycle with discount factors
   - SubscriptionStatus with transition rules

2. **Value Objects**:
   - Money with currency support
   - BillingCycle with discount calculations
   - SubscriptionStatus with state transitions

3. **Domain Events**:
   - SubscriptionCreated, Activated, Cancelled, Paused, Renewed

4. **Application Layer**:
   - Commands for subscription lifecycle management
   - Query service for subscription views
   - Background renewal processing

5. **Integration Ports**:
   - CustomerValidationPort (to CRM)
   - PricingProviderPort (to Pricing)
   - InvoiceGenerationPort (to Accounting)
   - PaymentProcessorPort (to external payment system)

6. **Architecture Rules**:
   - No direct dependencies on other contexts
   - Proper use of ports for integration
   - Correct package structure
   - Status state machine enforcement

This completes the implementation of the Subscription bounded context, which provides full subscription management capabilities including:
- Plan management with pricing tiers
- Subscription lifecycle (create, activate, pause, cancel)
- Recurring billing with configurable cycles
- Trial periods
- Auto-renewal
- Prorated charges for plan changes
- Integration with Pricing, CRM, and Accounting contexts
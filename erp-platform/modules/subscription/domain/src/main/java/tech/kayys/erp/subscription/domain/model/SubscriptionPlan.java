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
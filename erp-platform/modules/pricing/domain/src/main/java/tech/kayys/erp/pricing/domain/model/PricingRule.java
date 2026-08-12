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
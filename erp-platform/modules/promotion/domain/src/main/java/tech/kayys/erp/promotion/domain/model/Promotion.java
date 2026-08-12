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
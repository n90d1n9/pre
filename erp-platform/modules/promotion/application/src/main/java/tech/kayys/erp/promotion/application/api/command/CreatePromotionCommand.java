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
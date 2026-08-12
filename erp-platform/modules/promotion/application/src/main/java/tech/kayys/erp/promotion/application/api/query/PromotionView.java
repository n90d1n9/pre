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
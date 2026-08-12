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
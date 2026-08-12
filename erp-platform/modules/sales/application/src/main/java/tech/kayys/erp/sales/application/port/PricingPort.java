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
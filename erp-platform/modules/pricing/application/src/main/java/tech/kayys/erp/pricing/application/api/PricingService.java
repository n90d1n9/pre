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
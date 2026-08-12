package tech.kayys.erp.pricing.application.internal;

import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.pricing.application.api.CouponValidationResult;
import tech.kayys.erp.pricing.application.api.MoneyCommand;
import tech.kayys.erp.pricing.application.api.PricingService;
import tech.kayys.erp.pricing.application.api.command.CalculatePriceCommand;
import tech.kayys.erp.pricing.application.api.command.CalculateTaxCommand;
import tech.kayys.erp.pricing.application.api.query.PriceCalculationView;
import tech.kayys.erp.pricing.application.api.query.TaxCalculationView;
import tech.kayys.erp.pricing.domain.identifier.ProductId;
import tech.kayys.erp.pricing.domain.model.PriceCalculation;
import tech.kayys.erp.pricing.domain.model.PricingRule;
import tech.kayys.erp.pricing.domain.repository.PricingRuleRepository;
import tech.kayys.erp.pricing.domain.repository.TaxRateRepository;
import tech.kayys.erp.pricing.domain.valueobject.Money;
import tech.kayys.erp.pricing.domain.valueobject.TaxRate;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Implementation of the PricingService.
 * This is the core pricing engine that applies all pricing rules and tax calculations.
 */
@Singleton
@UseCase("Pricing calculation service")
public class PricingServiceImpl implements PricingService {

    private final PricingRuleRepository pricingRuleRepository;
    private final TaxRateRepository taxRateRepository;

    @Inject
    public PricingServiceImpl(
            PricingRuleRepository pricingRuleRepository,
            TaxRateRepository taxRateRepository) {
        this.pricingRuleRepository = pricingRuleRepository;
        this.taxRateRepository = taxRateRepository;
    }

    @Override
    public CompletionStage<PriceCalculationView> calculatePrice(CalculatePriceCommand command) {
        // 1. Get base price as Money
        Money basePrice = Money.of(
            command.basePrice().getAmountAsBigDecimal(),
            command.basePrice().currencyCode()
        );

        ProductId productId = ProductId.of(command.productId());
        int quantity = command.quantity();

        // 2. Find applicable pricing rules
        return findApplicableRules(command.productId(), command.couponCode())
            .thenCompose(rules -> {
                // 3. Apply rules in priority order
                PriceCalculation.Builder calculationBuilder = PriceCalculation.builder()
                    .productId(productId)
                    .basePrice(basePrice);

                Money currentPrice = basePrice;

                // Sort rules by priority (higher priority first)
                List<PricingRule> sortedRules = rules.stream()
                    .sorted(Comparator.comparing(PricingRule::getPriority).reversed())
                    .toList();

                // Apply each rule
                for (PricingRule rule : sortedRules) {
                    Money discountAmount = rule.calculateDiscount(currentPrice, quantity);
                    
                    if (!discountAmount.isZero()) {
                        currentPrice = currentPrice.subtract(discountAmount);
                        calculationBuilder.addDiscount(
                            new PriceCalculation.AppliedDiscount(
                                rule.getId().toString(),
                                rule.getName(),
                                rule.getDiscountType().name(),
                                rule.getDiscountValue(),
                                discountAmount
                            )
                        );
                    }
                }

                calculationBuilder.discountedPrice(currentPrice);

                // 4. Calculate taxes
                return calculateTaxes(currentPrice, command.jurisdiction())
                    .thenApply(taxAmount -> {
                        Money finalPrice = currentPrice.add(taxAmount);
                        calculationBuilder
                            .taxAmount(taxAmount)
                            .finalPrice(finalPrice);

                        // Add tax details
                        taxAmount.getAmount(); // We'll add tax details separately

                        return PriceCalculationView.fromDomain(calculationBuilder.build());
                    });
            });
    }

    @Override
    public CompletionStage<TaxCalculationView> calculateTax(CalculateTaxCommand command) {
        Money amount = Money.of(
            command.amount().getAmountAsBigDecimal(),
            command.amount().currencyCode()
        );

        return taxRateRepository.findByJurisdiction(command.jurisdiction())
            .thenApply(taxRates -> {
                // Find applicable tax rate for this product category
                TaxRate applicableRate = taxRates.stream()
                    .filter(rate -> rate.isCurrentlyEffective())
                    .filter(rate -> command.productCategory() == null || 
                        rate.getProductCategory().equals(command.productCategory()))
                    .findFirst()
                    .orElse(null);

                if (applicableRate == null) {
                    // No tax applicable
                    return TaxCalculationView.fromDomain(
                        amount,
                        Money.zero(amount.getCurrency().getCurrencyCode()),
                        command.jurisdiction(),
                        command.taxType().name(),
                        BigDecimal.ZERO
                    );
                }

                Money taxAmount = amount.percentage(applicableRate.getRate());
                
                return TaxCalculationView.fromDomain(
                    amount,
                    taxAmount,
                    command.jurisdiction(),
                    command.taxType().name(),
                    applicableRate.getRate()
                );
            });
    }

    @Override
    public CompletionStage<CouponValidationResult> validateCoupon(String couponCode, Money amount) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                new CouponValidationResult(false, "Coupon code is empty")
            );
        }

        return pricingRuleRepository.findByCouponCode(couponCode)
            .thenApply(rule -> {
                if (rule == null) {
                    return new CouponValidationResult(false, "Invalid coupon code");
                }
                if (!rule.isActive()) {
                    return new CouponValidationResult(false, "Coupon is expired or inactive");
                }
                if (rule.getMinimumOrderAmount() != null) {
                    Money minAmount = Money.of(rule.getMinimumOrderAmount(), amount.getCurrency().getCurrencyCode());
                    if (amount.isLessThan(minAmount)) {
                        return new CouponValidationResult(false, 
                            "Minimum order amount of " + minAmount + " required");
                    }
                }
                return new CouponValidationResult(true, "Coupon is valid", rule);
            });
    }

    @Override
    public CompletionStage<Money> getBasePrice(UUID productId) {
        // This would typically call the Catalog context
        // For now, return a default
        return CompletableFuture.completedFuture(Money.of("0.00", "USD"));
    }

    private CompletionStage<List<PricingRule>> findApplicableRules(UUID productId, String couponCode) {
        return pricingRuleRepository.findApplicableRules(productId)
            .thenApply(rules -> {
                List<PricingRule> applicableRules = new ArrayList<>(rules);
                
                // Add coupon rule if present
                if (couponCode != null && !couponCode.trim().isEmpty()) {
                    pricingRuleRepository.findByCouponCode(couponCode)
                        .thenAccept(couponRule -> {
                            if (couponRule != null && couponRule.isActive()) {
                                applicableRules.add(couponRule);
                            }
                        });
                }
                
                return applicableRules;
            });
    }

    private CompletionStage<Money> calculateTaxes(Money amount, String jurisdiction) {
        return taxRateRepository.findByJurisdiction(jurisdiction)
            .thenApply(taxRates -> {
                // Find the highest applicable tax rate
                // In a real system, you might apply multiple taxes
                TaxRate applicableRate = taxRates.stream()
                    .filter(TaxRate::isCurrentlyEffective)
                    .findFirst()
                    .orElse(null);

                if (applicableRate == null) {
                    return Money.zero(amount.getCurrency().getCurrencyCode());
                }

                return amount.percentage(applicableRate.getRate());
            });
    }
}
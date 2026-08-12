package tech.kayys.erp.pricing.application.api.query;

import tech.kayys.erp.pricing.domain.model.PriceCalculation;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * View of a price calculation result.
 */
public record PriceCalculationView(
        String productId,
        String basePrice,
        String discountedPrice,
        String taxAmount,
        String finalPrice,
        String currencyCode,
        List<DiscountView> appliedDiscounts,
        List<TaxView> appliedTaxes,
        BigDecimal totalDiscountPercentage,
        boolean hasDiscounts,
        String calculatedAt
) {

    public static PriceCalculationView fromDomain(PriceCalculation calculation) {
        return new PriceCalculationView(
            calculation.getProductId().toString(),
            calculation.getBasePrice().getAmount().toPlainString(),
            calculation.getDiscountedPrice() != null ? 
                calculation.getDiscountedPrice().getAmount().toPlainString() : null,
            calculation.getTaxAmount() != null && !calculation.getTaxAmount().isZero() ?
                calculation.getTaxAmount().getAmount().toPlainString() : "0.00",
            calculation.getFinalPrice().getAmount().toPlainString(),
            calculation.getCurrencyCode(),
            calculation.getAppliedDiscounts().stream()
                .map(DiscountView::fromDomain)
                .collect(Collectors.toList()),
            calculation.getAppliedTaxes().stream()
                .map(TaxView::fromDomain)
                .collect(Collectors.toList()),
            calculation.getTotalDiscountPercentage(),
            calculation.hasDiscounts(),
            calculation.getCalculatedAt().toString()
        );
    }

    public record DiscountView(
            String ruleName,
            String type,
            BigDecimal value,
            String discountAmount,
            String currencyCode
    ) {
        public static DiscountView fromDomain(PriceCalculation.AppliedDiscount discount) {
            return new DiscountView(
                discount.getRuleName(),
                discount.getType(),
                discount.getValue(),
                discount.getDiscountAmount().getAmount().toPlainString(),
                discount.getDiscountAmount().getCurrency().getCurrencyCode()
            );
        }
    }

    public record TaxView(
            String taxType,
            String jurisdiction,
            BigDecimal rate,
            String taxAmount,
            String currencyCode
    ) {
        public static TaxView fromDomain(PriceCalculation.AppliedTax tax) {
            return new TaxView(
                tax.getTaxType(),
                tax.getJurisdiction(),
                tax.getRate(),
                tax.getTaxAmount().getAmount().toPlainString(),
                tax.getTaxAmount().getCurrency().getCurrencyCode()
            );
        }
    }
}
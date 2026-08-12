package tech.kayys.erp.pricing.application.api.query;

import tech.kayys.erp.pricing.domain.valueobject.Money;

/**
 * View of a tax calculation result.
 */
public record TaxCalculationView(
        String taxableAmount,
        String taxRate,
        String taxAmount,
        String totalAmount,
        String currencyCode,
        String jurisdiction,
        String taxType
) {

    public static TaxCalculationView fromDomain(
            Money taxableAmount,
            Money taxAmount,
            String jurisdiction,
            String taxType,
            BigDecimal rate) {
        
        return new TaxCalculationView(
            taxableAmount.getAmount().toPlainString(),
            rate.toPlainString() + "%",
            taxAmount.getAmount().toPlainString(),
            taxableAmount.add(taxAmount).getAmount().toPlainString(),
            taxableAmount.getCurrency().getCurrencyCode(),
            jurisdiction,
            taxType
        );
    }
}
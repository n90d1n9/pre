package tech.kayys.erp.pricing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.pricing.application.api.MoneyCommand;
import tech.kayys.erp.pricing.domain.valueobject.TaxType;

/**
 * Command to calculate tax for a given amount.
 */
public record CalculateTaxCommand(
        MoneyCommand amount,
        String jurisdiction,
        TaxType taxType,
        String productCategory
) implements Command<TaxCalculationView> {

    public CalculateTaxCommand {
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required");
        }
        if (jurisdiction == null || jurisdiction.trim().isEmpty()) {
            throw new IllegalArgumentException("Jurisdiction is required");
        }
        if (taxType == null) {
            throw new IllegalArgumentException("Tax type is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private MoneyCommand amount;
        private String jurisdiction;
        private TaxType taxType = TaxType.VAT;
        private String productCategory = "standard";

        public Builder amount(MoneyCommand amount) {
            this.amount = amount;
            return this;
        }

        public Builder amount(String amount, String currencyCode) {
            this.amount = new MoneyCommand(amount, currencyCode);
            return this;
        }

        public Builder jurisdiction(String jurisdiction) {
            this.jurisdiction = jurisdiction;
            return this;
        }

        public Builder taxType(TaxType taxType) {
            this.taxType = taxType;
            return this;
        }

        public Builder productCategory(String productCategory) {
            this.productCategory = productCategory;
            return this;
        }

        public CalculateTaxCommand build() {
            return new CalculateTaxCommand(amount, jurisdiction, taxType, productCategory);
        }
    }
}
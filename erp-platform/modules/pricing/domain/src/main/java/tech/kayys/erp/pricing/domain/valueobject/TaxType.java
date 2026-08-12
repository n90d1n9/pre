package tech.kayys.erp.pricing.domain.valueobject;

/**
 * Types of taxes.
 */
public enum TaxType {
    VAT("Value Added Tax"),
    GST("Goods and Services Tax"),
    SALES_TAX("Sales Tax"),
    USE_TAX("Use Tax"),
    EXCISE("Excise Tax"),
    CUSTOMS("Customs Duty");

    private final String description;

    TaxType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
package tech.kayys.erp.tax;


import tech.kayys.erp.foundation.domain.ValueObject;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Tax code for VAT/GST/Sales tax.
 */
public final class TaxCode implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String code;
    private final String name;
    private final BigDecimal rate;
    private final boolean isCompound;
    private final String jurisdiction;

    public TaxCode(String code, String name, BigDecimal rate, boolean isCompound, String jurisdiction) {
        this.code = code;
        this.name = name;
        this.rate = rate;
        this.isCompound = isCompound;
        this.jurisdiction = jurisdiction;
        validate();
    }

    @Override
    public void validate() {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Tax code cannot be empty");
        }
        if (rate == null || rate.signum() < 0 || rate.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Tax rate must be between 0 and 100");
        }
        if (jurisdiction == null || jurisdiction.trim().isEmpty()) {
            throw new IllegalArgumentException("Jurisdiction cannot be empty");
        }
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public BigDecimal getRate() { return rate; }
    public boolean isCompound() { return isCompound; }
    public String getJurisdiction() { return jurisdiction; }

    /**
     * Calculates tax on an amount.
     */
    public Money calculateTax(Money amount) {
        return amount.percentage(rate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaxCode taxCode = (TaxCode) o;
        return Objects.equals(code, taxCode.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "TaxCode{" +
                "code='" + code + '\'' +
                ", rate=" + rate + "%" +
                ", jurisdiction='" + jurisdiction + '\'' +
                '}';
    }

    public static TaxCode of(String code, String name, BigDecimal rate, String jurisdiction) {
        return new TaxCode(code, name, rate, false, jurisdiction);
    }

    public static TaxCode of(String code, String name, BigDecimal rate, boolean isCompound, String jurisdiction) {
        return new TaxCode(code, name, rate, isCompound, jurisdiction);
    }
}
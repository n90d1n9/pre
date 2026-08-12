package tech.kayys.erp.pricing.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;
import tech.kayys.erp.pricing.domain.identifier.TaxRateId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Tax rate value object.
 */
public final class TaxRate implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final TaxRateId id;
    private final TaxType taxType;
    private final BigDecimal rate;
    private final String jurisdiction; // e.g., "US-CA", "UK", "EU"
    private final String productCategory; // e.g., "standard", "reduced", "zero"
    private final Instant effectiveFrom;
    private final Instant effectiveTo;

    public TaxRate(
            TaxRateId id,
            TaxType taxType,
            BigDecimal rate,
            String jurisdiction,
            String productCategory,
            Instant effectiveFrom,
            Instant effectiveTo) {
        this.id = id;
        this.taxType = taxType;
        this.rate = rate;
        this.jurisdiction = jurisdiction;
        this.productCategory = productCategory;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        validate();
    }

    @Override
    public void validate() {
        if (id == null) {
            throw new IllegalArgumentException("Tax rate ID cannot be null");
        }
        if (taxType == null) {
            throw new IllegalArgumentException("Tax type cannot be null");
        }
        if (rate == null || rate.signum() < 0 || rate.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Rate must be between 0 and 100");
        }
        if (jurisdiction == null || jurisdiction.trim().isEmpty()) {
            throw new IllegalArgumentException("Jurisdiction cannot be empty");
        }
        if (effectiveFrom == null) {
            throw new IllegalArgumentException("Effective from date cannot be null");
        }
        if (effectiveTo != null && effectiveTo.isBefore(effectiveFrom)) {
            throw new IllegalArgumentException("Effective to date must be after effective from date");
        }
    }

    public TaxRateId getId() { return id; }
    public TaxType getTaxType() { return taxType; }
    public BigDecimal getRate() { return rate; }
    public String getJurisdiction() { return jurisdiction; }
    public String getProductCategory() { return productCategory; }
    public Instant getEffectiveFrom() { return effectiveFrom; }
    public Instant getEffectiveTo() { return effectiveTo; }

    public boolean isEffective(Instant date) {
        if (date.isBefore(effectiveFrom)) {
            return false;
        }
        return effectiveTo == null || date.isBefore(effectiveTo);
    }

    public boolean isCurrentlyEffective() {
        return isEffective(Instant.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaxRate taxRate = (TaxRate) o;
        return Objects.equals(id, taxRate.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TaxRate{" +
                "id=" + id +
                ", taxType=" + taxType +
                ", rate=" + rate + "%" +
                ", jurisdiction='" + jurisdiction + '\'' +
                '}';
    }

    public static TaxRate of(
            TaxRateId id,
            TaxType taxType,
            BigDecimal rate,
            String jurisdiction,
            String productCategory,
            Instant effectiveFrom) {
        return new TaxRate(id, taxType, rate, jurisdiction, productCategory, effectiveFrom, null);
    }

    public static TaxRate of(
            TaxType taxType,
            BigDecimal rate,
            String jurisdiction) {
        return new TaxRate(
            TaxRateId.generate(),
            taxType,
            rate,
            jurisdiction,
            "standard",
            Instant.now(),
            null
        );
    }
}

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * PPN (Value Added Tax) configuration for Indonesia.
 * Implements current 11% rate with scheduled 12% increase.
 */
public final class PPNConfig implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    // Current rate: 11% (effective April 2022)
    // Increasing to 12% by 2025
    private static final double RATE_11 = 0.11;
    private static final double RATE_12 = 0.12;
    private static final Instant RATE_CHANGE_DATE = Instant.parse("2025-01-01T00:00:00Z");

    private final BigDecimal rate;
    private final String rateDescription;
    private final boolean isEffective;
    private final Instant effectiveFrom;
    private final Instant effectiveTo;
    private final boolean isB2B; // Business-to-Business transaction
    private final boolean isDipungut; // Buyer collects VAT

    public PPNConfig(
            BigDecimal rate,
            String rateDescription,
            boolean isEffective,
            Instant effectiveFrom,
            Instant effectiveTo,
            boolean isB2B,
            boolean isDipungut) {
        this.rate = rate;
        this.rateDescription = rateDescription;
        this.isEffective = isEffective;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.isB2B = isB2B;
        this.isDipungut = isDipungut;
        validate();
    }

    @Override
    public void validate() {
        if (rate == null || rate.signum() < 0) {
            throw new IllegalArgumentException("Rate must be positive");
        }
        if (effectiveFrom == null) {
            throw new IllegalArgumentException("Effective from date cannot be null");
        }
        if (effectiveTo != null && effectiveTo.isBefore(effectiveFrom)) {
            throw new IllegalArgumentException("Effective to must be after effective from");
        }
    }

    // Getters
    public BigDecimal getRate() { return rate; }
    public String getRateDescription() { return rateDescription; }
    public boolean isEffective() { return isEffective; }
    public Instant getEffectiveFrom() { return effectiveFrom; }
    public Instant getEffectiveTo() { return effectiveTo; }
    public boolean isB2B() { return isB2B; }
    public boolean isDipungut() { return isDipungut; }

    /**
     * Gets the current effective PPN rate based on current date.
     */
    public static PPNConfig getCurrentRate() {
        Instant now = Instant.now();
        double rate = now.isAfter(RATE_CHANGE_DATE) ? RATE_12 : RATE_11;
        
        return new PPNConfig(
            BigDecimal.valueOf(rate),
            "PPN " + (rate * 100) + "%",
            true,
            now,
            null,
            false,
            false
        );
    }

    /**
     * Gets the PPN rate for a specific date.
     */
    public static PPNConfig getRateForDate(Instant date) {
        double rate = date.isAfter(RATE_CHANGE_DATE) ? RATE_12 : RATE_11;
        
        return new PPNConfig(
            BigDecimal.valueOf(rate),
            "PPN " + (rate * 100) + "%",
            true,
            date,
            null,
            false,
            false
        );
    }

    @Override
    public String toString() {
        return "PPNConfig{" +
                "rate=" + rate.multiply(BigDecimal.valueOf(100)) + "%" +
                ", isB2B=" + isB2B +
                ", isDipungut=" + isDipungut +
                '}';
    }
}
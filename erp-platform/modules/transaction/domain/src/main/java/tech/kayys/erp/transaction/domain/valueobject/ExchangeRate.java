package tech.kayys.erp.transaction.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

/**
 * Exchange rate value object.
 */
public final class ExchangeRate implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String fromCurrency;
    private final String toCurrency;
    private final BigDecimal rate;
    private final BigDecimal inverseRate;
    private final Instant rateDate;
    private final String source;
    private final double markupPercentage;

    public ExchangeRate(
            String fromCurrency,
            String toCurrency,
            BigDecimal rate,
            Instant rateDate,
            String source,
            double markupPercentage) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate.setScale(6, RoundingMode.HALF_UP);
        this.inverseRate = BigDecimal.ONE.divide(rate, 6, RoundingMode.HALF_UP);
        this.rateDate = rateDate != null ? rateDate : Instant.now();
        this.source = source;
        this.markupPercentage = markupPercentage;
        validate();
    }

    @Override
    public void validate() {
        if (fromCurrency == null || fromCurrency.trim().isEmpty()) {
            throw new IllegalArgumentException("From currency cannot be empty");
        }
        if (toCurrency == null || toCurrency.trim().isEmpty()) {
            throw new IllegalArgumentException("To currency cannot be empty");
        }
        if (rate == null || rate.signum() <= 0) {
            throw new IllegalArgumentException("Rate must be positive");
        }
        if (markupPercentage < 0) {
            throw new IllegalArgumentException("Markup percentage cannot be negative");
        }
    }

    // Getters
    public String getFromCurrency() { return fromCurrency; }
    public String getToCurrency() { return toCurrency; }
    public BigDecimal getRate() { return rate; }
    public BigDecimal getInverseRate() { return inverseRate; }
    public Instant getRateDate() { return rateDate; }
    public String getSource() { return source; }
    public double getMarkupPercentage() { return markupPercentage; }

    /**
     * Converts an amount from the source currency to the target currency.
     */
    public Money convert(Money amount) {
        if (!amount.getCurrency().getCurrencyCode().equals(fromCurrency)) {
            throw new IllegalArgumentException(
                "Amount currency must match from currency: " + fromCurrency
            );
        }
        
        BigDecimal converted = amount.getAmount().multiply(rate);
        // Apply markup if any
        BigDecimal markup = converted.multiply(BigDecimal.valueOf(markupPercentage))
            .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        converted = converted.add(markup);
        
        return Money.of(converted, toCurrency);
    }

    /**
     * Checks if this exchange rate is still valid.
     */
    public boolean isValid(long maxAgeSeconds) {
        return Instant.now().isBefore(rateDate.plusSeconds(maxAgeSeconds));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRate that = (ExchangeRate) o;
        return Objects.equals(fromCurrency, that.fromCurrency) &&
               Objects.equals(toCurrency, that.toCurrency) &&
               rate.compareTo(that.rate) == 0 &&
               Objects.equals(rateDate, that.rateDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromCurrency, toCurrency, rate, rateDate);
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                fromCurrency + " -> " + toCurrency +
                ", rate=" + rate.toPlainString() +
                ", date=" + rateDate +
                '}';
    }

    public static ExchangeRate of(
            String fromCurrency,
            String toCurrency,
            BigDecimal rate) {
        return new ExchangeRate(fromCurrency, toCurrency, rate, Instant.now(), "SYSTEM", 0.0);
    }

    public static ExchangeRate of(
            String fromCurrency,
            String toCurrency,
            BigDecimal rate,
            double markupPercentage) {
        return new ExchangeRate(fromCurrency, toCurrency, rate, Instant.now(), "SYSTEM", markupPercentage);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String fromCurrency;
        private String toCurrency;
        private BigDecimal rate;
        private Instant rateDate;
        private String source = "SYSTEM";
        private double markupPercentage = 0.0;

        public Builder fromCurrency(String fromCurrency) {
            this.fromCurrency = fromCurrency;
            return this;
        }

        public Builder toCurrency(String toCurrency) {
            this.toCurrency = toCurrency;
            return this;
        }

        public Builder rate(BigDecimal rate) {
            this.rate = rate;
            return this;
        }

        public Builder rateDate(Instant rateDate) {
            this.rateDate = rateDate;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder markupPercentage(double markupPercentage) {
            this.markupPercentage = markupPercentage;
            return this;
        }

        public ExchangeRate build() {
            if (rateDate == null) {
                rateDate = Instant.now();
            }
            return new ExchangeRate(fromCurrency, toCurrency, rate, rateDate, source, markupPercentage);
        }
    }
}
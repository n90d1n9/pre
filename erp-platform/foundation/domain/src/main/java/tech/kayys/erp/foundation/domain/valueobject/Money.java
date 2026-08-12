package tech.kayys.erp.foundation.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;

/**
 * Monetary value with an explicit currency.
 *
 * Money is immutable. Deliberately does NOT force a fixed scale (e.g.
 * scale = 2) - ERP/accounting needs a proper RoundingPolicy / MoneyMath
 * / CurrencyRules / TaxCalculationPolicy elsewhere rather than baking
 * an assumption into this primitive.
 */
public record Money(
        BigDecimal amount,
        Currency currency
) implements ValueObject {

    public Money {
        Objects.requireNonNull(
                amount,
                "Money amount cannot be null"
        );

        Objects.requireNonNull(
                currency,
                "Money currency cannot be null"
        );
    }

    public static Money zero(Currency currency) {
        return new Money(ZERO, currency);
    }

    public static Money of(
            BigDecimal amount,
            Currency currency
    ) {
        return new Money(amount, currency);
    }

    public static Money of(
            long amount,
            Currency currency
    ) {
        return new Money(
                BigDecimal.valueOf(amount),
                currency
        );
    }

    public Money add(Money other) {
        requireSameCurrency(other);

        return new Money(
                amount.add(other.amount),
                currency
        );
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);

        return new Money(
                amount.subtract(other.amount),
                currency
        );
    }

    public Money multiply(BigDecimal multiplier) {
        Objects.requireNonNull(
                multiplier,
                "Multiplier cannot be null"
        );

        return new Money(
                amount.multiply(multiplier),
                currency
        );
    }

    public Money negate() {
        return new Money(
                amount.negate(),
                currency
        );
    }

    public Money abs() {
        return new Money(
                amount.abs(),
                currency
        );
    }

    public boolean isZero() {
        return amount.compareTo(ZERO) == 0;
    }

    public boolean isPositive() {
        return amount.compareTo(ZERO) > 0;
    }

    public boolean isNegative() {
        return amount.compareTo(ZERO) < 0;
    }

    public int compareTo(Money other) {
        requireSameCurrency(other);

        return amount.compareTo(other.amount);
    }

    public boolean greaterThan(Money other) {
        return compareTo(other) > 0;
    }

    public boolean greaterThanOrEqual(Money other) {
        return compareTo(other) >= 0;
    }

    public boolean lessThan(Money other) {
        return compareTo(other) < 0;
    }

    public boolean lessThanOrEqual(Money other) {
        return compareTo(other) <= 0;
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(
                other,
                "Money cannot be null"
        );

        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "Currency mismatch: "
                            + currency.code()
                            + " vs "
                            + other.currency.code()
            );
        }
    }

}

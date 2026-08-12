package tech.kayys.erp.pricing.application.api;

import java.math.BigDecimal;

/**
 * Money DTO for commands.
 */
public record MoneyCommand(
        String amount,
        String currencyCode
) {
    public MoneyCommand {
        if (amount == null || amount.trim().isEmpty()) {
            throw new IllegalArgumentException("Amount cannot be empty");
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code cannot be empty");
        }
        // Validate amount is numeric
        try {
            new BigDecimal(amount);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + amount);
        }
    }

    public BigDecimal getAmountAsBigDecimal() {
        return new BigDecimal(amount);
    }
}
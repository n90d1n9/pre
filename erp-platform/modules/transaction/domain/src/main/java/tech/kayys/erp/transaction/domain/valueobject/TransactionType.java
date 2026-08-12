package tech.kayys.erp.transaction.domain.valueobject;

/**
 * Types of financial transactions.
 */
public enum TransactionType {
    SALE("Sale - Purchase transaction"),
    REFUND("Refund - Money returned to customer"),
    AUTHORIZATION("Authorization - Hold on funds"),
    CAPTURE("Capture - Finalize authorization"),
    VOID("Void - Cancel transaction"),
    REVERSAL("Reversal - Reverse authorization"),
    ADJUSTMENT("Adjustment - Correction transaction"),
    CHARGEBACK("Chargeback - Customer dispute"),
    SETTLEMENT("Settlement - Batch settlement"),
    BATCH("Batch - Multiple transactions"),
    TIP_ADJUSTMENT("Tip Adjustment - Modify tip amount"),
    PARTIAL_CAPTURE("Partial Capture - Partial authorization"),
    PARTIAL_REFUND("Partial Refund - Partial refund of amount");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSale() {
        return this == SALE || this == CAPTURE || this == AUTHORIZATION;
    }

    public boolean isRefund() {
        return this == REFUND || this == PARTIAL_REFUND;
    }

    public boolean isReversal() {
        return this == VOID || this == REVERSAL || this == CHARGEBACK;
    }
}
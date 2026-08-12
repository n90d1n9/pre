package tech.kayys.erp.transaction.domain.valueobject;

/**
 * Status of a financial transaction.
 */
public enum TransactionStatus {
    // Initial states
    PENDING("Pending - Transaction initiated"),
    AUTHORIZED("Authorized - Funds approved"),
    CAPTURED("Captured - Funds collected"),
    
    // Completion states
    SETTLED("Settled - Funds transferred"),
    COMPLETED("Completed - Transaction finalized"),
    
    // Failure states
    FAILED("Failed - Transaction declined"),
    DECLINED("Declined - Payment refused"),
    CANCELLED("Cancelled - Transaction voided"),
    EXPIRED("Expired - Authorization expired"),
    
    // Dispute states
    CHARGEBACK("Chargeback - Customer dispute"),
    CHARGEBACK_REVERSED("Chargeback Reversed - Dispute resolved"),
    
    // Reversal states
    REFUNDED("Refunded - Money returned"),
    PARTIALLY_REFUNDED("Partially Refunded - Partial return"),
    REVERSED("Reversed - Transaction undone");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSuccess() {
        return this == COMPLETED || this == SETTLED || this == CAPTURED || this == AUTHORIZED;
    }

    public boolean isFailure() {
        return this == FAILED || this == DECLINED || this == CANCELLED || this == EXPIRED;
    }

    public boolean isRefund() {
        return this == REFUNDED || this == PARTIALLY_REFUNDED;
    }

    public boolean isDispute() {
        return this == CHARGEBACK || this == CHARGEBACK_REVERSED;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == DECLINED || 
               this == CANCELLED || this == REFUNDED || this == CHARGEBACK_REVERSED;
    }

    public boolean canTransitionTo(TransactionStatus target) {
        return switch (this) {
            case PENDING -> target == AUTHORIZED || target == FAILED || target == DECLINED || 
                             target == CANCELLED || target == EXPIRED;
            case AUTHORIZED -> target == CAPTURED || target == REVERSED || target == EXPIRED || 
                               target == CANCELLED;
            case CAPTURED -> target == SETTLED || target == COMPLETED || target == REFUNDED || 
                             target == PARTIALLY_REFUNDED || target == CHARGEBACK;
            case SETTLED -> target == COMPLETED || target == REFUNDED || target == CHARGEBACK;
            case COMPLETED -> target == REFUNDED || target == CHARGEBACK;
            case REFUNDED, PARTIALLY_REFUNDED -> target == CHARGEBACK_REVERSED;
            case CHARGEBACK -> target == CHARGEBACK_REVERSED;
            case FAILED, DECLINED, CANCELLED, EXPIRED, REVERSED, CHARGEBACK_REVERSED -> false;
        };
    }
}
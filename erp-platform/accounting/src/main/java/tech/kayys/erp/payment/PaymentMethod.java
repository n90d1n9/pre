package tech.kayys.erp.payment;


/**
 * Methods of payment.
 */
public enum PaymentMethod {
    CASH("Cash"),
    CHECK("Check"),
    BANK_TRANSFER("Bank Transfer"),
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    WIRE_TRANSFER("Wire Transfer"),
    PAYPAL("PayPal"),
    STRIPE("Stripe"),
    OTHER("Other");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isElectronic() {
        return this == BANK_TRANSFER || this == CREDIT_CARD || 
               this == DEBIT_CARD || this == WIRE_TRANSFER ||
               this == PAYPAL || this == STRIPE;
    }
}
package tech.kayys.erp.transaction.domain.valueobject;

/**
 * Payment gateway providers.
 */
public enum GatewayProvider {
    STRIPE("Stripe"),
    ADYEN("Adyen"),
    BRAINTREE("Braintree"),
    SQUARE("Square"),
    PAYPAL("PayPal"),
    AUTHORIZE_NET("Authorize.Net"),
    WORLD_PAY("WorldPay"),
    CYBERSOURCE("CyberSource"),
    CHECKOUT_COM("Checkout.com"),
    RAZORPAY("Razorpay"),
    PAYU("PayU"),
    PAYTM("Paytm"),
    CASHFREE("Cashfree"),
    INSTAMOJO("Instamojo"),
    CUSTOM("Custom Gateway");

    private final String displayName;

    GatewayProvider(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean supportsSavedCards() {
        return this != PAYPAL && this != CUSTOM;
    }

    public boolean supportsRecurring() {
        return this == STRIPE || this == ADYEN || this == BRAINTREE || 
               this == AUTHORIZE_NET || this == CHECKOUT_COM;
    }

    public boolean supportsWebhooks() {
        return this != CUSTOM;
    }
}
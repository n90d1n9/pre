package tech.kayys.erp.kiosk.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Payment configuration for the kiosk.
 */
public final class PaymentConfig implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final List<PaymentMethod> acceptedMethods;
    private final boolean requireSignature;
    private final boolean requirePin;
    private final boolean tipEnabled;
    private final List<Double> tipPercentages;
    private final boolean allowCustomTip;
    private final String processorId;
    private final String merchantId;
    private final String terminalId;
    private final boolean emvEnabled;
    private final boolean contactlessEnabled;

    public PaymentConfig(
            List<PaymentMethod> acceptedMethods,
            boolean requireSignature,
            boolean requirePin,
            boolean tipEnabled,
            List<Double> tipPercentages,
            boolean allowCustomTip,
            String processorId,
            String merchantId,
            String terminalId,
            boolean emvEnabled,
            boolean contactlessEnabled) {
        this.acceptedMethods = acceptedMethods != null ? new ArrayList<>(acceptedMethods) : new ArrayList<>();
        this.requireSignature = requireSignature;
        this.requirePin = requirePin;
        this.tipEnabled = tipEnabled;
        this.tipPercentages = tipPercentages != null ? new ArrayList<>(tipPercentages) : new ArrayList<>();
        this.allowCustomTip = allowCustomTip;
        this.processorId = processorId;
        this.merchantId = merchantId;
        this.terminalId = terminalId;
        this.emvEnabled = emvEnabled;
        this.contactlessEnabled = contactlessEnabled;
        validate();
    }

    @Override
    public void validate() {
        if (acceptedMethods.isEmpty()) {
            throw new IllegalArgumentException("At least one payment method must be accepted");
        }
        if (tipEnabled && tipPercentages.isEmpty() && !allowCustomTip) {
            throw new IllegalArgumentException("If tips are enabled, either tip percentages or custom tip must be allowed");
        }
        if (processorId == null || processorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment processor ID cannot be empty");
        }
    }

    // Getters
    public List<PaymentMethod> getAcceptedMethods() { return Collections.unmodifiableList(acceptedMethods); }
    public boolean isRequireSignature() { return requireSignature; }
    public boolean isRequirePin() { return requirePin; }
    public boolean isTipEnabled() { return tipEnabled; }
    public List<Double> getTipPercentages() { return Collections.unmodifiableList(tipPercentages); }
    public boolean isAllowCustomTip() { return allowCustomTip; }
    public String getProcessorId() { return processorId; }
    public String getMerchantId() { return merchantId; }
    public String getTerminalId() { return terminalId; }
    public boolean isEmvEnabled() { return emvEnabled; }
    public boolean isContactlessEnabled() { return contactlessEnabled; }

    public boolean acceptsMethod(PaymentMethod method) {
        return acceptedMethods.contains(method);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentConfig that = (PaymentConfig) o;
        return Objects.equals(terminalId, that.terminalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(terminalId);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<PaymentMethod> acceptedMethods = new ArrayList<>();
        private boolean requireSignature = false;
        private boolean requirePin = true;
        private boolean tipEnabled = false;
        private List<Double> tipPercentages = new ArrayList<>();
        private boolean allowCustomTip = true;
        private String processorId;
        private String merchantId;
        private String terminalId;
        private boolean emvEnabled = true;
        private boolean contactlessEnabled = true;

        public Builder acceptedMethods(List<PaymentMethod> acceptedMethods) {
            this.acceptedMethods = new ArrayList<>(acceptedMethods);
            return this;
        }

        public Builder addPaymentMethod(PaymentMethod method) {
            this.acceptedMethods.add(method);
            return this;
        }

        public Builder requireSignature(boolean requireSignature) {
            this.requireSignature = requireSignature;
            return this;
        }

        public Builder requirePin(boolean requirePin) {
            this.requirePin = requirePin;
            return this;
        }

        public Builder tipEnabled(boolean tipEnabled) {
            this.tipEnabled = tipEnabled;
            return this;
        }

        public Builder tipPercentages(List<Double> tipPercentages) {
            this.tipPercentages = new ArrayList<>(tipPercentages);
            return this;
        }

        public Builder addTipPercentage(double tipPercentage) {
            this.tipPercentages.add(tipPercentage);
            return this;
        }

        public Builder allowCustomTip(boolean allowCustomTip) {
            this.allowCustomTip = allowCustomTip;
            return this;
        }

        public Builder processorId(String processorId) {
            this.processorId = processorId;
            return this;
        }

        public Builder merchantId(String merchantId) {
            this.merchantId = merchantId;
            return this;
        }

        public Builder terminalId(String terminalId) {
            this.terminalId = terminalId;
            return this;
        }

        public Builder emvEnabled(boolean emvEnabled) {
            this.emvEnabled = emvEnabled;
            return this;
        }

        public Builder contactlessEnabled(boolean contactlessEnabled) {
            this.contactlessEnabled = contactlessEnabled;
            return this;
        }

        public PaymentConfig build() {
            if (acceptedMethods.isEmpty()) {
                acceptedMethods.add(PaymentMethod.CREDIT_CARD);
                acceptedMethods.add(PaymentMethod.DEBIT_CARD);
                acceptedMethods.add(PaymentMethod.MOBILE);
            }
            if (tipEnabled && tipPercentages.isEmpty() && !allowCustomTip) {
                tipPercentages.add(10.0);
                tipPercentages.add(15.0);
                tipPercentages.add(20.0);
            }
            return new PaymentConfig(
                acceptedMethods, requireSignature, requirePin,
                tipEnabled, tipPercentages, allowCustomTip,
                processorId, merchantId, terminalId,
                emvEnabled, contactlessEnabled
            );
        }
    }

    /**
     * Payment methods accepted at kiosk.
     */
    public enum PaymentMethod {
        CREDIT_CARD("Credit Card"),
        DEBIT_CARD("Debit Card"),
        MOBILE("Mobile Payment"),
        GIFT_CARD("Gift Card"),
        LOYALTY_POINTS("Loyalty Points"),
        CASH("Cash"),
        CHECK("Check"),
        SNAP_EBT("SNAP/EBT");

        private final String displayName;

        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isCard() {
            return this == CREDIT_CARD || this == DEBIT_CARD || this == GIFT_CARD;
        }

        public boolean isDigital() {
            return this == MOBILE || this == LOYALTY_POINTS;
        }
    }
}
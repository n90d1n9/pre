package tech.kayys.erp.transaction.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.time.Instant;
import java.util.Objects;

/**
 * Payment instrument used for transactions.
 */
public final class PaymentInstrument implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String instrumentId;
    private final PaymentMethod method;
    private final String lastFourDigits;
    private final String cardType; // VISA, MASTERCARD, AMEX, DISCOVER
    private final String token;
    private final String bin;
    private final String expiryMonth;
    private final String expiryYear;
    private final String cardholderName;
    private final String maskedNumber;
    private final boolean isTokenized;
    private final boolean isNetworkTokenized;
    private final String networkTokenId;
    private final Instant tokenExpiry;
    private final String fingerprint;

    public PaymentInstrument(
            String instrumentId,
            PaymentMethod method,
            String lastFourDigits,
            String cardType,
            String token,
            String bin,
            String expiryMonth,
            String expiryYear,
            String cardholderName,
            String maskedNumber,
            boolean isTokenized,
            boolean isNetworkTokenized,
            String networkTokenId,
            Instant tokenExpiry,
            String fingerprint) {
        this.instrumentId = instrumentId;
        this.method = method;
        this.lastFourDigits = lastFourDigits;
        this.cardType = cardType;
        this.token = token;
        this.bin = bin;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.cardholderName = cardholderName;
        this.maskedNumber = maskedNumber;
        this.isTokenized = isTokenized;
        this.isNetworkTokenized = isNetworkTokenized;
        this.networkTokenId = networkTokenId;
        this.tokenExpiry = tokenExpiry;
        this.fingerprint = fingerprint;
        validate();
    }

    @Override
    public void validate() {
        if (instrumentId == null || instrumentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Instrument ID cannot be empty");
        }
        if (method == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
    }

    // Getters
    public String getInstrumentId() { return instrumentId; }
    public PaymentMethod getMethod() { return method; }
    public String getLastFourDigits() { return lastFourDigits; }
    public String getCardType() { return cardType; }
    public String getToken() { return token; }
    public String getBin() { return bin; }
    public String getExpiryMonth() { return expiryMonth; }
    public String getExpiryYear() { return expiryYear; }
    public String getCardholderName() { return cardholderName; }
    public String getMaskedNumber() { return maskedNumber; }
    public boolean isTokenized() { return isTokenized; }
    public boolean isNetworkTokenized() { return isNetworkTokenized; }
    public String getNetworkTokenId() { return networkTokenId; }
    public Instant getTokenExpiry() { return tokenExpiry; }
    public String getFingerprint() { return fingerprint; }

    public String getMaskedDisplay() {
        if (maskedNumber != null) {
            return maskedNumber;
        }
        if (lastFourDigits != null && method == PaymentMethod.CREDIT_CARD) {
            return "•••• •••• •••• " + lastFourDigits;
        }
        return method.getDisplayName();
    }

    public boolean isCard() {
        return method == PaymentMethod.CREDIT_CARD || method == PaymentMethod.DEBIT_CARD;
    }

    public boolean isTokenValid() {
        return isTokenized && tokenExpiry != null && Instant.now().isBefore(tokenExpiry);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentInstrument that = (PaymentInstrument) o;
        return Objects.equals(instrumentId, that.instrumentId) ||
               Objects.equals(fingerprint, that.fingerprint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instrumentId, fingerprint);
    }

    @Override
    public String toString() {
        return "PaymentInstrument{" +
                "instrumentId='" + instrumentId + '\'' +
                ", method=" + method +
                ", masked=" + getMaskedDisplay() +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String instrumentId;
        private PaymentMethod method;
        private String lastFourDigits;
        private String cardType;
        private String token;
        private String bin;
        private String expiryMonth;
        private String expiryYear;
        private String cardholderName;
        private String maskedNumber;
        private boolean isTokenized = false;
        private boolean isNetworkTokenized = false;
        private String networkTokenId;
        private Instant tokenExpiry;
        private String fingerprint;

        public Builder instrumentId(String instrumentId) {
            this.instrumentId = instrumentId;
            return this;
        }

        public Builder method(PaymentMethod method) {
            this.method = method;
            return this;
        }

        public Builder lastFourDigits(String lastFourDigits) {
            this.lastFourDigits = lastFourDigits;
            return this;
        }

        public Builder cardType(String cardType) {
            this.cardType = cardType;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder bin(String bin) {
            this.bin = bin;
            return this;
        }

        public Builder expiryMonth(String expiryMonth) {
            this.expiryMonth = expiryMonth;
            return this;
        }

        public Builder expiryYear(String expiryYear) {
            this.expiryYear = expiryYear;
            return this;
        }

        public Builder cardholderName(String cardholderName) {
            this.cardholderName = cardholderName;
            return this;
        }

        public Builder maskedNumber(String maskedNumber) {
            this.maskedNumber = maskedNumber;
            return this;
        }

        public Builder isTokenized(boolean isTokenized) {
            this.isTokenized = isTokenized;
            return this;
        }

        public Builder isNetworkTokenized(boolean isNetworkTokenized) {
            this.isNetworkTokenized = isNetworkTokenized;
            return this;
        }

        public Builder networkTokenId(String networkTokenId) {
            this.networkTokenId = networkTokenId;
            return this;
        }

        public Builder tokenExpiry(Instant tokenExpiry) {
            this.tokenExpiry = tokenExpiry;
            return this;
        }

        public Builder fingerprint(String fingerprint) {
            this.fingerprint = fingerprint;
            return this;
        }

        public PaymentInstrument build() {
            if (instrumentId == null) {
                instrumentId = UUID.randomUUID().toString();
            }
            if (isCard() && lastFourDigits == null && maskedNumber == null) {
                throw new IllegalStateException("Card instruments require last four digits or masked number");
            }
            return new PaymentInstrument(
                instrumentId, method, lastFourDigits, cardType, token, bin,
                expiryMonth, expiryYear, cardholderName, maskedNumber,
                isTokenized, isNetworkTokenized, networkTokenId,
                tokenExpiry, fingerprint
            );
        }
    }

    public enum PaymentMethod {
        CREDIT_CARD("Credit Card"),
        DEBIT_CARD("Debit Card"),
        GIFT_CARD("Gift Card"),
        MOBILE_WALLET("Mobile Wallet"),
        PAYPAL("PayPal"),
        APPLE_PAY("Apple Pay"),
        GOOGLE_PAY("Google Pay"),
        SAMSUNG_PAY("Samsung Pay"),
        CASH("Cash"),
        CHECK("Check"),
        BANK_TRANSFER("Bank Transfer"),
        ACH("ACH"),
        SNAP_EBT("SNAP/EBT"),
        LOYALTY_POINTS("Loyalty Points"),
        CRYPTO("Cryptocurrency");

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
            return this == MOBILE_WALLET || this == PAYPAL || this == APPLE_PAY ||
                   this == GOOGLE_PAY || this == SAMSUNG_PAY;
        }

        public boolean isCashLike() {
            return this == CASH || this == CHECK;
        }
    }
}
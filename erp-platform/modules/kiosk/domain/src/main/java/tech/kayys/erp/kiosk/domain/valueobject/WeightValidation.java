package tech.kayys.erp.kiosk.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;
import tech.kayys.erp.groceries.domain.valueobject.Weight;

import java.time.Instant;
import java.util.Objects;

/**
 * Weight validation result for self-checkout.
 */
public final class WeightValidation implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String productId;
    private final Weight scannedWeight;
    private final Weight actualWeight;
    private final double tolerancePercent;
    private final boolean validated;
    private final String status; // PASSED, FAILED, MANUAL_REVIEW
    private final Instant validationTime;
    private final String validationMessage;

    public WeightValidation(
            String productId,
            Weight scannedWeight,
            Weight actualWeight,
            double tolerancePercent,
            boolean validated,
            String status,
            Instant validationTime,
            String validationMessage) {
        this.productId = productId;
        this.scannedWeight = scannedWeight;
        this.actualWeight = actualWeight;
        this.tolerancePercent = tolerancePercent;
        this.validated = validated;
        this.status = status;
        this.validationTime = validationTime != null ? validationTime : Instant.now();
        this.validationMessage = validationMessage;
        validate();
    }

    @Override
    public void validate() {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        if (scannedWeight == null) {
            throw new IllegalArgumentException("Scanned weight cannot be null");
        }
        if (tolerancePercent < 0 || tolerancePercent > 100) {
            throw new IllegalArgumentException("Tolerance must be between 0 and 100");
        }
    }

    // Getters
    public String getProductId() { return productId; }
    public Weight getScannedWeight() { return scannedWeight; }
    public Weight getActualWeight() { return actualWeight; }
    public double getTolerancePercent() { return tolerancePercent; }
    public boolean isValidated() { return validated; }
    public String getStatus() { return status; }
    public Instant getValidationTime() { return validationTime; }
    public String getValidationMessage() { return validationMessage; }

    public double getWeightDifference() {
        if (actualWeight == null || scannedWeight == null) {
            return 0.0;
        }
        return actualWeight.toGrams().doubleValue() - scannedWeight.toGrams().doubleValue();
    }

    public double getWeightDifferencePercent() {
        if (scannedWeight == null || scannedWeight.isZero()) {
            return 0.0;
        }
        double diff = getWeightDifference();
        return (diff / scannedWeight.toGrams().doubleValue()) * 100.0;
    }

    public boolean isWithinTolerance() {
        return Math.abs(getWeightDifferencePercent()) <= tolerancePercent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeightValidation that = (WeightValidation) o;
        return Objects.equals(productId, that.productId) &&
               Objects.equals(validationTime, that.validationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, validationTime);
    }

    @Override
    public String toString() {
        return "WeightValidation{" +
                "productId='" + productId + '\'' +
                ", validated=" + validated +
                ", status='" + status + '\'' +
                ", weightDiff=" + getWeightDifferencePercent() + "%" +
                '}';
    }

    public static WeightValidation success(String productId, Weight scannedWeight, Weight actualWeight) {
        return new WeightValidation(
            productId, scannedWeight, actualWeight, 5.0,
            true, "PASSED", Instant.now(),
            "Weight validation passed"
        );
    }

    public static WeightValidation failure(String productId, Weight scannedWeight, Weight actualWeight, String message) {
        return new WeightValidation(
            productId, scannedWeight, actualWeight, 5.0,
            false, "FAILED", Instant.now(),
            message
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String productId;
        private Weight scannedWeight;
        private Weight actualWeight;
        private double tolerancePercent = 5.0;
        private boolean validated;
        private String status;
        private Instant validationTime;
        private String validationMessage;

        public Builder productId(String productId) {
            this.productId = productId;
            return this;
        }

        public Builder scannedWeight(Weight scannedWeight) {
            this.scannedWeight = scannedWeight;
            return this;
        }

        public Builder actualWeight(Weight actualWeight) {
            this.actualWeight = actualWeight;
            return this;
        }

        public Builder tolerancePercent(double tolerancePercent) {
            this.tolerancePercent = tolerancePercent;
            return this;
        }

        public Builder validated(boolean validated) {
            this.validated = validated;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder validationTime(Instant validationTime) {
            this.validationTime = validationTime;
            return this;
        }

        public Builder validationMessage(String validationMessage) {
            this.validationMessage = validationMessage;
            return this;
        }

        public WeightValidation build() {
            return new WeightValidation(
                productId, scannedWeight, actualWeight,
                tolerancePercent, validated, status,
                validationTime, validationMessage
            );
        }
    }
}
package tech.kayys.erp.kiosk.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.time.Instant;
import java.util.Objects;

/**
 * Age verification result for restricted items.
 */
public final class AgeVerificationResult implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final boolean verified;
    private final int age;
    private final String idType; // DRIVERS_LICENSE, PASSPORT, ID_CARD
    private final String idNumber;
    private final String scannedData;
    private final Instant verificationTime;
    private final String verifiedBy;
    private final String reason;

    public AgeVerificationResult(
            boolean verified,
            int age,
            String idType,
            String idNumber,
            String scannedData,
            Instant verificationTime,
            String verifiedBy,
            String reason) {
        this.verified = verified;
        this.age = age;
        this.idType = idType;
        this.idNumber = idNumber;
        this.scannedData = scannedData;
        this.verificationTime = verificationTime != null ? verificationTime : Instant.now();
        this.verifiedBy = verifiedBy;
        this.reason = reason;
        validate();
    }

    @Override
    public void validate() {
        if (verified && age < 0) {
            throw new IllegalArgumentException("Age cannot be negative for verified results");
        }
        if (verified && idType == null) {
            throw new IllegalArgumentException("ID type is required for verified results");
        }
    }

    // Getters
    public boolean isVerified() { return verified; }
    public int getAge() { return age; }
    public String getIdType() { return idType; }
    public String getIdNumber() { return idNumber; }
    public String getScannedData() { return scannedData; }
    public Instant getVerificationTime() { return verificationTime; }
    public String getVerifiedBy() { return verifiedBy; }
    public String getReason() { return reason; }

    public boolean meetsAgeRequirement(int requiredAge) {
        return verified && age >= requiredAge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgeVerificationResult that = (AgeVerificationResult) o;
        return Objects.equals(idNumber, that.idNumber) &&
               Objects.equals(verificationTime, that.verificationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idNumber, verificationTime);
    }

    @Override
    public String toString() {
        return "AgeVerificationResult{" +
                "verified=" + verified +
                ", age=" + age +
                ", idType='" + idType + '\'' +
                '}';
    }

    public static AgeVerificationResult success(int age, String idType, String idNumber, String scannedData) {
        return new AgeVerificationResult(
            true, age, idType, idNumber, scannedData,
            Instant.now(), null, null
        );
    }

    public static AgeVerificationResult failure(String reason) {
        return new AgeVerificationResult(
            false, -1, null, null, null,
            Instant.now(), null, reason
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean verified;
        private int age;
        private String idType;
        private String idNumber;
        private String scannedData;
        private Instant verificationTime;
        private String verifiedBy;
        private String reason;

        public Builder verified(boolean verified) {
            this.verified = verified;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder idType(String idType) {
            this.idType = idType;
            return this;
        }

        public Builder idNumber(String idNumber) {
            this.idNumber = idNumber;
            return this;
        }

        public Builder scannedData(String scannedData) {
            this.scannedData = scannedData;
            return this;
        }

        public Builder verificationTime(Instant verificationTime) {
            this.verificationTime = verificationTime;
            return this;
        }

        public Builder verifiedBy(String verifiedBy) {
            this.verifiedBy = verifiedBy;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public AgeVerificationResult build() {
            return new AgeVerificationResult(
                verified, age, idType, idNumber, scannedData,
                verificationTime, verifiedBy, reason
            );
        }
    }
}
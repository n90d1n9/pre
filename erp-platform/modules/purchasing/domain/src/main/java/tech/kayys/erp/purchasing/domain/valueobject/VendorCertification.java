package tech.kayys.erp.purchasing.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.time.Instant;
import java.util.Objects;

/**
 * Vendor certification value object.
 */
public final class VendorCertification implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String certificationName;
    private final String certificationNumber;
    private final String issuingAuthority;
    private final Instant issueDate;
    private final Instant expiryDate;
    private final boolean verified;

    public VendorCertification(
            String certificationName,
            String certificationNumber,
            String issuingAuthority,
            Instant issueDate,
            Instant expiryDate,
            boolean verified) {
        this.certificationName = certificationName;
        this.certificationNumber = certificationNumber;
        this.issuingAuthority = issuingAuthority;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.verified = verified;
        validate();
    }

    @Override
    public void validate() {
        if (certificationName == null || certificationName.trim().isEmpty()) {
            throw new IllegalArgumentException("Certification name cannot be empty");
        }
        if (issuingAuthority == null || issuingAuthority.trim().isEmpty()) {
            throw new IllegalArgumentException("Issuing authority cannot be empty");
        }
        if (issueDate == null) {
            throw new IllegalArgumentException("Issue date cannot be null");
        }
        if (expiryDate != null && expiryDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("Expiry date must be after issue date");
        }
    }

    public String getCertificationName() { return certificationName; }
    public String getCertificationNumber() { return certificationNumber; }
    public String getIssuingAuthority() { return issuingAuthority; }
    public Instant getIssueDate() { return issueDate; }
    public Instant getExpiryDate() { return expiryDate; }
    public boolean isVerified() { return verified; }

    public boolean isValid() {
        if (!verified) return false;
        if (expiryDate == null) return true;
        return Instant.now().isBefore(expiryDate);
    }

    public boolean isExpiringSoon(int daysThreshold) {
        if (expiryDate == null) return false;
        Instant threshold = Instant.now().plusSeconds(daysThreshold * 24L * 60L * 60L);
        return expiryDate.isBefore(threshold);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorCertification that = (VendorCertification) o;
        return Objects.equals(certificationNumber, that.certificationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificationNumber);
    }

    @Override
    public String toString() {
        return "VendorCertification{" +
                "certificationName='" + certificationName + '\'' +
                ", issuingAuthority='" + issuingAuthority + '\'' +
                ", expiryDate=" + expiryDate +
                ", verified=" + verified +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String certificationName;
        private String certificationNumber;
        private String issuingAuthority;
        private Instant issueDate;
        private Instant expiryDate;
        private boolean verified = false;

        public Builder certificationName(String certificationName) {
            this.certificationName = certificationName;
            return this;
        }

        public Builder certificationNumber(String certificationNumber) {
            this.certificationNumber = certificationNumber;
            return this;
        }

        public Builder issuingAuthority(String issuingAuthority) {
            this.issuingAuthority = issuingAuthority;
            return this;
        }

        public Builder issueDate(Instant issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        public Builder expiryDate(Instant expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder verified(boolean verified) {
            this.verified = verified;
            return this;
        }

        public VendorCertification build() {
            if (issueDate == null) {
                issueDate = Instant.now();
            }
            return new VendorCertification(
                certificationName, certificationNumber, issuingAuthority,
                issueDate, expiryDate, verified
            );
        }
    }
}
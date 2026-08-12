package tech.kayys.erp.transaction.domain.model;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.time.Instant;
import java.util.Objects;

/**
 * 3D Secure authentication value object.
 * Handles SCA (Strong Customer Authentication) requirements.
 */
public final class ThreeDSecureAuthentication implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String authenticationId;
    private final String transactionId;
    private final String enrollmentStatus; // Y, N, U
    private final String authenticationStatus; // Y, A, N, U, R
    private final String eciIndicator;
    private final String cavv; // Cardholder Authentication Verification Value
    private final String xid; // Transaction Identifier
    private final String dsTransactionId;
    private final String threeDSVersion;
    private final String challengeStatus;
    private final boolean authenticated;
    private final Instant authenticationTime;
    private final String authenticationMethod;
    private final String browserInfo;
    private final String failureReason;

    public ThreeDSecureAuthentication(
            String authenticationId,
            String transactionId,
            String enrollmentStatus,
            String authenticationStatus,
            String eciIndicator,
            String cavv,
            String xid,
            String dsTransactionId,
            String threeDSVersion,
            String challengeStatus,
            boolean authenticated,
            Instant authenticationTime,
            String authenticationMethod,
            String browserInfo,
            String failureReason) {
        this.authenticationId = authenticationId;
        this.transactionId = transactionId;
        this.enrollmentStatus = enrollmentStatus;
        this.authenticationStatus = authenticationStatus;
        this.eciIndicator = eciIndicator;
        this.cavv = cavv;
        this.xid = xid;
        this.dsTransactionId = dsTransactionId;
        this.threeDSVersion = threeDSVersion;
        this.challengeStatus = challengeStatus;
        this.authenticated = authenticated;
        this.authenticationTime = authenticationTime != null ? authenticationTime : Instant.now();
        this.authenticationMethod = authenticationMethod;
        this.browserInfo = browserInfo;
        this.failureReason = failureReason;
        validate();
    }

    @Override
    public void validate() {
        if (authenticationId == null || authenticationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Authentication ID cannot be empty");
        }
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be empty");
        }
    }

    // Getters
    public String getAuthenticationId() { return authenticationId; }
    public String getTransactionId() { return transactionId; }
    public String getEnrollmentStatus() { return enrollmentStatus; }
    public String getAuthenticationStatus() { return authenticationStatus; }
    public String getEciIndicator() { return eciIndicator; }
    public String getCavv() { return cavv; }
    public String getXid() { return xid; }
    public String getDsTransactionId() { return dsTransactionId; }
    public String getThreeDSVersion() { return threeDSVersion; }
    public String getChallengeStatus() { return challengeStatus; }
    public boolean isAuthenticated() { return authenticated; }
    public Instant getAuthenticationTime() { return authenticationTime; }
    public String getAuthenticationMethod() { return authenticationMethod; }
    public String getBrowserInfo() { return browserInfo; }
    public String getFailureReason() { return failureReason; }

    public boolean isEnrolled() {
        return "Y".equals(enrollmentStatus);
    }

    public boolean isSuccessful() {
        return authenticated && "Y".equals(authenticationStatus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThreeDSecureAuthentication that = (ThreeDSecureAuthentication) o;
        return Objects.equals(authenticationId, that.authenticationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authenticationId);
    }

    @Override
    public String toString() {
        return "ThreeDSecureAuthentication{" +
                "authenticationId='" + authenticationId + '\'' +
                ", authenticated=" + authenticated +
                ", threeDSVersion='" + threeDSVersion + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String authenticationId;
        private String transactionId;
        private String enrollmentStatus = "U";
        private String authenticationStatus = "U";
        private String eciIndicator;
        private String cavv;
        private String xid;
        private String dsTransactionId;
        private String threeDSVersion = "2.2.0";
        private String challengeStatus;
        private boolean authenticated = false;
        private Instant authenticationTime;
        private String authenticationMethod;
        private String browserInfo;
        private String failureReason;

        public Builder authenticationId(String authenticationId) {
            this.authenticationId = authenticationId;
            return this;
        }

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder enrollmentStatus(String enrollmentStatus) {
            this.enrollmentStatus = enrollmentStatus;
            return this;
        }

        public Builder authenticationStatus(String authenticationStatus) {
            this.authenticationStatus = authenticationStatus;
            return this;
        }

        public Builder eciIndicator(String eciIndicator) {
            this.eciIndicator = eciIndicator;
            return this;
        }

        public Builder cavv(String cavv) {
            this.cavv = cavv;
            return this;
        }

        public Builder xid(String xid) {
            this.xid = xid;
            return this;
        }

        public Builder dsTransactionId(String dsTransactionId) {
            this.dsTransactionId = dsTransactionId;
            return this;
        }

        public Builder threeDSVersion(String threeDSVersion) {
            this.threeDSVersion = threeDSVersion;
            return this;
        }

        public Builder challengeStatus(String challengeStatus) {
            this.challengeStatus = challengeStatus;
            return this;
        }

        public Builder authenticated(boolean authenticated) {
            this.authenticated = authenticated;
            return this;
        }

        public Builder authenticationTime(Instant authenticationTime) {
            this.authenticationTime = authenticationTime;
            return this;
        }

        public Builder authenticationMethod(String authenticationMethod) {
            this.authenticationMethod = authenticationMethod;
            return this;
        }

        public Builder browserInfo(String browserInfo) {
            this.browserInfo = browserInfo;
            return this;
        }

        public Builder failureReason(String failureReason) {
            this.failureReason = failureReason;
            return this;
        }

        public ThreeDSecureAuthentication build() {
            if (authenticationId == null) {
                authenticationId = UUID.randomUUID().toString();
            }
            return new ThreeDSecureAuthentication(
                authenticationId, transactionId, enrollmentStatus,
                authenticationStatus, eciIndicator, cavv, xid,
                dsTransactionId, threeDSVersion, challengeStatus,
                authenticated, authenticationTime, authenticationMethod,
                browserInfo, failureReason
            );
        }
    }
}
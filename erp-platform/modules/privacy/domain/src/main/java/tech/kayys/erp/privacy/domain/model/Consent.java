package tech.kayys.erp.privacy.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.privacy.domain.identifier.ConsentId;

import java.time.Instant;

/**
 * Consent aggregate root.
 * Manages user consent for data processing.
 */
public final class Consent extends AggregateRoot<ConsentId> {
    
    private static final long serialVersionUID = 1L;
    
    private String userId;
    private String purpose;
    private boolean granted;
    private Instant grantedAt;
    private Instant revokedAt;
    private String ipAddress;
    private String userAgent;
    private String notes;
    private boolean active;

    private Consent(ConsentId id) {
        super(id);
        this.active = true;
        this.granted = false;
    }

    private Consent() {
        super();
    }

    /**
     * Factory method to create a new consent record.
     */
    public static Consent create(
            ConsentId id,
            String userId,
            String purpose,
            boolean granted,
            String ipAddress,
            String userAgent) {
        Consent consent = new Consent(id);
        consent.userId = userId;
        consent.purpose = purpose;
        consent.granted = granted;
        consent.ipAddress = ipAddress;
        consent.userAgent = userAgent;
        if (granted) {
            consent.grantedAt = Instant.now();
        }
        return consent;
    }

    /**
     * Grants consent.
     */
    public void grant() {
        this.granted = true;
        this.grantedAt = Instant.now();
        this.revokedAt = null;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Revokes consent.
     */
    public void revoke() {
        this.granted = false;
        this.revokedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the consent record.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getUserId() { return userId; }
    public String getPurpose() { return purpose; }
    public boolean isGranted() { return granted; }
    public Instant getGrantedAt() { return grantedAt; }
    public Instant getRevokedAt() { return revokedAt; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Consent{" +
                "id=" + getId() +
                ", userId='" + userId + '\'' +
                ", purpose='" + purpose + '\'' +
                ", granted=" + granted +
                ", grantedAt=" + grantedAt +
                '}';
    }
}
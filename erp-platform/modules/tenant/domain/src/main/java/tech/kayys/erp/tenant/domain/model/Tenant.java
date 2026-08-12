package tech.kayys.erp.tenant.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.tenant.domain.identifier.TenantId;
import tech.kayys.erp.tenant.domain.valueobject.PlanType;
import tech.kayys.erp.tenant.domain.valueobject.TenantStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tenant aggregate root.
 * Represents a tenant in the multi-tenant system.
 */
public final class Tenant extends AggregateRoot<TenantId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String subdomain;
    private String domain;
    private TenantStatus status;
    private PlanType plan;
    private Instant subscriptionStart;
    private Instant subscriptionEnd;
    private int maxUsers;
    private int maxCompanies;
    private List<String> features;
    private String timezone;
    private String language;
    private String currencyCode;
    private String address;
    private String city;
    private String country;
    private String contactEmail;
    private String contactPhone;
    private String notes;
    private boolean active;
    private String createdBy;
    private String lastModifiedBy;

    private Tenant(TenantId id) {
        super(id);
        this.status = TenantStatus.PENDING;
        this.features = new ArrayList<>();
        this.active = true;
        this.maxUsers = 10;
        this.maxCompanies = 1;
        this.language = "en";
    }

    private Tenant() {
        super();
    }

    /**
     * Factory method to create a new tenant.
     */
    public static Tenant create(
            TenantId id,
            String name,
            String subdomain,
            PlanType plan,
            String contactEmail) {
        Tenant tenant = new Tenant(id);
        tenant.name = name;
        tenant.subdomain = subdomain;
        tenant.plan = plan;
        tenant.contactEmail = contactEmail;
        tenant.maxUsers = plan.getMaxUsers();
        tenant.maxCompanies = plan.getMaxCompanies();
        tenant.status = TenantStatus.PENDING;
        return tenant;
    }

    /**
     * Activates the tenant.
     */
    public void activate() {
        if (status != TenantStatus.PENDING && status != TenantStatus.INACTIVE) {
            throw new IllegalStateException("Cannot activate tenant in status: " + status);
        }
        this.status = TenantStatus.ACTIVE;
        this.active = true;
        this.subscriptionStart = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Suspends the tenant.
     */
    public void suspend(String reason) {
        if (status != TenantStatus.ACTIVE) {
            throw new IllegalStateException("Cannot suspend tenant in status: " + status);
        }
        this.status = TenantStatus.SUSPENDED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Reactivates the tenant.
     */
    public void reactivate() {
        if (status != TenantStatus.SUSPENDED && status != TenantStatus.EXPIRED) {
            throw new IllegalStateException("Cannot reactivate tenant in status: " + status);
        }
        this.status = TenantStatus.ACTIVE;
        this.active = true;
        this.subscriptionStart = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the tenant.
     */
    public void deactivate() {
        if (status == TenantStatus.DELETED) {
            throw new IllegalStateException("Tenant is already deleted");
        }
        this.status = TenantStatus.INACTIVE;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the tenant's subscription plan.
     */
    public void updatePlan(PlanType newPlan) {
        if (status == TenantStatus.DELETED) {
            throw new IllegalStateException("Cannot update deleted tenant");
        }
        this.plan = newPlan;
        this.maxUsers = newPlan.getMaxUsers();
        this.maxCompanies = newPlan.getMaxCompanies();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a feature to the tenant.
     */
    public void addFeature(String feature) {
        if (!features.contains(feature)) {
            features.add(feature);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a feature from the tenant.
     */
    public void removeFeature(String feature) {
        features.remove(feature);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if the tenant can add more companies.
     */
    public boolean canAddCompany() {
        // This would be checked against actual company count
        return true; // Placeholder
    }

    /**
     * Checks if the tenant can add more users.
     */
    public boolean canAddUser() {
        // This would be checked against actual user count
        return true; // Placeholder
    }

    /**
     * Checks if the subscription is expired.
     */
    public boolean isSubscriptionExpired() {
        if (subscriptionEnd == null) {
            return false;
        }
        return Instant.now().isAfter(subscriptionEnd);
    }

    /**
     * Extends the subscription.
     */
    public void extendSubscription(int days, String reason) {
        if (subscriptionEnd == null) {
            this.subscriptionEnd = Instant.now().plusSeconds(days * 24L * 60L * 60L);
        } else {
            this.subscriptionEnd = subscriptionEnd.plusSeconds(days * 24L * 60L * 60L);
        }
        if (this.status == TenantStatus.EXPIRED) {
            this.status = TenantStatus.ACTIVE;
            this.active = true;
        }
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the subscription period.
     */
    public void setSubscriptionPeriod(Instant start, Instant end) {
        this.subscriptionStart = start;
        this.subscriptionEnd = end;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getName() { return name; }
    public String getSubdomain() { return subdomain; }
    public String getDomain() { return domain; }
    public TenantStatus getStatus() { return status; }
    public PlanType getPlan() { return plan; }
    public Instant getSubscriptionStart() { return subscriptionStart; }
    public Instant getSubscriptionEnd() { return subscriptionEnd; }
    public int getMaxUsers() { return maxUsers; }
    public int getMaxCompanies() { return maxCompanies; }
    public List<String> getFeatures() { return Collections.unmodifiableList(features); }
    public String getTimezone() { return timezone; }
    public String getLanguage() { return language; }
    public String getCurrencyCode() { return currencyCode; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public String getContactEmail() { return contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active && status == TenantStatus.ACTIVE; }
    public String getCreatedBy() { return createdBy; }
    public String getLastModifiedBy() { return lastModifiedBy; }

    public void setDomain(String domain) {
        this.domain = domain;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setLanguage(String language) {
        this.language = language;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setAddress(String address) {
        this.address = address;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCity(String city) {
        this.city = city;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCountry(String country) {
        this.country = country;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", subdomain='" + subdomain + '\'' +
                ", status=" + status +
                ", plan=" + plan +
                '}';
    }
}
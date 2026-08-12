package tech.kayys.erp.crm.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.crm.domain.identifier.CustomerPortalUserId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Customer portal user aggregate root.
 * Represents a customer's portal access and preferences.
 */
public final class CustomerPortalUser extends AggregateRoot<CustomerPortalUserId> {
    
    private static final long serialVersionUID = 1L;
    
    private String customerId;
    private String customerName;
    private String email;
    private String username;
    private String passwordHash;
    private boolean emailVerified;
    private boolean active;
    private List<String> preferences;
    private List<String> recentTickets;
    private List<String> savedSearches;
    private String lastLoginAt;
    private String notes;

    private CustomerPortalUser(CustomerPortalUserId id) {
        super(id);
        this.preferences = new ArrayList<>();
        this.recentTickets = new ArrayList<>();
        this.savedSearches = new ArrayList<>();
        this.active = true;
        this.emailVerified = false;
    }

    private CustomerPortalUser() {
        super();
    }

    /**
     * Factory method to create a new portal user.
     */
    public static CustomerPortalUser create(
            CustomerPortalUserId id,
            String customerId,
            String customerName,
            String email,
            String username) {
        CustomerPortalUser user = new CustomerPortalUser(id);
        user.customerId = customerId;
        user.customerName = customerName;
        user.email = email;
        user.username = username;
        return user;
    }

    /**
     * Verifies the user's email.
     */
    public void verifyEmail() {
        this.emailVerified = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records a login.
     */
    public void recordLogin() {
        this.lastLoginAt = Instant.now().toString();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the password.
     */
    public void updatePassword(String passwordHash) {
        this.passwordHash = passwordHash;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a recent ticket.
     */
    public void addRecentTicket(String ticketId) {
        // Keep only last 10 tickets
        recentTickets.remove(ticketId);
        recentTickets.add(0, ticketId);
        if (recentTickets.size() > 10) {
            recentTickets = recentTickets.subList(0, 10);
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a saved search.
     */
    public void addSavedSearch(String search) {
        if (!savedSearches.contains(search)) {
            savedSearches.add(search);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a saved search.
     */
    public void removeSavedSearch(String search) {
        savedSearches.remove(search);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the portal user.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isEmailVerified() { return emailVerified; }
    public boolean isActive() { return active; }
    public List<String> getPreferences() { return Collections.unmodifiableList(preferences); }
    public List<String> getRecentTickets() { return Collections.unmodifiableList(recentTickets); }
    public List<String> getSavedSearches() { return Collections.unmodifiableList(savedSearches); }
    public String getLastLoginAt() { return lastLoginAt; }
    public String getNotes() { return notes; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "CustomerPortalUser{" +
                "id=" + getId() +
                ", customerName='" + customerName + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                '}';
    }
}
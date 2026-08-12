package tech.kayys.erp.tenant.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.tenant.domain.identifier.CompanyId;
import tech.kayys.erp.tenant.domain.identifier.TenantId;
import tech.kayys.erp.tenant.domain.identifier.UserId;
import tech.kayys.erp.tenant.domain.valueobject.RoleType;
import tech.kayys.erp.tenant.domain.valueobject.UserStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User aggregate root.
 * Represents a user within a tenant.
 */
public final class User extends AggregateRoot<UserId> {
    
    private static final long serialVersionUID = 1L;
    
    private TenantId tenantId;
    private CompanyId companyId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<RoleType> roles;
    private UserStatus status;
    private String language;
    private String timezone;
    private boolean twoFactorEnabled;
    private Instant lastLoginAt;
    private int failedLoginAttempts;
    private String notes;
    private boolean active;

    private User(UserId id) {
        super(id);
        this.roles = new ArrayList<>();
        this.status = UserStatus.PENDING;
        this.active = true;
        this.failedLoginAttempts = 0;
        this.twoFactorEnabled = false;
    }

    private User() {
        super();
    }

    /**
     * Factory method to create a new user.
     */
    public static User create(
            UserId id,
            TenantId tenantId,
            String username,
            String email,
            String firstName,
            String lastName) {
        User user = new User(id);
        user.tenantId = tenantId;
        user.username = username;
        user.email = email;
        user.firstName = firstName;
        user.lastName = lastName;
        user.status = UserStatus.PENDING;
        return user;
    }

    /**
     * Activates the user.
     */
    public void activate() {
        if (status != UserStatus.PENDING && status != UserStatus.INACTIVE) {
            throw new IllegalStateException("Cannot activate user in status: " + status);
        }
        this.status = UserStatus.ACTIVE;
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the user.
     */
    public void deactivate() {
        if (status == UserStatus.DELETED) {
            throw new IllegalStateException("User is already deleted");
        }
        this.status = UserStatus.INACTIVE;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Suspends the user.
     */
    public void suspend(String reason) {
        if (status == UserStatus.DELETED) {
            throw new IllegalStateException("Cannot suspend deleted user");
        }
        this.status = UserStatus.SUSPENDED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Locks the user account.
     */
    public void lock(String reason) {
        if (status == UserStatus.DELETED) {
            throw new IllegalStateException("Cannot lock deleted user");
        }
        this.status = UserStatus.LOCKED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Unlocks the user account.
     */
    public void unlock() {
        if (status != UserStatus.LOCKED) {
            throw new IllegalStateException("User is not locked");
        }
        this.status = UserStatus.ACTIVE;
        this.active = true;
        this.failedLoginAttempts = 0;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a role to the user.
     */
    public void addRole(RoleType role) {
        if (!roles.contains(role)) {
            roles.add(role);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a role from the user.
     */
    public void removeRole(RoleType role) {
        roles.remove(role);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if the user has a specific role.
     */
    public boolean hasRole(RoleType role) {
        return roles.contains(role);
    }

    /**
     * Checks if the user has admin access.
     */
    public boolean isAdmin() {
        return roles.stream().anyMatch(RoleType::isAdmin);
    }

    /**
     * Records a successful login.
     */
    public void recordLogin() {
        this.lastLoginAt = Instant.now();
        this.failedLoginAttempts = 0;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records a failed login attempt.
     */
    public void recordFailedLogin() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.status = UserStatus.LOCKED;
            this.active = false;
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the company for the user.
     */
    public void setCompany(CompanyId companyId) {
        this.companyId = companyId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the user's full name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters
    public TenantId getTenantId() { return tenantId; }
    public CompanyId getCompanyId() { return companyId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public List<RoleType> getRoles() { return Collections.unmodifiableList(roles); }
    public UserStatus getStatus() { return status; }
    public String getLanguage() { return language; }
    public String getTimezone() { return timezone; }
    public boolean isTwoFactorEnabled() { return twoFactorEnabled; }
    public Instant getLastLoginAt() { return lastLoginAt; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active && status == UserStatus.ACTIVE; }

    public void setLanguage(String language) {
        this.language = language;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", status=" + status +
                ", roles=" + roles +
                '}';
    }
}
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <module>modules/catalog/domain</module>
    <module>modules/catalog/application</module>
    <module>modules/catalog/infrastructure</module>
    <module>modules/catalog/interfaces</module>

    <module>modules/sales/domain</module>
    <module>modules/sales/application</module>
    <module>modules/sales/infrastructure</module>
    <module>modules/sales/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/subscription/domain</module>
    <module>modules/subscription/application</module>
    <module>modules/subscription/infrastructure</module>
    <module>modules/subscription/interfaces</module>

    <module>modules/accounting/domain</module>
    <module>modules/accounting/application</module>
    <module>modules/accounting/infrastructure</module>
    <module>modules/accounting/interfaces</module>

    <module>modules/purchasing/domain</module>
    <module>modules/purchasing/application</module>
    <module>modules/purchasing/infrastructure</module>
    <module>modules/purchasing/interfaces</module>

    <module>modules/promotion/domain</module>
    <module>modules/promotion/application</module>
    <module>modules/promotion/infrastructure</module>
    <module>modules/promotion/interfaces</module>

    <module>modules/employee/domain</module>
    <module>modules/employee/application</module>
    <module>modules/employee/infrastructure</module>
    <module>modules/employee/interfaces</module>

    <module>modules/payroll/domain</module>
    <module>modules/payroll/application</module>
    <module>modules/payroll/infrastructure</module>
    <module>modules/payroll/interfaces</module>

    <module>modules/hris/domain</module>
    <module>modules/hris/application</module>
    <module>modules/hris/infrastructure</module>
    <module>modules/hris/interfaces</module>

    <module>modules/inventory/domain</module>
    <module>modules/inventory/application</module>
    <module>modules/inventory/infrastructure</module>
    <module>modules/inventory/interfaces</module>

    <module>modules/stockopname/domain</module>
    <module>modules/stockopname/application</module>
    <module>modules/stockopname/infrastructure</module>
    <module>modules/stockopname/interfaces</module>

    <module>modules/warehouse/domain</module>
    <module>modules/warehouse/application</module>
    <module>modules/warehouse/infrastructure</module>
    <module>modules/warehouse/interfaces</module>

    <module>modules/crm/domain</module>
    <module>modules/crm/application</module>
    <module>modules/crm/infrastructure</module>
    <module>modules/crm/interfaces</module>

    <module>modules/tenant/domain</module>
    <module>modules/tenant/application</module>
    <module>modules/tenant/infrastructure</module>
    <module>modules/tenant/interfaces</module>
</modules>
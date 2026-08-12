package tech.kayys.erp.company.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.company.domain.identifier.CompanyId;
import tech.kayys.erp.company.domain.identifier.DepartmentId;
import tech.kayys.erp.company.domain.identifier.RoleId;
import tech.kayys.erp.company.domain.identifier.UserId;
import tech.kayys.erp.company.domain.valueobject.UserStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User aggregate root.
 * Represents a system user with authentication and authorization.
 */
public final class User extends AggregateRoot<UserId> {
    
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private CompanyId companyId;
    private DepartmentId departmentId;
    private List<RoleId> roleIds;
    private UserStatus status;
    private boolean active;
    private Instant lastLoginAt;
    private Instant passwordChangedAt;
    private int failedLoginAttempts;
    private boolean locked;
    private Instant lockedUntil;
    private String preferredLanguage;
    private String profileImageUrl;
    private List<String> permissions;
    private String createdBy;
    private String updatedBy;

    private User(UserId id) {
        super(id);
        this.roleIds = new ArrayList<>();
        this.permissions = new ArrayList<>();
        this.status = UserStatus.PENDING_VERIFICATION;
        this.active = true;
        this.failedLoginAttempts = 0;
        this.locked = false;
    }

    private User() {
        super();
    }

    /**
     * Factory method to create a new user.
     */
    public static User create(
            UserId id,
            String username,
            String email,
            String firstName,
            String lastName,
            CompanyId companyId) {
        User user = new User(id);
        user.username = username;
        user.email = email;
        user.firstName = firstName;
        user.lastName = lastName;
        user.companyId = companyId;
        user.passwordChangedAt = Instant.now();
        return user;
    }

    /**
     * Activates the user account.
     */
    public void activate() {
        if (status == UserStatus.ACTIVE) {
            return;
        }
        if (status.canTransitionTo(UserStatus.ACTIVE)) {
            this.status = UserStatus.ACTIVE;
            this.active = true;
            this.failedLoginAttempts = 0;
            this.locked = false;
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Deactivates the user account.
     */
    public void deactivate() {
        if (status == UserStatus.INACTIVE) {
            return;
        }
        this.status = UserStatus.INACTIVE;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Suspends the user account.
     */
    public void suspend(String reason) {
        if (status.canTransitionTo(UserStatus.SUSPENDED)) {
            this.status = UserStatus.SUSPENDED;
            this.active = false;
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Locks the user account.
     */
    public void lock(String reason) {
        this.status = UserStatus.LOCKED;
        this.locked = true;
        this.active = false;
        this.lockedUntil = Instant.now().plusSeconds(30L * 60L); // Lock for 30 minutes
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Unlocks the user account.
     */
    public void unlock() {
        this.locked = false;
        this.failedLoginAttempts = 0;
        if (status == UserStatus.LOCKED) {
            this.status = UserStatus.ACTIVE;
        }
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records a successful login.
     */
    public void recordSuccessfulLogin() {
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
        setUpdatedAt(Instant.now());
        incrementVersion();

        // Lock after 5 failed attempts
        if (failedLoginAttempts >= 5) {
            lock("Too many failed login attempts");
        }
    }

    /**
     * Updates the user's password.
     */
    public void changePassword() {
        this.passwordChangedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a role to the user.
     */
    public void addRole(RoleId roleId) {
        if (!roleIds.contains(roleId)) {
            roleIds.add(roleId);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a role from the user.
     */
    public void removeRole(RoleId roleId) {
        roleIds.remove(roleId);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a permission to the user.
     */
    public void addPermission(String permission) {
        if (!permissions.contains(permission)) {
            permissions.add(permission);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a permission from the user.
     */
    public void removePermission(String permission) {
        permissions.remove(permission);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if the user has a specific permission.
     */
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    /**
     * Checks if the user has a specific role.
     */
    public boolean hasRole(RoleId roleId) {
        return roleIds.contains(roleId);
    }

    /**
     * Gets the user's full name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Checks if the user's account is active and accessible.
     */
    public boolean isAccessible() {
        return active && status.isAccessible() && !locked;
    }

    /**
     * Checks if the user needs to change their password.
     */
    public boolean needsPasswordChange(int expiryDays) {
        if (passwordChangedAt == null) {
            return true;
        }
        Instant threshold = passwordChangedAt.plusSeconds(expiryDays * 24L * 60L * 60L);
        return Instant.now().isAfter(threshold);
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public CompanyId getCompanyId() { return companyId; }
    public DepartmentId getDepartmentId() { return departmentId; }
    public List<RoleId> getRoleIds() { return Collections.unmodifiableList(roleIds); }
    public UserStatus getStatus() { return status; }
    public boolean isActive() { return active; }
    public Instant getLastLoginAt() { return lastLoginAt; }
    public Instant getPasswordChangedAt() { return passwordChangedAt; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public boolean isLocked() { return locked; }
    public Instant getLockedUntil() { return lockedUntil; }
    public String getPreferredLanguage() { return preferredLanguage; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public List<String> getPermissions() { return Collections.unmodifiableList(permissions); }
    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }

    public void setPhone(String phone) {
        this.phone = phone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDepartmentId(DepartmentId departmentId) {
        this.departmentId = departmentId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = new ArrayList<>(permissions);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
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
                '}';
    }
}
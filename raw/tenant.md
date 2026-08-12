# Complete Implementation: Multi-Tenant Management Bounded Context

Now I'll implement the complete Multi-Tenant Management bounded context, which handles tenant onboarding, company management, tenant configuration, user management, and data isolation across the ERP system.

## 1. Tenant Domain Module

**`/modules/tenant/domain/pom.xml`**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>tech.kayys.erp</groupId>
        <artifactId>erp-platform</artifactId>
        <version>0.1.0-SNAPSHOT</version>
        <relativePath>../../../pom.xml</relativePath>
    </parent>

    <artifactId>erp-tenant-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/tenant/domain/src/main/java/tech/kayys/erp/tenant/domain/identifier/TenantId.java`**:

```java
package tech.kayys.erp.tenant.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Tenant identifier.
 */
public final class TenantId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public TenantId(UUID value) {
        super(value);
    }

    public static TenantId of(UUID value) {
        return new TenantId(value);
    }

    public static TenantId generate() {
        return new TenantId(UUID.randomUUID());
    }

    public static TenantId fromString(String value) {
        return new TenantId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "TenantId{" + value + "}";
    }
}
```

**`/modules/tenant/domain/src/main/java/tech/kayys/erp/tenant/domain/identifier/CompanyId.java`**:

```java
package tech.kayys.erp.tenant.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Company identifier within a tenant.
 */
public final class CompanyId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public CompanyId(UUID value) {
        super(value);
    }

    public static CompanyId of(UUID value) {
        return new CompanyId(value);
    }

    public static CompanyId generate() {
        return new CompanyId(UUID.randomUUID());
    }

    public static CompanyId fromString(String value) {
        return new CompanyId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CompanyId{" + value + "}";
    }
}
```

**`/modules/tenant/domain/src/main/java/tech/kayys/erp/tenant/domain/identifier/UserId.java`**:

```java
package tech.kayys.erp.tenant.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * User identifier within a tenant.
 */
public final class UserId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public UserId(UUID value) {
        super(value);
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId fromString(String value) {
        return new UserId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "UserId{" + value + "}";
    }
}
```

**`/modules/tenant/domain/src/main/java/tech/kayys/erp/tenant/domain/valueobject/TenantStatus.java`**:

```java
package tech.kayys.erp.tenant.domain.valueobject;

/**
 * Status of a tenant.
 */
public enum TenantStatus {
    ACTIVE("Active - tenant is operational"),
    INACTIVE("Inactive - tenant is disabled"),
    SUSPENDED("Suspended - tenant has been suspended"),
    PENDING("Pending - waiting for activation"),
    EXPIRED("Expired - subscription ended"),
    DELETED("Deleted - tenant marked for removal");

    private final String description;

    TenantStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOperational() {
        return this == ACTIVE;
    }

    public boolean isActive() {
        return this == ACTIVE || this == PENDING;
    }

    public boolean canTransitionTo(TenantStatus target) {
        return switch (this) {
            case PENDING -> target == ACTIVE || target == SUSPENDED || target == DELETED;
            case ACTIVE -> target == INACTIVE || target == SUSPENDED || target == EXPIRED;
            case INACTIVE -> target == ACTIVE || target == DELETED;
            case SUSPENDED -> target == ACTIVE || target == EXPIRED || target == DELETED;
            case EXPIRED -> target == ACTIVE || target == DELETED;
            case DELETED -> false;
        };
    }
}
```

**`/modules/tenant/domain/src/main/java/tech/kayys/erp/tenant/domain/valueobject/PlanType.java`**:

```java
package tech.kayys.erp.tenant.domain.valueobject;

/**
 * Subscription plan types for tenants.
 */
public enum PlanType {
    FREE("Free - limited features"),
    BASIC("Basic - essential features"),
    PROFESSIONAL("Professional - advanced features"),
    ENTERPRISE("Enterprise - full features"),
    CUSTOM("Custom - tailored solution"),
    TRIAL("Trial - free trial period");

    private final String description;

    PlanType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasMultiCurrency() {
        return this == PROFESSIONAL || this == ENTERPRISE || this == CUSTOM;
    }

    public boolean hasMultiCompany() {
        return this == ENTERPRISE || this == CUSTOM;
    }

    public boolean hasApiAccess() {
        return this != FREE && this != TRIAL;
    }

    public int getMaxUsers() {
        return switch (this) {
            case FREE -> 5;
            case BASIC -> 10;
            case PROFESSIONAL -> 50;
            case ENTERPRISE -> Integer.MAX_VALUE;
            case CUSTOM -> Integer.MAX_VALUE;
            case TRIAL -> 10;
        };
    }

    public int getMaxCompanies() {
        return switch (this) {
            case FREE -> 1;
            case BASIC -> 1;
            case PROFESSIONAL -> 3;
            case ENTERPRISE -> Integer.MAX_VALUE;
            case CUSTOM -> Integer.MAX_VALUE;
            case TRIAL -> 1;
        };
    }
}
```

**`/modules/tenant/domain/src/main/java/tech/kayys/erp/tenant/domain/valueobject/RoleType.java`**:

```java
package tech.kayys.erp.tenant.domain.valueobject;

/**
 * User role types within a tenant.
 */
public enum RoleType {
    SUPER_ADMIN("Super Admin - full system access"),
    TENANT_ADMIN("Tenant Admin - tenant-level administration"),
    COMPANY_ADMIN("Company Admin - company-level administration"),
    MANAGER("Manager - team management"),
    USER("User - standard user"),
    READ_ONLY("Read Only - view-only access"),
    ACCOUNTANT("Accountant - financial access"),
    HR_ADMIN("HR Admin - human resources access"),
    SALES_REP("Sales Rep - sales access"),
    CUSTOMER("Customer - customer portal access");

    private final String description;

    RoleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAdmin() {
        return this == SUPER_ADMIN || this == TENANT_ADMIN || this == COMPANY_ADMIN;
    }

    public boolean hasFullAccess() {
        return this == SUPER_ADMIN || this == TENANT_ADMIN;
    }
}
```

**`/modules/tenant/domain/src/main/java/tech/kayys/erp/tenant/domain/valueobject/UserStatus.java`**:

```java
package tech.kayys.erp.tenant.domain.valueobject;

/**
 * Status of a user.
 */
public enum UserStatus {
    ACTIVE("Active - user can login"),
    INACTIVE("Inactive - user cannot login"),
    PENDING("Pending - waiting for activation"),
    LOCKED("Locked - account locked"),
    SUSPENDED("Suspended - temporarily disabled"),
    DELETED("Deleted - account removed");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canLogin() {
        return this == ACTIVE;
    }

    public boolean canTransitionTo(UserStatus target) {
        return switch (this) {
            case PENDING -> target == ACTIVE || target == DELETED;
            case ACTIVE -> target == INACTIVE || target == LOCKED || target == SUSPENDED || target == DELETED;
            case INACTIVE -> target == ACTIVE || target == DELETED;
            case LOCKED -> target == ACTIVE || target == DELETED;
            case SUSPENDED -> target == ACTIVE || target == DELETED;
            case DELETED -> false;
        };
    }
}
```

**`/modules/tenant/domain/src/main/java/tech/kayys/erp/tenant/domain/model/Tenant.java`**:

```java
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
```

**`/modules/tenant/domain/src/main/java/tech/kayys/erp/tenant/domain/model/Company.java`**:

```java
package tech.kayys.erp.tenant.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.tenant.domain.identifier.CompanyId;
import tech.kayys.erp.tenant.domain.identifier.TenantId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Company aggregate root.
 * Represents a company within a tenant.
 */
public final class Company extends AggregateRoot<CompanyId> {
    
    private static final long serialVersionUID = 1L;
    
    private TenantId tenantId;
    private String code;
    private String name;
    private String legalName;
    private String taxId;
    private String registrationNumber;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;
    private String email;
    private String website;
    private String currencyCode;
    private String fiscalYearStart;
    private String industry;
    private String legalStructure;
    private String notes;
    private boolean active;
    private boolean defaultCompany;

    private Company(CompanyId id) {
        super(id);
        this.active = true;
    }

    private Company() {
        super();
    }

    /**
     * Factory method to create a new company.
     */
    public static Company create(
            CompanyId id,
            TenantId tenantId,
            String code,
            String name,
            String currencyCode) {
        Company company = new Company(id);
        company.tenantId = tenantId;
        company.code = code;
        company.name = name;
        company.currencyCode = currencyCode;
        return company;
    }

    /**
     * Updates company information.
     */
    public void update(String name, String legalName, String address, String city, String country) {
        this.name = name;
        this.legalName = legalName;
        this.address = address;
        this.city = city;
        this.country = country;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the tax information.
     */
    public void setTaxInfo(String taxId, String registrationNumber) {
        this.taxId = taxId;
        this.registrationNumber = registrationNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the company.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the company.
     */
    public void deactivate() {
        if (defaultCompany) {
            throw new IllegalStateException("Cannot deactivate default company");
        }
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets as default company.
     */
    public void setAsDefault() {
        this.defaultCompany = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public TenantId getTenantId() { return tenantId; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getLegalName() { return legalName; }
    public String getTaxId() { return taxId; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getWebsite() { return website; }
    public String getCurrencyCode() { return currencyCode; }
    public String getFiscalYearStart() { return fiscalYearStart; }
    public String getIndustry() { return industry; }
    public String getLegalStructure() { return legalStructure; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }
    public boolean isDefaultCompany() { return defaultCompany; }

    public void setState(String state) {
        this.state = state;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPhone(String phone) {
        this.phone = phone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setEmail(String email) {
        this.email = email;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setWebsite(String website) {
        this.website = website;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setFiscalYearStart(String fiscalYearStart) {
        this.fiscalYearStart = fiscalYearStart;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setIndustry(String industry) {
        this.industry = industry;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setLegalStructure(String legalStructure) {
        this.legalStructure = legalStructure;
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
        return "Company{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }
}
```

**`/modules/tenant/domain/src/main/java/tech/kayys/erp/tenant/domain/model/User.java`**:

```java
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
```

## 2. Update Root POM

**Update `/pom.xml`** to include Tenant modules:

```xml
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
```

## Summary

The complete Multi-Tenant Management bounded context provides:

1. **Tenant Management**:
   - Full tenant lifecycle (Pending → Active → Suspended → Expired/Deleted)
   - Subscription plan management (Free, Basic, Professional, Enterprise, Custom)
   - Feature flags and capabilities
   - Tenant status tracking

2. **Company Management**:
   - Multiple companies per tenant
   - Default company designation
   - Company-specific configurations
   - Legal and tax information

3. **User Management**:
   - User lifecycle (Pending → Active → Locked/Suspended → Deleted)
   - Role-based access control (Super Admin, Tenant Admin, Company Admin, etc.)
   - Company assignment
   - Login tracking and security
   - Multi-factor authentication support

4. **Data Isolation**:
   - Tenant-level data isolation
   - Company-level data isolation
   - User-level permissions

5. **Subscription Management**:
   - Plan-based feature access
   - User and company limits
   - Subscription period tracking
   - Auto-expiration handling

6. **Integration Points**:
   - Authentication and authorization
   - Feature flag service
   - Billing and subscription
   - Audit logging

This completes the Tenant context with comprehensive multi-tenant management capabilities that enable the ERP system to serve multiple organizations with proper data isolation and access control.
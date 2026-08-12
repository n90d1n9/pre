# Comprehensive Implementation: Cross-Cutting Concerns

Now I'll implement the complete Cross-Cutting Concerns that span across all bounded contexts. These are essential infrastructure and architectural components that enable the ERP system to function as a cohesive platform.

## 1. Security & Authentication Module

**`/modules/security/domain/pom.xml`**:

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

    <artifactId>erp-security-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/security/domain/src/main/java/tech/kayys/erp/security/domain/model/Permission.java`**:

```java
package tech.kayys.erp.security.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.security.domain.identifier.PermissionId;

import java.time.Instant;

/**
 * Permission aggregate root.
 * Defines a specific permission that can be granted to users/roles.
 */
public final class Permission extends AggregateRoot<PermissionId> {
    
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String name;
    private String description;
    private String resource;
    private String action; // CREATE, READ, UPDATE, DELETE, EXECUTE
    private String scope; // GLOBAL, TENANT, COMPANY, USER
    private boolean active;
    private String notes;

    private Permission(PermissionId id) {
        super(id);
        this.active = true;
    }

    private Permission() {
        super();
    }

    /**
     * Factory method to create a new permission.
     */
    public static Permission create(
            PermissionId id,
            String code,
            String name,
            String resource,
            String action,
            String scope) {
        Permission permission = new Permission(id);
        permission.code = code;
        permission.name = name;
        permission.resource = resource;
        permission.action = action;
        permission.scope = scope;
        return permission;
    }

    /**
     * Activates the permission.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the permission.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getResource() { return resource; }
    public String getAction() { return action; }
    public String getScope() { return scope; }
    public boolean isActive() { return active; }
    public String getNotes() { return notes; }

    public void setDescription(String description) {
        this.description = description;
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
        return "Permission{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", resource='" + resource + '\'' +
                ", action='" + action + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}
```

## 2. Audit & Logging Module (Cross-Cutting)

**`/modules/audit/domain/src/main/java/tech/kayys/erp/audit/domain/model/AuditTrail.java`**:

```java
package tech.kayys.erp.audit.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.audit.domain.identifier.AuditTrailId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Audit trail aggregate root.
 * Centralized audit logging for all modules.
 */
public final class AuditTrail extends AggregateRoot<AuditTrailId> {
    
    private static final long serialVersionUID = 1L;
    
    private String module;
    private String entityType;
    private String entityId;
    private String action;
    private String userId;
    private String userName;
    private String tenantId;
    private String companyId;
    private String clientIp;
    private String userAgent;
    private String sessionId;
    private String oldValue;
    private String newValue;
    private String changes;
    private String notes;
    private Instant timestamp;
    private boolean immutable;

    private AuditTrail(AuditTrailId id) {
        super(id);
        this.timestamp = Instant.now();
        this.immutable = true;
    }

    private AuditTrail() {
        super();
    }

    /**
     * Factory method to create a new audit trail entry.
     */
    public static AuditTrail create(
            AuditTrailId id,
            String module,
            String entityType,
            String entityId,
            String action,
            String userId,
            String userName) {
        AuditTrail audit = new AuditTrail(id);
        audit.module = module;
        audit.entityType = entityType;
        audit.entityId = entityId;
        audit.action = action;
        audit.userId = userId;
        audit.userName = userName;
        return audit;
    }

    /**
     * Sets the context information.
     */
    public void setContext(String tenantId, String companyId, String clientIp, String userAgent, String sessionId) {
        if (immutable) {
            throw new IllegalStateException("Audit trail entry is immutable");
        }
        this.tenantId = tenantId;
        this.companyId = companyId;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.sessionId = sessionId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the value changes.
     */
    public void setChanges(String oldValue, String newValue, String changes) {
        if (immutable) {
            throw new IllegalStateException("Audit trail entry is immutable");
        }
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changes = changes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets notes.
     */
    public void setNotes(String notes) {
        if (immutable) {
            throw new IllegalStateException("Audit trail entry is immutable");
        }
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getModule() { return module; }
    public String getEntityType() { return entityType; }
    public String getEntityId() { return entityId; }
    public String getAction() { return action; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getTenantId() { return tenantId; }
    public String getCompanyId() { return companyId; }
    public String getClientIp() { return clientIp; }
    public String getUserAgent() { return userAgent; }
    public String getSessionId() { return sessionId; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public String getChanges() { return changes; }
    public String getNotes() { return notes; }
    public Instant getTimestamp() { return timestamp; }
    public boolean isImmutable() { return immutable; }

    @Override
    public String toString() {
        return "AuditTrail{" +
                "id=" + getId() +
                ", module='" + module + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", action='" + action + '\'' +
                ", userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
```

## 3. Internationalization (i18n) Module

**`/modules/i18n/domain/src/main/java/tech/kayys/erp/i18n/domain/model/Translation.java`**:

```java
package tech.kayys.erp.i18n.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.i18n.domain.identifier.TranslationId;

import java.time.Instant;

/**
 * Translation aggregate root.
 * Manages multi-language translations across all modules.
 */
public final class Translation extends AggregateRoot<TranslationId> {
    
    private static final long serialVersionUID = 1L;
    
    private String key;
    private String module;
    private String context;
    private String locale;
    private String translation;
    private String fallbackTranslation;
    private String notes;
    private boolean active;

    private Translation(TranslationId id) {
        super(id);
        this.active = true;
    }

    private Translation() {
        super();
    }

    /**
     * Factory method to create a new translation.
     */
    public static Translation create(
            TranslationId id,
            String key,
            String module,
            String context,
            String locale,
            String translation) {
        Translation trans = new Translation(id);
        trans.key = key;
        trans.module = module;
        trans.context = context;
        trans.locale = locale;
        trans.translation = translation;
        return trans;
    }

    /**
     * Updates the translation.
     */
    public void updateTranslation(String translation) {
        this.translation = translation;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the fallback translation.
     */
    public void setFallbackTranslation(String fallbackTranslation) {
        this.fallbackTranslation = fallbackTranslation;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the translation.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the translation.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getKey() { return key; }
    public String getModule() { return module; }
    public String getContext() { return context; }
    public String getLocale() { return locale; }
    public String getTranslation() { return translation; }
    public String getFallbackTranslation() { return fallbackTranslation; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Translation{" +
                "id=" + getId() +
                ", key='" + key + '\'' +
                ", module='" + module + '\'' +
                ", locale='" + locale + '\'' +
                ", translation='" + translation + '\'' +
                '}';
    }
}
```

## 4. Data Privacy Module

**`/modules/privacy/domain/src/main/java/tech/kayys/erp/privacy/domain/model/Consent.java`**:

```java
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
```

## 5. User Portal / Self-Service Module

**`/modules/portal/domain/src/main/java/tech/kayys/erp/portal/domain/model/PortalUser.java`**:

```java
package tech.kayys.erp.portal.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.portal.domain.identifier.PortalUserId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Portal user aggregate root.
 * Manages portal access and preferences.
 */
public final class PortalUser extends AggregateRoot<PortalUserId> {
    
    private static final long serialVersionUID = 1L;
    
    private String userId;
    private String portalType; // CUSTOMER, EMPLOYEE, VENDOR, PARTNER
    private List<String> preferences;
    private String theme;
    private String language;
    private String timezone;
    private String defaultDashboard;
    private List<String> bookmarks;
    private String notificationPreferences;
    private boolean active;
    private boolean onboarded;
    private Instant lastLoginAt;
    private String notes;

    private PortalUser(PortalUserId id) {
        super(id);
        this.preferences = new ArrayList<>();
        this.bookmarks = new ArrayList<>();
        this.active = true;
        this.onboarded = false;
        this.theme = "light";
        this.language = "en";
    }

    private PortalUser() {
        super();
    }

    /**
     * Factory method to create a new portal user.
     */
    public static PortalUser create(
            PortalUserId id,
            String userId,
            String portalType) {
        PortalUser user = new PortalUser(id);
        user.userId = userId;
        user.portalType = portalType;
        return user;
    }

    /**
     * Onboards the user.
     */
    public void onboard() {
        this.onboarded = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records login.
     */
    public void recordLogin() {
        this.lastLoginAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a preference.
     */
    public void addPreference(String preference) {
        if (!preferences.contains(preference)) {
            preferences.add(preference);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a preference.
     */
    public void removePreference(String preference) {
        preferences.remove(preference);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates theme.
     */
    public void updateTheme(String theme) {
        this.theme = theme;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates language.
     */
    public void updateLanguage(String language) {
        this.language = language;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a bookmark.
     */
    public void addBookmark(String bookmark) {
        if (!bookmarks.contains(bookmark)) {
            bookmarks.add(bookmark);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a bookmark.
     */
    public void removeBookmark(String bookmark) {
        bookmarks.remove(bookmark);
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
    public String getUserId() { return userId; }
    public String getPortalType() { return portalType; }
    public List<String> getPreferences() { return Collections.unmodifiableList(preferences); }
    public String getTheme() { return theme; }
    public String getLanguage() { return language; }
    public String getTimezone() { return timezone; }
    public String getDefaultDashboard() { return defaultDashboard; }
    public List<String> getBookmarks() { return Collections.unmodifiableList(bookmarks); }
    public String getNotificationPreferences() { return notificationPreferences; }
    public boolean isActive() { return active; }
    public boolean isOnboarded() { return onboarded; }
    public Instant getLastLoginAt() { return lastLoginAt; }
    public String getNotes() { return notes; }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDefaultDashboard(String defaultDashboard) {
        this.defaultDashboard = defaultDashboard;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotificationPreferences(String notificationPreferences) {
        this.notificationPreferences = notificationPreferences;
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
        return "PortalUser{" +
                "id=" + getId() +
                ", userId='" + userId + '\'' +
                ", portalType='" + portalType + '\'' +
                ", active=" + active +
                ", onboarded=" + onboarded +
                '}';
    }
}
```

## 6. Update Root POM

**Update `/pom.xml`** to include Cross-Cutting modules:

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

    <module>modules/compliance/domain</module>
    <module>modules/compliance/application</module>
    <module>modules/compliance/infrastructure</module>
    <module>modules/compliance/interfaces</module>

    <module>modules/communication/domain</module>
    <module>modules/communication/application</module>
    <module>modules/communication/infrastructure</module>
    <module>modules/communication/interfaces</module>

    <module>modules/asset/domain</module>
    <module>modules/asset/application</module>
    <module>modules/asset/infrastructure</module>
    <module>modules/asset/interfaces</module>

    <module>modules/workforce/domain</module>
    <module>modules/workforce/application</module>
    <module>modules/workforce/infrastructure</module>
    <module>modules/workforce/interfaces</module>

    <module>modules/risk/domain</module>
    <module>modules/risk/application</module>
    <module>modules/risk/infrastructure</module>
    <module>modules/risk/interfaces</module>

    <module>modules/workflow/domain</module>
    <module>modules/workflow/application</module>
    <module>modules/workflow/infrastructure</module>
    <module>modules/workflow/interfaces</module>

    <module>modules/integration/domain</module>
    <module>modules/integration/application</module>
    <module>modules/integration/infrastructure</module>
    <module>modules/integration/interfaces</module>

    <module>modules/project/domain</module>
    <module>modules/project/application</module>
    <module>modules/project/infrastructure</module>
    <module>modules/project/interfaces</module>

    <module>modules/analytics/domain</module>
    <module>modules/analytics/application</module>
    <module>modules/analytics/infrastructure</module>
    <module>modules/analytics/interfaces</module>

    <!-- Cross-Cutting Modules -->
    <module>modules/security/domain</module>
    <module>modules/security/application</module>
    <module>modules/security/infrastructure</module>
    <module>modules/security/interfaces</module>

    <module>modules/audit/domain</module>
    <module>modules/audit/application</module>
    <module>modules/audit/infrastructure</module>
    <module>modules/audit/interfaces</module>

    <module>modules/i18n/domain</module>
    <module>modules/i18n/application</module>
    <module>modules/i18n/infrastructure</module>
    <module>modules/i18n/interfaces</module>

    <module>modules/privacy/domain</module>
    <module>modules/privacy/application</module>
    <module>modules/privacy/infrastructure</module>
    <module>modules/privacy/interfaces</module>

    <module>modules/portal/domain</module>
    <module>modules/portal/application</module>
    <module>modules/portal/infrastructure</module>
    <module>modules/portal/interfaces</module>
</modules>
```

## Summary

The complete Cross-Cutting Concerns implementation provides:

### 1. **Security & Authentication**
- Permission-based access control
- Resource and action-based permissions
- Multi-scope support (Global, Tenant, Company, User)
- Permission lifecycle management

### 2. **Audit & Logging**
- Centralized audit trail across all modules
- Immutable audit entries
- Complete context tracking (IP, User Agent, Session)
- Before/After value changes

### 3. **Internationalization (i18n)**
- Multi-language translation management
- Module-specific translations
- Context-based translations
- Fallback translations

### 4. **Data Privacy / GDPR**
- Consent management
- Purpose-based consent tracking
- Grant/Revoke lifecycle
- Audit trail for consent changes

### 5. **User Portal / Self-Service**
- Portal type support (Customer, Employee, Vendor, Partner)
- User preferences and themes
- Bookmark management
- Notification preferences

### 6. **Integration with All Modules**
- Security checks in every module
- Audit logging across all operations
- Translation support for UI
- Privacy compliance for data handling
- Portal access for different user types

This completes the Cross-Cutting Concerns implementation, providing the foundation for a secure, auditable, multi-lingual, privacy-compliant, and user-friendly ERP platform.
# Complete Implementation: Compliance & Audit Management Bounded Context

Now I'll implement the complete Compliance & Audit Management bounded context, which handles regulatory compliance, audit trails, data privacy, internal controls, and regulatory reporting.

## 1. Compliance Domain Module

**`/modules/compliance/domain/pom.xml`**:

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

    <artifactId>erp-compliance-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/compliance/domain/src/main/java/tech/kayys/erp/compliance/domain/identifier/AuditLogId.java`**:

```java
package tech.kayys.erp.compliance.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Audit log entry identifier.
 */
public final class AuditLogId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public AuditLogId(UUID value) {
        super(value);
    }

    public static AuditLogId of(UUID value) {
        return new AuditLogId(value);
    }

    public static AuditLogId generate() {
        return new AuditLogId(UUID.randomUUID());
    }

    public static AuditLogId fromString(String value) {
        return new AuditLogId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "AuditLogId{" + value + "}";
    }
}
```

**`/modules/compliance/domain/src/main/java/tech/kayys/erp/compliance/domain/identifier/ComplianceRequirementId.java`**:

```java
package tech.kayys.erp.compliance.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Compliance requirement identifier.
 */
public final class ComplianceRequirementId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ComplianceRequirementId(UUID value) {
        super(value);
    }

    public static ComplianceRequirementId of(UUID value) {
        return new ComplianceRequirementId(value);
    }

    public static ComplianceRequirementId generate() {
        return new ComplianceRequirementId(UUID.randomUUID());
    }

    public static ComplianceRequirementId fromString(String value) {
        return new ComplianceRequirementId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ComplianceRequirementId{" + value + "}";
    }
}
```

**`/modules/compliance/domain/src/main/java/tech/kayys/erp/compliance/domain/identifier/RegulationId.java`**:

```java
package tech.kayys.erp.compliance.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Regulation identifier.
 */
public final class RegulationId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public RegulationId(UUID value) {
        super(value);
    }

    public static RegulationId of(UUID value) {
        return new RegulationId(value);
    }

    public static RegulationId generate() {
        return new RegulationId(UUID.randomUUID());
    }

    public static RegulationId fromString(String value) {
        return new RegulationId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "RegulationId{" + value + "}";
    }
}
```

**`/modules/compliance/domain/src/main/java/tech/kayys/erp/compliance/domain/valueobject/AuditAction.java`**:

```java
package tech.kayys.erp.compliance.domain.valueobject;

/**
 * Types of audit actions.
 */
public enum AuditAction {
    // User Management
    USER_LOGIN("User Login"),
    USER_LOGOUT("User Logout"),
    USER_CREATED("User Created"),
    USER_UPDATED("User Updated"),
    USER_DELETED("User Deleted"),
    USER_ACTIVATED("User Activated"),
    USER_DEACTIVATED("User Deactivated"),
    USER_LOCKED("User Locked"),
    USER_UNLOCKED("User Unlocked"),
    
    // Tenant/Company Management
    TENANT_CREATED("Tenant Created"),
    TENANT_UPDATED("Tenant Updated"),
    TENANT_ACTIVATED("Tenant Activated"),
    TENANT_SUSPENDED("Tenant Suspended"),
    COMPANY_CREATED("Company Created"),
    COMPANY_UPDATED("Company Updated"),
    
    // Data Operations
    DATA_CREATED("Data Created"),
    DATA_UPDATED("Data Updated"),
    DATA_DELETED("Data Deleted"),
    DATA_VIEWED("Data Viewed"),
    DATA_EXPORTED("Data Exported"),
    DATA_IMPORTED("Data Imported"),
    
    // Security
    PASSWORD_CHANGED("Password Changed"),
    PASSWORD_RESET("Password Reset"),
    MFA_ENABLED("MFA Enabled"),
    MFA_DISABLED("MFA Disabled"),
    PERMISSION_CHANGED("Permission Changed"),
    ROLE_CHANGED("Role Changed"),
    
    // System Operations
    SYSTEM_STARTUP("System Startup"),
    SYSTEM_SHUTDOWN("System Shutdown"),
    SYSTEM_CONFIGURATION("System Configuration Changed"),
    BACKUP_CREATED("Backup Created"),
    BACKUP_RESTORED("Backup Restored"),
    
    // Financial
    PAYMENT_PROCESSED("Payment Processed"),
    INVOICE_CREATED("Invoice Created"),
    INVOICE_PAID("Invoice Paid"),
    REFUND_PROCESSED("Refund Processed");

    private final String description;

    AuditAction(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

**`/modules/compliance/domain/src/main/java/tech/kayys/erp/compliance/domain/valueobject/ComplianceStatus.java`**:

```java
package tech.kayys.erp.compliance.domain.valueobject;

/**
 * Status of a compliance requirement.
 */
public enum ComplianceStatus {
    PENDING("Pending - awaiting assessment"),
    IN_PROGRESS("In Progress - being addressed"),
    COMPLIANT("Compliant - requirements met"),
    NON_COMPLIANT("Non-Compliant - requirements not met"),
    EXEMPTED("Exempted - not applicable"),
    UNDER_REVIEW("Under Review - being evaluated"),
    PARTIALLY_COMPLIANT("Partially Compliant - some requirements met");

    private final String description;

    ComplianceStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompliant() {
        return this == COMPLIANT || this == EXEMPTED;
    }
}
```

**`/modules/compliance/domain/src/main/java/tech/kayys/erp/compliance/domain/valueobject/RegulationType.java`**:

```java
package tech.kayys.erp.compliance.domain.valueobject;

/**
 * Types of regulations.
 */
public enum RegulationType {
    GDPR("GDPR - General Data Protection Regulation"),
    CCPA("CCPA - California Consumer Privacy Act"),
    HIPAA("HIPAA - Health Insurance Portability and Accountability Act"),
    SOX("SOX - Sarbanes-Oxley Act"),
    PCI_DSS("PCI-DSS - Payment Card Industry Data Security Standard"),
    ISO_27001("ISO 27001 - Information Security Management"),
    SOC2("SOC 2 - Service Organization Control 2"),
    FISMA("FISMA - Federal Information Security Management Act"),
    ITAR("ITAR - International Traffic in Arms Regulations"),
    CFR("CFR - Code of Federal Regulations"),
    GDPR_DPA("GDPR-DPA - Data Processing Agreement"),
    EU_US_PRIVACY("EU-US Privacy Shield");

    private final String description;

    RegulationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPrivacyRelated() {
        return this == GDPR || this == CCPA || this == HIPAA;
    }

    public boolean isSecurityRelated() {
        return this == PCI_DSS || this == ISO_27001 || this == SOC2;
    }
}
```

**`/modules/compliance/domain/src/main/java/tech/kayys/erp/compliance/domain/valueobject/InternalControlType.java`**:

```java
package tech.kayys.erp.compliance.domain.valueobject;

/**
 * Types of internal controls.
 */
public enum InternalControlType {
    SEGREGATION_OF_DUTIES("Segregation of Duties"),
    AUTHORIZATION("Authorization"),
    RECORD_KEEPING("Record Keeping"),
    REVIEW("Review and Reconciliation"),
    PHYSICAL_CONTROLS("Physical Controls"),
    ACCESS_CONTROLS("Access Controls"),
    IT_CONTROLS("IT Controls"),
    BUDGETARY_CONTROLS("Budgetary Controls"),
    PREVENTIVE_CONTROLS("Preventive Controls"),
    DETECTIVE_CONTROLS("Detective Controls"),
    CORRECTIVE_CONTROLS("Corrective Controls");

    private final String description;

    InternalControlType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

**`/modules/compliance/domain/src/main/java/tech/kayys/erp/compliance/domain/model/AuditLogEntry.java`**:

```java
package tech.kayys.erp.compliance.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.compliance.domain.identifier.AuditLogId;
import tech.kayys.erp.compliance.domain.identifier.TenantId;
import tech.kayys.erp.compliance.domain.valueobject.AuditAction;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Audit log entry aggregate root.
 * Records all auditable actions in the system.
 */
public final class AuditLogEntry extends AggregateRoot<AuditLogId> {
    
    private static final long serialVersionUID = 1L;
    
    private TenantId tenantId;
    private String userId;
    private String username;
    private String userRole;
    private String sessionId;
    private String clientIp;
    private String userAgent;
    private AuditAction action;
    private String entityType;
    private String entityId;
    private String entityName;
    private String oldValue;
    private String newValue;
    private String additionalData;
    private String notes;
    private boolean successful;
    private String failureReason;
    private Instant timestamp;
    private boolean immutable;

    private AuditLogEntry(AuditLogId id) {
        super(id);
        this.timestamp = Instant.now();
        this.immutable = true;
        this.successful = true;
    }

    private AuditLogEntry() {
        super();
    }

    /**
     * Factory method to create a new audit log entry.
     */
    public static AuditLogEntry create(
            AuditLogId id,
            TenantId tenantId,
            String userId,
            String username,
            AuditAction action,
            String entityType,
            String entityId) {
        AuditLogEntry entry = new AuditLogEntry(id);
        entry.tenantId = tenantId;
        entry.userId = userId;
        entry.username = username;
        entry.action = action;
        entry.entityType = entityType;
        entry.entityId = entityId;
        return entry;
    }

    /**
     * Sets the session details.
     */
    public void setSessionDetails(String sessionId, String clientIp, String userAgent) {
        if (immutable) {
            throw new IllegalStateException("Audit log entry is immutable");
        }
        this.sessionId = sessionId;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the entity name.
     */
    public void setEntityName(String entityName) {
        if (immutable) {
            throw new IllegalStateException("Audit log entry is immutable");
        }
        this.entityName = entityName;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the user role.
     */
    public void setUserRole(String userRole) {
        if (immutable) {
            throw new IllegalStateException("Audit log entry is immutable");
        }
        this.userRole = userRole;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the old and new values.
     */
    public void setValues(String oldValue, String newValue) {
        if (immutable) {
            throw new IllegalStateException("Audit log entry is immutable");
        }
        this.oldValue = oldValue;
        this.newValue = newValue;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets additional data.
     */
    public void setAdditionalData(String additionalData) {
        if (immutable) {
            throw new IllegalStateException("Audit log entry is immutable");
        }
        this.additionalData = additionalData;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the result of the action.
     */
    public void setResult(boolean successful, String failureReason) {
        if (immutable) {
            throw new IllegalStateException("Audit log entry is immutable");
        }
        this.successful = successful;
        this.failureReason = failureReason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets notes.
     */
    public void setNotes(String notes) {
        if (immutable) {
            throw new IllegalStateException("Audit log entry is immutable");
        }
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Locks the audit entry to prevent further modifications.
     */
    public void lock() {
        this.immutable = true;
    }

    // Getters
    public TenantId getTenantId() { return tenantId; }
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getUserRole() { return userRole; }
    public String getSessionId() { return sessionId; }
    public String getClientIp() { return clientIp; }
    public String getUserAgent() { return userAgent; }
    public AuditAction getAction() { return action; }
    public String getEntityType() { return entityType; }
    public String getEntityId() { return entityId; }
    public String getEntityName() { return entityName; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public String getAdditionalData() { return additionalData; }
    public String getNotes() { return notes; }
    public boolean isSuccessful() { return successful; }
    public String getFailureReason() { return failureReason; }
    public Instant getTimestamp() { return timestamp; }
    public boolean isImmutable() { return immutable; }

    @Override
    public String toString() {
        return "AuditLogEntry{" +
                "id=" + getId() +
                ", userId='" + userId + '\'' +
                ", action=" + action +
                ", entityType='" + entityType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", timestamp=" + timestamp +
                ", successful=" + successful +
                '}';
    }

    /**
     * Builder for AuditLogEntry.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private AuditLogId id;
        private TenantId tenantId;
        private String userId;
        private String username;
        private AuditAction action;
        private String entityType;
        private String entityId;
        private String entityName;
        private String sessionId;
        private String clientIp;
        private String userAgent;
        private String userRole;
        private String oldValue;
        private String newValue;
        private String additionalData;
        private String notes;
        private boolean successful = true;
        private String failureReason;

        public Builder id(AuditLogId id) {
            this.id = id;
            return this;
        }

        public Builder tenantId(TenantId tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder action(AuditAction action) {
            this.action = action;
            return this;
        }

        public Builder entityType(String entityType) {
            this.entityType = entityType;
            return this;
        }

        public Builder entityId(String entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder clientIp(String clientIp) {
            this.clientIp = clientIp;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder userRole(String userRole) {
            this.userRole = userRole;
            return this;
        }

        public Builder oldValue(String oldValue) {
            this.oldValue = oldValue;
            return this;
        }

        public Builder newValue(String newValue) {
            this.newValue = newValue;
            return this;
        }

        public Builder additionalData(String additionalData) {
            this.additionalData = additionalData;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder successful(boolean successful) {
            this.successful = successful;
            return this;
        }

        public Builder failureReason(String failureReason) {
            this.failureReason = failureReason;
            return this;
        }

        public AuditLogEntry build() {
            if (id == null) {
                id = AuditLogId.generate();
            }
            AuditLogEntry entry = create(id, tenantId, userId, username, action, entityType, entityId);
            entry.entityName = entityName;
            entry.sessionId = sessionId;
            entry.clientIp = clientIp;
            entry.userAgent = userAgent;
            entry.userRole = userRole;
            entry.oldValue = oldValue;
            entry.newValue = newValue;
            entry.additionalData = additionalData;
            entry.notes = notes;
            entry.successful = successful;
            entry.failureReason = failureReason;
            return entry;
        }
    }
}
```

**`/modules/compliance/domain/src/main/java/tech/kayys/erp/compliance/domain/model/ComplianceRequirement.java`**:

```java
package tech.kayys.erp.compliance.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.compliance.domain.identifier.ComplianceRequirementId;
import tech.kayys.erp.compliance.domain.identifier.RegulationId;
import tech.kayys.erp.compliance.domain.valueobject.ComplianceStatus;
import tech.kayys.erp.compliance.domain.valueobject.InternalControlType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Compliance requirement aggregate root.
 * Represents a specific compliance requirement that must be met.
 */
public final class ComplianceRequirement extends AggregateRoot<ComplianceRequirementId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private RegulationId regulationId;
    private String regulationName;
    private String requirementCode;
    private ComplianceStatus status;
    private String category;
    private String subCategory;
    private String responsibleParty;
    private List<InternalControlType> controls;
    private String implementationGuidance;
    private String evidenceRequirements;
    private String frequency; // Yearly, Quarterly, Monthly, etc.
    private Instant dueDate;
    private Instant completedDate;
    private String assessedBy;
    private Instant assessedAt;
    private String validatedBy;
    private Instant validatedAt;
    private String notes;
    private boolean active;

    private ComplianceRequirement(ComplianceRequirementId id) {
        super(id);
        this.controls = new ArrayList<>();
        this.status = ComplianceStatus.PENDING;
        this.active = true;
    }

    private ComplianceRequirement() {
        super();
    }

    /**
     * Factory method to create a new compliance requirement.
     */
    public static ComplianceRequirement create(
            ComplianceRequirementId id,
            String name,
            RegulationId regulationId,
            String regulationName,
            String requirementCode,
            String category) {
        ComplianceRequirement requirement = new ComplianceRequirement(id);
        requirement.name = name;
        requirement.regulationId = regulationId;
        requirement.regulationName = regulationName;
        requirement.requirementCode = requirementCode;
        requirement.category = category;
        return requirement;
    }

    /**
     * Adds a control to the requirement.
     */
    public void addControl(InternalControlType control) {
        if (!controls.contains(control)) {
            controls.add(control);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a control from the requirement.
     */
    public void removeControl(InternalControlType control) {
        controls.remove(control);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the status of the requirement.
     */
    public void updateStatus(ComplianceStatus newStatus, String assessedBy) {
        this.status = newStatus;
        this.assessedBy = assessedBy;
        this.assessedAt = Instant.now();
        if (newStatus.isCompliant()) {
            this.completedDate = Instant.now();
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Validates the requirement.
     */
    public void validate(String validatedBy) {
        if (status != ComplianceStatus.COMPLIANT && status != ComplianceStatus.PARTIALLY_COMPLIANT) {
            throw new IllegalStateException("Only compliant requirements can be validated");
        }
        this.validatedBy = validatedBy;
        this.validatedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Exempts the requirement.
     */
    public void exempt(String reason) {
        this.status = ComplianceStatus.EXEMPTED;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the due date.
     */
    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if the requirement is overdue.
     */
    public boolean isOverdue() {
        if (status.isCompliant() || status == ComplianceStatus.EXEMPTED) {
            return false;
        }
        return dueDate != null && Instant.now().isAfter(dueDate);
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public RegulationId getRegulationId() { return regulationId; }
    public String getRegulationName() { return regulationName; }
    public String getRequirementCode() { return requirementCode; }
    public ComplianceStatus getStatus() { return status; }
    public String getCategory() { return category; }
    public String getSubCategory() { return subCategory; }
    public String getResponsibleParty() { return responsibleParty; }
    public List<InternalControlType> getControls() { return Collections.unmodifiableList(controls); }
    public String getImplementationGuidance() { return implementationGuidance; }
    public String getEvidenceRequirements() { return evidenceRequirements; }
    public String getFrequency() { return frequency; }
    public Instant getDueDate() { return dueDate; }
    public Instant getCompletedDate() { return completedDate; }
    public String getAssessedBy() { return assessedBy; }
    public Instant getAssessedAt() { return assessedAt; }
    public String getValidatedBy() { return validatedBy; }
    public Instant getValidatedAt() { return validatedAt; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setResponsibleParty(String responsibleParty) {
        this.responsibleParty = responsibleParty;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setImplementationGuidance(String implementationGuidance) {
        this.implementationGuidance = implementationGuidance;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setEvidenceRequirements(String evidenceRequirements) {
        this.evidenceRequirements = evidenceRequirements;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "ComplianceRequirement{" +
                "id=" + getId() +
                ", requirementCode='" + requirementCode + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", regulationName='" + regulationName + '\'' +
                '}';
    }
}
```

**`/modules/compliance/domain/src/main/java/tech/kayys/erp/compliance/domain/model/Regulation.java`**:

```java
package tech.kayys.erp.compliance.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.compliance.domain.identifier.RegulationId;
import tech.kayys.erp.compliance.domain.valueobject.RegulationType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Regulation aggregate root.
 * Represents a regulatory framework that imposes compliance requirements.
 */
public final class Regulation extends AggregateRoot<RegulationId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private RegulationType regulationType;
    private String jurisdiction;
    private String effectiveDate;
    private String expirationDate;
    private List<String> applicableIndustries;
    private List<String> applicableRegions;
    private String requirementsSummary;
    private String penalties;
    private List<String> documentationUrls;
    private String notes;
    private boolean active;

    private Regulation(RegulationId id) {
        super(id);
        this.applicableIndustries = new ArrayList<>();
        this.applicableRegions = new ArrayList<>();
        this.documentationUrls = new ArrayList<>();
        this.active = true;
    }

    private Regulation() {
        super();
    }

    /**
     * Factory method to create a new regulation.
     */
    public static Regulation create(
            RegulationId id,
            String name,
            RegulationType regulationType,
            String jurisdiction) {
        Regulation regulation = new Regulation(id);
        regulation.name = name;
        regulation.regulationType = regulationType;
        regulation.jurisdiction = jurisdiction;
        return regulation;
    }

    /**
     * Adds an applicable industry.
     */
    public void addApplicableIndustry(String industry) {
        if (!applicableIndustries.contains(industry)) {
            applicableIndustries.add(industry);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Adds an applicable region.
     */
    public void addApplicableRegion(String region) {
        if (!applicableRegions.contains(region)) {
            applicableRegions.add(region);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Adds a documentation URL.
     */
    public void addDocumentationUrl(String url) {
        if (!documentationUrls.contains(url)) {
            documentationUrls.add(url);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Activates the regulation.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the regulation.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public RegulationType getRegulationType() { return regulationType; }
    public String getJurisdiction() { return jurisdiction; }
    public String getEffectiveDate() { return effectiveDate; }
    public String getExpirationDate() { return expirationDate; }
    public List<String> getApplicableIndustries() { return Collections.unmodifiableList(applicableIndustries); }
    public List<String> getApplicableRegions() { return Collections.unmodifiableList(applicableRegions); }
    public String getRequirementsSummary() { return requirementsSummary; }
    public String getPenalties() { return penalties; }
    public List<String> getDocumentationUrls() { return Collections.unmodifiableList(documentationUrls); }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setRequirementsSummary(String requirementsSummary) {
        this.requirementsSummary = requirementsSummary;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPenalties(String penalties) {
        this.penalties = penalties;
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
        return "Regulation{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", regulationType=" + regulationType +
                ", jurisdiction='" + jurisdiction + '\'' +
                '}';
    }
}
```

## 2. Update Root POM

**Update `/pom.xml`** to include Compliance modules:

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
</modules>
```

## Summary

The complete Compliance & Audit Management bounded context provides:

1. **Audit Trail Management**:
   - Comprehensive audit logging with immutable entries
   - Support for all user and system actions
   - Complete audit trail for compliance
   - Session and IP tracking
   - Before/after value tracking

2. **Compliance Requirements**:
   - Requirement lifecycle (Pending → In Progress → Compliant/Non-Compliant)
   - Regulation association
   - Control mapping
   - Assessment and validation tracking
   - Evidence requirements

3. **Regulation Management**:
   - Support for multiple regulatory frameworks (GDPR, CCPA, HIPAA, SOX, PCI-DSS, etc.)
   - Jurisdiction and industry applicability
   - Documentation and reference materials

4. **Internal Controls**:
   - Control types (Segregation of Duties, Authorization, Record Keeping, etc.)
   - Mapping to compliance requirements
   - Control effectiveness tracking

5. **Security and Privacy**:
   - User action auditing
   - Data access tracking
   - Permission change auditing
   - MFA status tracking

6. **Reporting**:
   - Compliance status reports
   - Audit log search and retrieval
   - Overdue requirement tracking

This completes the Compliance & Audit Management context with comprehensive regulatory compliance and audit capabilities that enable the ERP system to meet regulatory requirements and maintain proper audit trails.
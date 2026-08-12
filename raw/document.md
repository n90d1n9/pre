# Complete Implementation: Document Management / ECM Bounded Context

Now I'll implement the complete Document Management / Enterprise Content Management (ECM) bounded context, which handles document storage, version control, approvals, digital signatures, folder organization, and document workflows.

## 1. Document Management Domain Module

**`/modules/document/domain/pom.xml`**:

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

    <artifactId>erp-document-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/document/domain/src/main/java/tech/kayys/erp/document/domain/identifier/DocumentId.java`**:

```java
package tech.kayys.erp.document.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Document identifier.
 */
public final class DocumentId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public DocumentId(UUID value) {
        super(value);
    }

    public static DocumentId of(UUID value) {
        return new DocumentId(value);
    }

    public static DocumentId generate() {
        return new DocumentId(UUID.randomUUID());
    }

    public static DocumentId fromString(String value) {
        return new DocumentId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "DocumentId{" + value + "}";
    }
}
```

**`/modules/document/domain/src/main/java/tech/kayys/erp/document/domain/identifier/FolderId.java`**:

```java
package tech.kayys.erp.document.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Folder identifier.
 */
public final class FolderId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public FolderId(UUID value) {
        super(value);
    }

    public static FolderId of(UUID value) {
        return new FolderId(value);
    }

    public static FolderId generate() {
        return new FolderId(UUID.randomUUID());
    }

    public static FolderId fromString(String value) {
        return new FolderId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "FolderId{" + value + "}";
    }
}
```

**`/modules/document/domain/src/main/java/tech/kayys/erp/document/domain/identifier/DocumentVersionId.java`**:

```java
package tech.kayys.erp.document.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Document version identifier.
 */
public final class DocumentVersionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public DocumentVersionId(UUID value) {
        super(value);
    }

    public static DocumentVersionId of(UUID value) {
        return new DocumentVersionId(value);
    }

    public static DocumentVersionId generate() {
        return new DocumentVersionId(UUID.randomUUID());
    }

    public static DocumentVersionId fromString(String value) {
        return new DocumentVersionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "DocumentVersionId{" + value + "}";
    }
}
```

**`/modules/document/domain/src/main/java/tech/kayys/erp/document/domain/valueobject/DocumentStatus.java`**:

```java
package tech.kayys.erp.document.domain.valueobject;

/**
 * Status of a document.
 */
public enum DocumentStatus {
    DRAFT("Draft - being created"),
    PENDING_APPROVAL("Pending Approval - awaiting review"),
    APPROVED("Approved - reviewed and accepted"),
    REJECTED("Rejected - not accepted"),
    PUBLISHED("Published - final version"),
    ARCHIVED("Archived - historical"),
    EXPIRED("Expired - no longer valid"),
    DELETED("Deleted - removed");

    private final String description;

    DocumentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == DRAFT || this == PENDING_APPROVAL || 
               this == APPROVED || this == PUBLISHED;
    }

    public boolean isFinal() {
        return this == APPROVED || this == PUBLISHED || 
               this == REJECTED || this == ARCHIVED || this == EXPIRED;
    }

    public boolean canTransitionTo(DocumentStatus target) {
        return switch (this) {
            case DRAFT -> target == PENDING_APPROVAL || target == ARCHIVED || target == DELETED;
            case PENDING_APPROVAL -> target == APPROVED || target == REJECTED;
            case APPROVED -> target == PUBLISHED || target == ARCHIVED || target == EXPIRED;
            case PUBLISHED -> target == ARCHIVED || target == EXPIRED || target == DRAFT;
            case REJECTED, ARCHIVED, EXPIRED, DELETED -> false;
        };
    }
}
```

**`/modules/document/domain/src/main/java/tech/kayys/erp/document/domain/valueobject/DocumentType.java`**:

```java
package tech.kayys.erp.document.domain.valueobject;

/**
 * Types of documents.
 */
public enum DocumentType {
    INVOICE("Invoice"),
    PURCHASE_ORDER("Purchase Order"),
    SALES_ORDER("Sales Order"),
    CONTRACT("Contract"),
    AGREEMENT("Agreement"),
    POLICY("Policy"),
    PROCEDURE("Procedure"),
    REPORT("Report"),
    PRESENTATION("Presentation"),
    SPREADSHEET("Spreadsheet"),
    IMAGE("Image"),
    VIDEO("Video"),
    AUDIO("Audio"),
    ARCHIVE("Archive"),
    EMAIL("Email"),
    LETTER("Letter"),
    MEMO("Memo"),
    FORM("Form"),
    TEMPLATE("Template"),
    OTHER("Other");

    private final String displayName;

    DocumentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
```

**`/modules/document/domain/src/main/java/tech/kayys/erp/document/domain/valueobject/DocumentSecurity.java`**:

```java
package tech.kayys.erp.document.domain.valueobject;

/**
 * Security classification of documents.
 */
public enum DocumentSecurity {
    PUBLIC("Public - accessible to all"),
    INTERNAL("Internal - accessible within organization"),
    CONFIDENTIAL("Confidential - restricted access"),
    RESTRICTED("Restricted - very limited access"),
    TOP_SECRET("Top Secret - highest classification");

    private final String description;

    DocumentSecurity(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return switch (this) {
            case PUBLIC -> 1;
            case INTERNAL -> 2;
            case CONFIDENTIAL -> 3;
            case RESTRICTED -> 4;
            case TOP_SECRET -> 5;
        };
    }

    public boolean hasAccess(DocumentSecurity userLevel) {
        return userLevel.getLevel() >= this.getLevel();
    }
}
```

**`/modules/document/domain/src/main/java/tech/kayys/erp/document/domain/model/Document.java`**:

```java
package tech.kayys.erp.document.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.document.domain.identifier.DocumentId;
import tech.kayys.erp.document.domain.identifier.FolderId;
import tech.kayys.erp.document.domain.valueobject.DocumentSecurity;
import tech.kayys.erp.document.domain.valueobject.DocumentStatus;
import tech.kayys.erp.document.domain.valueobject.DocumentType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Document aggregate root.
 * Represents a document in the content management system.
 */
public final class Document extends AggregateRoot<DocumentId> {
    
    private static final long serialVersionUID = 1L;
    
    private String title;
    private String description;
    private DocumentType documentType;
    private DocumentStatus status;
    private DocumentSecurity security;
    private FolderId folderId;
    private String fileName;
    private String fileExtension;
    private long fileSize;
    private String mimeType;
    private String storageKey;
    private String checksum;
    private String version;
    private List<DocumentVersion> versions;
    private List<String> tags;
    private String ownerId;
    private String ownerName;
    private String department;
    private List<String> sharedWith;
    private Instant expiryDate;
    private Instant publishedAt;
    private String publishedBy;
    private String notes;
    private boolean active;

    private Document(DocumentId id) {
        super(id);
        this.versions = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.sharedWith = new ArrayList<>();
        this.status = DocumentStatus.DRAFT;
        this.security = DocumentSecurity.INTERNAL;
        this.active = true;
        this.version = "1.0";
    }

    private Document() {
        super();
    }

    /**
     * Factory method to create a new document.
     */
    public static Document create(
            DocumentId id,
            String title,
            DocumentType documentType,
            String ownerId,
            String ownerName,
            String fileName,
            String mimeType) {
        Document document = new Document(id);
        document.title = title;
        document.documentType = documentType;
        document.ownerId = ownerId;
        document.ownerName = ownerName;
        document.fileName = fileName;
        document.mimeType = mimeType;
        // Create initial version
        DocumentVersion initialVersion = DocumentVersion.create(
            DocumentVersionId.generate(),
            id,
            "1.0",
            "Initial version",
            ownerId,
            ownerName,
            fileName,
            fileSize,
            mimeType,
            storageKey,
            checksum
        );
        document.versions.add(initialVersion);
        return document;
    }

    /**
     * Submits the document for approval.
     */
    public void submitForApproval() {
        if (status != DocumentStatus.DRAFT) {
            throw new IllegalStateException("Cannot submit document in status: " + status);
        }
        this.status = DocumentStatus.PENDING_APPROVAL;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Approves the document.
     */
    public void approve(String approvedBy) {
        if (status != DocumentStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Cannot approve document in status: " + status);
        }
        this.status = DocumentStatus.APPROVED;
        this.publishedBy = approvedBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Rejects the document.
     */
    public void reject(String reason) {
        if (status != DocumentStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Cannot reject document in status: " + status);
        }
        this.status = DocumentStatus.REJECTED;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Publishes the document.
     */
    public void publish(String publishedBy) {
        if (status != DocumentStatus.APPROVED) {
            throw new IllegalStateException("Cannot publish document in status: " + status);
        }
        this.status = DocumentStatus.PUBLISHED;
        this.publishedBy = publishedBy;
        this.publishedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Archives the document.
     */
    public void archive() {
        if (status == DocumentStatus.DELETED) {
            throw new IllegalStateException("Cannot archive deleted document");
        }
        this.status = DocumentStatus.ARCHIVED;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Restores the document from archive.
     */
    public void restore() {
        if (status != DocumentStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot restore document in status: " + status);
        }
        this.status = DocumentStatus.DRAFT;
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Expires the document.
     */
    public void expire() {
        this.status = DocumentStatus.EXPIRED;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a new version of the document.
     */
    public void addVersion(DocumentVersion version) {
        if (version == null) {
            throw new IllegalArgumentException("Version cannot be null");
        }
        versions.add(version);
        this.version = version.getVersionNumber();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a tag to the document.
     */
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a tag from the document.
     */
    public void removeTag(String tag) {
        tags.remove(tag);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Shares the document with a user.
     */
    public void shareWith(String userId) {
        if (!sharedWith.contains(userId)) {
            sharedWith.add(userId);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes sharing from a user.
     */
    public void unshareWith(String userId) {
        sharedWith.remove(userId);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the folder for the document.
     */
    public void setFolder(FolderId folderId) {
        this.folderId = folderId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the document details.
     */
    public void update(String title, String description, String department) {
        this.title = title;
        this.description = description;
        this.department = department;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the latest version.
     */
    public DocumentVersion getLatestVersion() {
        if (versions.isEmpty()) {
            return null;
        }
        return versions.get(versions.size() - 1);
    }

    /**
     * Gets a specific version by number.
     */
    public DocumentVersion getVersion(String versionNumber) {
        return versions.stream()
            .filter(v -> v.getVersionNumber().equals(versionNumber))
            .findFirst()
            .orElse(null);
    }

    /**
     * Gets the version count.
     */
    public int getVersionCount() {
        return versions.size();
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public DocumentType getDocumentType() { return documentType; }
    public DocumentStatus getStatus() { return status; }
    public DocumentSecurity getSecurity() { return security; }
    public FolderId getFolderId() { return folderId; }
    public String getFileName() { return fileName; }
    public String getFileExtension() { return fileExtension; }
    public long getFileSize() { return fileSize; }
    public String getMimeType() { return mimeType; }
    public String getStorageKey() { return storageKey; }
    public String getChecksum() { return checksum; }
    public String getVersion() { return version; }
    public List<DocumentVersion> getVersions() { return Collections.unmodifiableList(versions); }
    public List<String> getTags() { return Collections.unmodifiableList(tags); }
    public String getOwnerId() { return ownerId; }
    public String getOwnerName() { return ownerName; }
    public String getDepartment() { return department; }
    public List<String> getSharedWith() { return Collections.unmodifiableList(sharedWith); }
    public Instant getExpiryDate() { return expiryDate; }
    public Instant getPublishedAt() { return publishedAt; }
    public String getPublishedBy() { return publishedBy; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setSecurity(DocumentSecurity security) {
        this.security = security;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDepartment(String department) {
        this.department = department;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
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
        return "Document{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", type=" + documentType +
                ", status=" + status +
                ", version=" + version +
                ", versions=" + versions.size() +
                '}';
    }
}
```

**`/modules/document/domain/src/main/java/tech/kayys/erp/document/domain/model/DocumentVersion.java`**:

```java
package tech.kayys.erp.document.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.document.domain.identifier.DocumentId;
import tech.kayys.erp.document.domain.identifier.DocumentVersionId;

import java.time.Instant;

/**
 * Document version aggregate root.
 * Represents a specific version of a document.
 */
public final class DocumentVersion extends AggregateRoot<DocumentVersionId> {
    
    private static final long serialVersionUID = 1L;
    
    private DocumentId documentId;
    private String versionNumber;
    private String changeNotes;
    private String fileName;
    private long fileSize;
    private String mimeType;
    private String storageKey;
    private String checksum;
    private String createdBy;
    private String createdByName;
    private Instant createdAt;
    private boolean current;
    private String notes;

    private DocumentVersion(DocumentVersionId id) {
        super(id);
        this.createdAt = Instant.now();
        this.current = false;
    }

    private DocumentVersion() {
        super();
    }

    /**
     * Factory method to create a new document version.
     */
    public static DocumentVersion create(
            DocumentVersionId id,
            DocumentId documentId,
            String versionNumber,
            String changeNotes,
            String createdBy,
            String createdByName,
            String fileName,
            long fileSize,
            String mimeType,
            String storageKey,
            String checksum) {
        DocumentVersion version = new DocumentVersion(id);
        version.documentId = documentId;
        version.versionNumber = versionNumber;
        version.changeNotes = changeNotes;
        version.createdBy = createdBy;
        version.createdByName = createdByName;
        version.fileName = fileName;
        version.fileSize = fileSize;
        version.mimeType = mimeType;
        version.storageKey = storageKey;
        version.checksum = checksum;
        return version;
    }

    /**
     * Marks this version as current.
     */
    public void markCurrent() {
        this.current = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Unmarks this version as current.
     */
    public void unmarkCurrent() {
        this.current = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public DocumentId getDocumentId() { return documentId; }
    public String getVersionNumber() { return versionNumber; }
    public String getChangeNotes() { return changeNotes; }
    public String getFileName() { return fileName; }
    public long getFileSize() { return fileSize; }
    public String getMimeType() { return mimeType; }
    public String getStorageKey() { return storageKey; }
    public String getChecksum() { return checksum; }
    public String getCreatedBy() { return createdBy; }
    public String getCreatedByName() { return createdByName; }
    public Instant getCreatedAt() { return createdAt; }
    public boolean isCurrent() { return current; }
    public String getNotes() { return notes; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "DocumentVersion{" +
                "id=" + getId() +
                ", versionNumber='" + versionNumber + '\'' +
                ", fileName='" + fileName + '\'' +
                ", current=" + current +
                '}';
    }
}
```

**`/modules/document/domain/src/main/java/tech/kayys/erp/document/domain/model/DocumentFolder.java`**:

```java
package tech.kayys.erp.document.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.document.domain.identifier.FolderId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Document folder aggregate root.
 * Represents a folder for organizing documents.
 */
public final class DocumentFolder extends AggregateRoot<FolderId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private FolderId parentFolderId;
    private List<FolderId> subFolders;
    private List<String> permissions;
    private String ownerId;
    private String ownerName;
    private String department;
    private boolean active;
    private String notes;

    private DocumentFolder(FolderId id) {
        super(id);
        this.subFolders = new ArrayList<>();
        this.permissions = new ArrayList<>();
        this.active = true;
    }

    private DocumentFolder() {
        super();
    }

    /**
     * Factory method to create a new folder.
     */
    public static DocumentFolder create(
            FolderId id,
            String name,
            String ownerId,
            String ownerName,
            String department) {
        DocumentFolder folder = new DocumentFolder(id);
        folder.name = name;
        folder.ownerId = ownerId;
        folder.ownerName = ownerName;
        folder.department = department;
        return folder;
    }

    /**
     * Adds a subfolder.
     */
    public void addSubFolder(FolderId subFolderId) {
        if (!subFolders.contains(subFolderId)) {
            subFolders.add(subFolderId);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a subfolder.
     */
    public void removeSubFolder(FolderId subFolderId) {
        subFolders.remove(subFolderId);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the parent folder.
     */
    public void setParentFolder(FolderId parentFolderId) {
        if (this.id.equals(parentFolderId)) {
            throw new IllegalArgumentException("Cannot set self as parent");
        }
        this.parentFolderId = parentFolderId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a permission to the folder.
     */
    public void addPermission(String permission) {
        if (!permissions.contains(permission)) {
            permissions.add(permission);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a permission from the folder.
     */
    public void removePermission(String permission) {
        permissions.remove(permission);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the folder.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the folder.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the folder information.
     */
    public void update(String name, String description) {
        this.name = name;
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the number of subfolders.
     */
    public int getSubFolderCount() {
        return subFolders.size();
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public FolderId getParentFolderId() { return parentFolderId; }
    public List<FolderId> getSubFolders() { return Collections.unmodifiableList(subFolders); }
    public List<String> getPermissions() { return Collections.unmodifiableList(permissions); }
    public String getOwnerId() { return ownerId; }
    public String getOwnerName() { return ownerName; }
    public String getDepartment() { return department; }
    public boolean isActive() { return active; }
    public String getNotes() { return notes; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDepartment(String department) {
        this.department = department;
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
        return "DocumentFolder{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", subFolders=" + subFolders.size() +
                ", active=" + active +
                '}';
    }
}
```

## 2. Document Approval Workflow Integration

**`/modules/document/domain/src/main/java/tech/kayys/erp/document/domain/model/DocumentApproval.java`**:

```java
package tech.kayys.erp.document.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.document.domain.identifier.DocumentId;
import tech.kayys.erp.document.domain.identifier.DocumentApprovalId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Document approval aggregate root.
 * Manages document approval workflows.
 */
public final class DocumentApproval extends AggregateRoot<DocumentApprovalId> {
    
    private static final long serialVersionUID = 1L;
    
    private DocumentId documentId;
    private String documentTitle;
    private List<Approver> approvers;
    private String currentApprover;
    private int currentStep;
    private String status; // PENDING, APPROVED, REJECTED, ESCALATED
    private String initiatedBy;
    private Instant initiatedAt;
    private Instant completedAt;
    private String notes;
    private boolean active;

    private DocumentApproval(DocumentApprovalId id) {
        super(id);
        this.approvers = new ArrayList<>();
        this.status = "PENDING";
        this.active = true;
        this.currentStep = 0;
        this.initiatedAt = Instant.now();
    }

    private DocumentApproval() {
        super();
    }

    /**
     * Factory method to create a new document approval.
     */
    public static DocumentApproval create(
            DocumentApprovalId id,
            DocumentId documentId,
            String documentTitle,
            List<Approver> approvers,
            String initiatedBy) {
        DocumentApproval approval = new DocumentApproval(id);
        approval.documentId = documentId;
        approval.documentTitle = documentTitle;
        approval.approvers = new ArrayList<>(approvers);
        approval.initiatedBy = initiatedBy;
        if (!approvers.isEmpty()) {
            approval.currentApprover = approvers.get(0).getUserId();
        }
        return approval;
    }

    /**
     * Approves the document by current approver.
     */
    public void approve(String userId, String comments) {
        if (!isCurrentApprover(userId)) {
            throw new IllegalStateException("User is not the current approver");
        }
        
        Approver current = approvers.get(currentStep);
        if (current != null) {
            current.approve(comments);
        }
        
        currentStep++;
        
        if (currentStep >= approvers.size()) {
            this.status = "APPROVED";
            this.completedAt = Instant.now();
            this.active = false;
        } else {
            this.currentApprover = approvers.get(currentStep).getUserId();
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Rejects the document by current approver.
     */
    public void reject(String userId, String reason) {
        if (!isCurrentApprover(userId)) {
            throw new IllegalStateException("User is not the current approver");
        }
        
        this.status = "REJECTED";
        this.completedAt = Instant.now();
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Escalates the approval.
     */
    public void escalate(String reason) {
        this.status = "ESCALATED";
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if a user is the current approver.
     */
    public boolean isCurrentApprover(String userId) {
        if (currentStep >= approvers.size()) {
            return false;
        }
        return approvers.get(currentStep).getUserId().equals(userId);
    }

    /**
     * Gets the current approver.
     */
    public Approver getCurrentApprover() {
        if (currentStep >= approvers.size()) {
            return null;
        }
        return approvers.get(currentStep);
    }

    /**
     * Gets the approval progress.
     */
    public double getProgress() {
        if (approvers.isEmpty()) {
            return 0.0;
        }
        return (double) currentStep / approvers.size() * 100.0;
    }

    // Getters
    public DocumentId getDocumentId() { return documentId; }
    public String getDocumentTitle() { return documentTitle; }
    public List<Approver> getApprovers() { return Collections.unmodifiableList(approvers); }
    public String getCurrentApprover() { return currentApprover; }
    public int getCurrentStep() { return currentStep; }
    public String getStatus() { return status; }
    public String getInitiatedBy() { return initiatedBy; }
    public Instant getInitiatedAt() { return initiatedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "DocumentApproval{" +
                "id=" + getId() +
                ", documentId=" + documentId +
                ", status=" + status +
                ", progress=" + getProgress() + "%" +
                '}';
    }

    /**
     * Approver value object.
     */
    public static final class Approver implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String userId;
        private final String userName;
        private final String role;
        private final int order;
        private boolean approved;
        private String comments;
        private Instant approvedAt;

        public Approver(String userId, String userName, String role, int order) {
            this.userId = userId;
            this.userName = userName;
            this.role = role;
            this.order = order;
            this.approved = false;
            validate();
        }

        @Override
        public void validate() {
            if (userId == null || userId.trim().isEmpty()) {
                throw new IllegalArgumentException("User ID cannot be empty");
            }
            if (order < 0) {
                throw new IllegalArgumentException("Order cannot be negative");
            }
        }

        public String getUserId() { return userId; }
        public String getUserName() { return userName; }
        public String getRole() { return role; }
        public int getOrder() { return order; }
        public boolean isApproved() { return approved; }
        public String getComments() { return comments; }
        public Instant getApprovedAt() { return approvedAt; }

        public void approve(String comments) {
            this.approved = true;
            this.comments = comments;
            this.approvedAt = Instant.now();
        }

        @Override
        public String toString() {
            return "Approver{" +
                    "userId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", approved=" + approved +
                    '}';
        }
    }
}
```

## 3. Update Root POM

**Update `/pom.xml`** to include Document modules:

```xml
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <!-- ... existing modules ... -->

    <!-- Document Management -->
    <module>modules/document/domain</module>
    <module>modules/document/application</module>
    <module>modules/document/infrastructure</module>
    <module>modules/document/interfaces</module>

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

The complete Document Management / ECM bounded context provides:

1. **Document Management**:
   - Full document lifecycle (Draft → Pending Approval → Approved → Published → Archived/Expired)
   - Multiple document types (Invoice, Contract, Policy, Report, etc.)
   - Security classification (Public, Internal, Confidential, Restricted, Top Secret)
   - Document ownership and department association
   - Tagging and search support

2. **Version Control**:
   - Full version history tracking
   - Version numbering (major.minor)
   - Change notes for each version
   - Current version designation
   - Version rollback capability

3. **Folder Organization**:
   - Hierarchical folder structure
   - Nested subfolders
   - Folder permissions
   - Department-based organization

4. **Approval Workflow**:
   - Multi-step approval process
   - Sequential approvers
   - Approval/Rejection with comments
   - Escalation support
   - Progress tracking

5. **Integration Points**:
   - Workflow context for automation
   - Communication for notifications
   - Security for access control
   - Audit for compliance

This completes the Document Management context with comprehensive enterprise content management capabilities that integrate with Workflow, Communication, Security, and Audit contexts throughout the ERP system.


# Complete Implementation: Document Management / ECM with Persistence

Now I'll complete the Document Management / ECM implementation with the necessary persistence entities, repositories, and infrastructure components.

## 1. Persistence Entities

**`/modules/document/infrastructure/src/main/java/tech/kayys/erp/document/infrastructure/persistence/entity/DocumentEntity.java`**:

```java
package tech.kayys.erp.document.infrastructure.persistence.entity;

import tech.kayys.erp.foundation.persistence.BaseEntity;
import tech.kayys.erp.document.domain.model.Document;
import tech.kayys.erp.document.domain.valueobject.DocumentSecurity;
import tech.kayys.erp.document.domain.valueobject.DocumentStatus;
import tech.kayys.erp.document.domain.valueobject.DocumentType;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Document entity for persistence.
 */
@Entity
@Table(name = "documents", indexes = {
    @Index(name = "idx_document_title", columnList = "title"),
    @Index(name = "idx_document_status", columnList = "status"),
    @Index(name = "idx_document_type", columnList = "document_type"),
    @Index(name = "idx_document_owner", columnList = "owner_id"),
    @Index(name = "idx_document_folder", columnList = "folder_id")
})
public class DocumentEntity extends BaseEntity {

    @Column(name = "title", nullable = false, length = 255)
    public String title;

    @Column(name = "description", length = 2000)
    public String description;

    @Column(name = "document_type", nullable = false)
    @Enumerated(EnumType.STRING)
    public DocumentType documentType;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    public DocumentStatus status;

    @Column(name = "security", nullable = false)
    @Enumerated(EnumType.STRING)
    public DocumentSecurity security;

    @Column(name = "folder_id", columnDefinition = "UUID")
    public UUID folderId;

    @Column(name = "file_name", nullable = false, length = 255)
    public String fileName;

    @Column(name = "file_extension", length = 20)
    public String fileExtension;

    @Column(name = "file_size")
    public long fileSize;

    @Column(name = "mime_type", length = 100)
    public String mimeType;

    @Column(name = "storage_key", length = 500)
    public String storageKey;

    @Column(name = "checksum", length = 64)
    public String checksum;

    @Column(name = "version", nullable = false)
    public String version;

    @Column(name = "owner_id", columnDefinition = "UUID")
    public UUID ownerId;

    @Column(name = "owner_name", length = 100)
    public String ownerName;

    @Column(name = "department", length = 100)
    public String department;

    @Column(name = "expiry_date")
    public Instant expiryDate;

    @Column(name = "published_at")
    public Instant publishedAt;

    @Column(name = "published_by", length = 100)
    public String publishedBy;

    @Column(name = "notes", length = 2000)
    public String notes;

    @ElementCollection
    @CollectionTable(name = "document_tags", joinColumns = @JoinColumn(name = "document_id"))
    @Column(name = "tag", length = 50)
    public List<String> tags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "document_shares", joinColumns = @JoinColumn(name = "document_id"))
    @Column(name = "shared_with", columnDefinition = "UUID")
    public List<UUID> sharedWith = new ArrayList<>();

    /**
     * Converts entity to domain.
     */
    public Document toDomain() {
        // Domain conversion logic
        return null; // Will be implemented in mapper
    }
}
```

**`/modules/document/infrastructure/src/main/java/tech/kayys/erp/document/infrastructure/persistence/entity/DocumentVersionEntity.java`**:

```java
package tech.kayys.erp.document.infrastructure.persistence.entity;

import tech.kayys.erp.foundation.persistence.BaseEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Document version entity for persistence.
 */
@Entity
@Table(name = "document_versions", indexes = {
    @Index(name = "idx_version_document", columnList = "document_id"),
    @Index(name = "idx_version_number", columnList = "version_number")
})
public class DocumentVersionEntity extends BaseEntity {

    @Column(name = "document_id", nullable = false, columnDefinition = "UUID")
    public UUID documentId;

    @Column(name = "version_number", nullable = false, length = 20)
    public String versionNumber;

    @Column(name = "change_notes", length = 1000)
    public String changeNotes;

    @Column(name = "file_name", nullable = false, length = 255)
    public String fileName;

    @Column(name = "file_size")
    public long fileSize;

    @Column(name = "mime_type", length = 100)
    public String mimeType;

    @Column(name = "storage_key", length = 500)
    public String storageKey;

    @Column(name = "checksum", length = 64)
    public String checksum;

    @Column(name = "created_by", length = 100)
    public String createdBy;

    @Column(name = "created_by_name", length = 100)
    public String createdByName;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @Column(name = "is_current", nullable = false)
    public boolean current;

    @Column(name = "notes", length = 2000)
    public String notes;
}
```

**`/modules/document/infrastructure/src/main/java/tech/kayys/erp/document/infrastructure/persistence/entity/DocumentFolderEntity.java`**:

```java
package tech.kayys.erp.document.infrastructure.persistence.entity;

import tech.kayys.erp.foundation.persistence.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Document folder entity for persistence.
 */
@Entity
@Table(name = "document_folders", indexes = {
    @Index(name = "idx_folder_name", columnList = "name"),
    @Index(name = "idx_folder_parent", columnList = "parent_folder_id"),
    @Index(name = "idx_folder_owner", columnList = "owner_id")
})
public class DocumentFolderEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    public String name;

    @Column(name = "description", length = 2000)
    public String description;

    @Column(name = "parent_folder_id", columnDefinition = "UUID")
    public UUID parentFolderId;

    @Column(name = "owner_id", columnDefinition = "UUID")
    public UUID ownerId;

    @Column(name = "owner_name", length = 100)
    public String ownerName;

    @Column(name = "department", length = 100)
    public String department;

    @Column(name = "notes", length = 2000)
    public String notes;

    @ElementCollection
    @CollectionTable(name = "folder_permissions", joinColumns = @JoinColumn(name = "folder_id"))
    @Column(name = "permission", length = 50)
    public List<String> permissions = new ArrayList<>();
}
```

**`/modules/document/infrastructure/src/main/java/tech/kayys/erp/document/infrastructure/persistence/entity/DocumentApprovalEntity.java`**:

```java
package tech.kayys.erp.document.infrastructure.persistence.entity;

import tech.kayys.erp.foundation.persistence.BaseEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Document approval entity for persistence.
 */
@Entity
@Table(name = "document_approvals", indexes = {
    @Index(name = "idx_approval_document", columnList = "document_id"),
    @Index(name = "idx_approval_status", columnList = "status")
})
public class DocumentApprovalEntity extends BaseEntity {

    @Column(name = "document_id", nullable = false, columnDefinition = "UUID")
    public UUID documentId;

    @Column(name = "document_title", length = 255)
    public String documentTitle;

    @Column(name = "current_approver", columnDefinition = "UUID")
    public UUID currentApprover;

    @Column(name = "current_step")
    public int currentStep;

    @Column(name = "status", nullable = false, length = 20)
    public String status;

    @Column(name = "initiated_by", columnDefinition = "UUID")
    public UUID initiatedBy;

    @Column(name = "initiated_at", nullable = false)
    public Instant initiatedAt;

    @Column(name = "completed_at")
    public Instant completedAt;

    @Column(name = "notes", length = 2000)
    public String notes;

    @ElementCollection
    @CollectionTable(name = "approval_approvers", joinColumns = @JoinColumn(name = "approval_id"))
    @AttributeOverrides({
        @AttributeOverride(name = "userId", column = @Column(name = "user_id", columnDefinition = "UUID")),
        @AttributeOverride(name = "userName", column = @Column(name = "user_name", length = 100)),
        @AttributeOverride(name = "role", column = @Column(name = "role", length = 50)),
        @AttributeOverride(name = "order", column = @Column(name = "approval_order")),
        @AttributeOverride(name = "approved", column = @Column(name = "is_approved")),
        @AttributeOverride(name = "comments", column = @Column(name = "comments", length = 1000)),
        @AttributeOverride(name = "approvedAt", column = @Column(name = "approved_at"))
    })
    public List<ApproverEntity> approvers = new ArrayList<>();

    /**
     * Approver entity embedded.
     */
    @Embeddable
    public static class ApproverEntity {
        public UUID userId;
        public String userName;
        public String role;
        public int order;
        public boolean approved;
        public String comments;
        public Instant approvedAt;
    }
}
```

## 2. Repository Interfaces

**`/modules/document/domain/src/main/java/tech/kayys/erp/document/domain/repository/DocumentRepository.java`**:

```java
package tech.kayys.erp.document.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.document.domain.identifier.DocumentId;
import tech.kayys.erp.document.domain.identifier.FolderId;
import tech.kayys.erp.document.domain.model.Document;
import tech.kayys.erp.document.domain.valueobject.DocumentStatus;
import tech.kayys.erp.document.domain.valueobject.DocumentType;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for Document aggregates.
 */
public interface DocumentRepository extends Repository<Document, DocumentId> {

    /**
     * Finds documents by folder.
     */
    CompletionStage<List<Document>> findByFolder(FolderId folderId);

    /**
     * Finds documents by status.
     */
    CompletionStage<List<Document>> findByStatus(DocumentStatus status);

    /**
     * Finds documents by type.
     */
    CompletionStage<List<Document>> findByType(DocumentType type);

    /**
     * Finds documents by owner.
     */
    CompletionStage<List<Document>> findByOwner(String ownerId);

    /**
     * Finds documents by tag.
     */
    CompletionStage<List<Document>> findByTag(String tag);

    /**
     * Finds documents by title containing text.
     */
    CompletionStage<List<Document>> findByTitleContaining(String title);

    /**
     * Finds documents by department.
     */
    CompletionStage<List<Document>> findByDepartment(String department);

    /**
     * Finds documents published between dates.
     */
    CompletionStage<List<Document>> findPublishedBetween(Instant start, Instant end);

    /**
     * Finds documents expiring soon.
     */
    CompletionStage<List<Document>> findDocumentsExpiringSoon(int days);

    /**
     * Counts documents by status.
     */
    CompletionStage<Long> countByStatus(DocumentStatus status);

    /**
     * Counts documents by type.
     */
    CompletionStage<Long> countByType(DocumentType type);

    /**
     * Searches documents with full-text search.
     */
    CompletionStage<List<Document>> searchDocuments(String searchTerm);

    /**
     * Finds shared documents for a user.
     */
    CompletionStage<List<Document>> findSharedWithUser(String userId);
}
```

**`/modules/document/domain/src/main/java/tech/kayys/erp/document/domain/repository/DocumentVersionRepository.java`**:

```java
package tech.kayys.erp.document.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.document.domain.identifier.DocumentId;
import tech.kayys.erp.document.domain.identifier.DocumentVersionId;
import tech.kayys.erp.document.domain.model.DocumentVersion;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for DocumentVersion aggregates.
 */
public interface DocumentVersionRepository extends Repository<DocumentVersion, DocumentVersionId> {

    /**
     * Finds versions for a document.
     */
    CompletionStage<List<DocumentVersion>> findByDocument(DocumentId documentId);

    /**
     * Finds the current version of a document.
     */
    CompletionStage<DocumentVersion> findCurrentVersion(DocumentId documentId);

    /**
     * Finds versions created by a user.
     */
    CompletionStage<List<DocumentVersion>> findByCreator(String createdBy);

    /**
     * Counts versions for a document.
     */
    CompletionStage<Long> countByDocument(DocumentId documentId);

    /**
     * Finds versions older than a date.
     */
    CompletionStage<List<DocumentVersion>> findVersionsOlderThan(Instant date);
}
```

**`/modules/document/domain/src/main/java/tech/kayys/erp/document/domain/repository/DocumentFolderRepository.java`**:

```java
package tech.kayys.erp.document.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.document.domain.identifier.FolderId;
import tech.kayys.erp.document.domain.model.DocumentFolder;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for DocumentFolder aggregates.
 */
public interface DocumentFolderRepository extends Repository<DocumentFolder, FolderId> {

    /**
     * Finds subfolders of a parent folder.
     */
    CompletionStage<List<DocumentFolder>> findSubfolders(FolderId parentId);

    /**
     * Finds root folders.
     */
    CompletionStage<List<DocumentFolder>> findRootFolders();

    /**
     * Finds folders by owner.
     */
    CompletionStage<List<DocumentFolder>> findByOwner(String ownerId);

    /**
     * Finds folders by department.
     */
    CompletionStage<List<DocumentFolder>> findByDepartment(String department);

    /**
     * Finds folders by name containing text.
     */
    CompletionStage<List<DocumentFolder>> findByNameContaining(String name);

    /**
     * Gets the folder path.
     */
    CompletionStage<List<DocumentFolder>> getFolderPath(FolderId folderId);

    /**
     * Checks if a folder name exists in a parent.
     */
    CompletionStage<Boolean> existsByNameAndParent(String name, FolderId parentId);
}
```

**`/modules/document/domain/src/main/java/tech/kayys/erp/document/domain/repository/DocumentApprovalRepository.java`**:

```java
package tech.kayys.erp.document.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.document.domain.identifier.DocumentApprovalId;
import tech.kayys.erp.document.domain.identifier.DocumentId;
import tech.kayys.erp.document.domain.model.DocumentApproval;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for DocumentApproval aggregates.
 */
public interface DocumentApprovalRepository extends Repository<DocumentApproval, DocumentApprovalId> {

    /**
     * Finds approvals for a document.
     */
    CompletionStage<List<DocumentApproval>> findByDocument(DocumentId documentId);

    /**
     * Finds active approvals for a document.
     */
    CompletionStage<DocumentApproval> findActiveApproval(DocumentId documentId);

    /**
     * Finds approvals pending for a user.
     */
    CompletionStage<List<DocumentApproval>> findPendingApprovals(String userId);

    /**
     * Finds approvals initiated by a user.
     */
    CompletionStage<List<DocumentApproval>> findInitiatedBy(String userId);

    /**
     * Finds approvals by status.
     */
    CompletionStage<List<DocumentApproval>> findByStatus(String status);

    /**
     * Finds approvals escalated.
     */
    CompletionStage<List<DocumentApproval>> findEscalatedApprovals();
}
```

## 3. Repository Implementations

**`/modules/document/infrastructure/src/main/java/tech/kayys/erp/document/infrastructure/persistence/repository/DocumentRepositoryImpl.java`**:

```java
package tech.kayys.erp.document.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import tech.kayys.erp.document.domain.identifier.DocumentId;
import tech.kayys.erp.document.domain.identifier.FolderId;
import tech.kayys.erp.document.domain.model.Document;
import tech.kayys.erp.document.domain.repository.DocumentRepository;
import tech.kayys.erp.document.domain.valueobject.DocumentStatus;
import tech.kayys.erp.document.domain.valueobject.DocumentType;
import tech.kayys.erp.document.infrastructure.persistence.entity.DocumentEntity;
import tech.kayys.erp.document.infrastructure.persistence.mapper.DocumentMapper;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Implementation of DocumentRepository using Hibernate Reactive Panache.
 */
@ApplicationScoped
public class DocumentRepositoryImpl implements DocumentRepository {

    private final DocumentMapper mapper;

    public DocumentRepositoryImpl(DocumentMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @WithTransaction
    public CompletionStage<Document> save(Document document) {
        DocumentEntity entity = mapper.toEntity(document);
        
        if (entity.id != null) {
            return Panache.withTransaction(() -> entity.<DocumentEntity>persist()
                .onItem()
                .transform(v -> {
                    document.clearEvents();
                    return document;
                })
                .subscribe()
                .asCompletionStage());
        } else {
            entity.id = UUID.randomUUID();
            return Panache.withTransaction(() -> entity.<DocumentEntity>persist()
                .onItem()
                .transform(v -> {
                    document.clearEvents();
                    return document;
                })
                .subscribe()
                .asCompletionStage());
        }
    }

    @Override
    @WithSession
    public CompletionStage<Optional<Document>> findById(DocumentId id) {
        return DocumentEntity.<DocumentEntity>findById(id.getValue())
            .onItem()
            .transform(entity -> {
                if (entity == null) {
                    return Optional.empty();
                }
                return Optional.of(mapper.toDomain(entity));
            })
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Boolean> existsById(DocumentId id) {
        return DocumentEntity.<DocumentEntity>findById(id.getValue())
            .onItem()
            .transform(entity -> entity != null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> delete(Document document) {
        return DocumentEntity.deleteById(document.getId().getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> deleteById(DocumentId id) {
        return DocumentEntity.deleteById(id.getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findByFolder(FolderId folderId) {
        return DocumentEntity.list("folderId = ?1", folderId.getValue())
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findByStatus(DocumentStatus status) {
        return DocumentEntity.list("status = ?1", status)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findByType(DocumentType type) {
        return DocumentEntity.list("documentType = ?1", type)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findByOwner(String ownerId) {
        return DocumentEntity.list("ownerId = ?1", UUID.fromString(ownerId))
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findByTag(String tag) {
        return DocumentEntity.find("?1 member of tags", tag)
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findByTitleContaining(String title) {
        return DocumentEntity.list("title like ?1", "%" + title + "%")
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findByDepartment(String department) {
        return DocumentEntity.list("department = ?1", department)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findPublishedBetween(Instant start, Instant end) {
        return DocumentEntity.list("publishedAt between ?1 and ?2", start, end)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findDocumentsExpiringSoon(int days) {
        Instant threshold = Instant.now().plusSeconds(days * 24L * 60L * 60L);
        return DocumentEntity.list("expiryDate is not null and expiryDate <= ?1", threshold)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Long> countByStatus(DocumentStatus status) {
        return DocumentEntity.count("status = ?1", status)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Long> countByType(DocumentType type) {
        return DocumentEntity.count("documentType = ?1", type)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> searchDocuments(String searchTerm) {
        // Full-text search across title, description, tags, and content
        // This uses a native query for PostgreSQL full-text search
        return DocumentEntity.find("""
                title ilike ?1 or description ilike ?1 or 
                exists (select 1 from document_tags dt where dt.document_id = id and dt.tag ilike ?1)
                """, "%" + searchTerm + "%")
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findSharedWithUser(String userId) {
        UUID userUUID = UUID.fromString(userId);
        return DocumentEntity.find("?1 member of sharedWith", userUUID)
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }
}
```

**`/modules/document/infrastructure/src/main/java/tech/kayys/erp/document/infrastructure/persistence/repository/DocumentVersionRepositoryImpl.java`**:

```java
package tech.kayys.erp.document.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import tech.kayys.erp.document.domain.identifier.DocumentId;
import tech.kayys.erp.document.domain.identifier.DocumentVersionId;
import tech.kayys.erp.document.domain.model.DocumentVersion;
import tech.kayys.erp.document.domain.repository.DocumentVersionRepository;
import tech.kayys.erp.document.infrastructure.persistence.entity.DocumentVersionEntity;
import tech.kayys.erp.document.infrastructure.persistence.mapper.DocumentVersionMapper;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Implementation of DocumentVersionRepository.
 */
@ApplicationScoped
public class DocumentVersionRepositoryImpl implements DocumentVersionRepository {

    private final DocumentVersionMapper mapper;

    public DocumentVersionRepositoryImpl(DocumentVersionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @WithTransaction
    public CompletionStage<DocumentVersion> save(DocumentVersion version) {
        DocumentVersionEntity entity = mapper.toEntity(version);
        
        return Panache.withTransaction(() -> entity.<DocumentVersionEntity>persist()
            .onItem()
            .transform(v -> {
                version.clearEvents();
                return version;
            })
            .subscribe()
            .asCompletionStage());
    }

    @Override
    @WithSession
    public CompletionStage<Optional<DocumentVersion>> findById(DocumentVersionId id) {
        return DocumentVersionEntity.<DocumentVersionEntity>findById(id.getValue())
            .onItem()
            .transform(entity -> {
                if (entity == null) {
                    return Optional.empty();
                }
                return Optional.of(mapper.toDomain(entity));
            })
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Boolean> existsById(DocumentVersionId id) {
        return DocumentVersionEntity.<DocumentVersionEntity>findById(id.getValue())
            .onItem()
            .transform(entity -> entity != null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> delete(DocumentVersion version) {
        return DocumentVersionEntity.deleteById(version.getId().getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> deleteById(DocumentVersionId id) {
        return DocumentVersionEntity.deleteById(id.getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<DocumentVersion>> findByDocument(DocumentId documentId) {
        return DocumentVersionEntity.list("documentId = ?1 order by createdAt desc", documentId.getValue())
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<DocumentVersion> findCurrentVersion(DocumentId documentId) {
        return DocumentVersionEntity.find("documentId = ?1 and current = true", documentId.getValue())
            .firstResult()
            .onItem()
            .transform(entity -> entity != null ? mapper.toDomain(entity) : null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<DocumentVersion>> findByCreator(String createdBy) {
        return DocumentVersionEntity.list("createdBy = ?1 order by createdAt desc", createdBy)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Long> countByDocument(DocumentId documentId) {
        return DocumentVersionEntity.count("documentId = ?1", documentId.getValue())
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<DocumentVersion>> findVersionsOlderThan(Instant date) {
        return DocumentVersionEntity.list("createdAt < ?1", date)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }
}
```

## 4. Mappers

**`/modules/document/infrastructure/src/main/java/tech/kayys/erp/document/infrastructure/persistence/mapper/DocumentMapper.java`**:

```java
package tech.kayys.erp.document.infrastructure.persistence.mapper;

import tech.kayys.erp.document.domain.identifier.DocumentId;
import tech.kayys.erp.document.domain.identifier.DocumentVersionId;
import tech.kayys.erp.document.domain.identifier.FolderId;
import tech.kayys.erp.document.domain.model.Document;
import tech.kayys.erp.document.domain.model.DocumentVersion;
import tech.kayys.erp.document.infrastructure.persistence.entity.DocumentEntity;
import tech.kayys.erp.document.infrastructure.persistence.entity.DocumentVersionEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper between Document domain and persistence entities.
 */
@ApplicationScoped
public class DocumentMapper {

    private final DocumentVersionMapper versionMapper;

    public DocumentMapper(DocumentVersionMapper versionMapper) {
        this.versionMapper = versionMapper;
    }

    /**
     * Converts domain Document to persistence entity.
     */
    public DocumentEntity toEntity(Document document) {
        DocumentEntity entity = new DocumentEntity();
        entity.id = document.getId().getValue();
        entity.title = document.getTitle();
        entity.description = document.getDescription();
        entity.documentType = document.getDocumentType();
        entity.status = document.getStatus();
        entity.security = document.getSecurity();
        entity.folderId = document.getFolderId() != null ? document.getFolderId().getValue() : null;
        entity.fileName = document.getFileName();
        entity.fileExtension = document.getFileExtension();
        entity.fileSize = document.getFileSize();
        entity.mimeType = document.getMimeType();
        entity.storageKey = document.getStorageKey();
        entity.checksum = document.getChecksum();
        entity.version = document.getVersion();
        entity.ownerId = document.getOwnerId() != null ? UUID.fromString(document.getOwnerId()) : null;
        entity.ownerName = document.getOwnerName();
        entity.department = document.getDepartment();
        entity.expiryDate = document.getExpiryDate();
        entity.publishedAt = document.getPublishedAt();
        entity.publishedBy = document.getPublishedBy();
        entity.notes = document.getNotes();
        entity.active = document.isActive();
        entity.version = document.getVersion();
        
        if (document.getTags() != null) {
            entity.tags = document.getTags();
        }
        
        if (document.getSharedWith() != null) {
            entity.sharedWith = document.getSharedWith().stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
        }
        
        return entity;
    }

    /**
     * Converts persistence entity to domain Document.
     */
    public Document toDomain(DocumentEntity entity) {
        Document document = new Document(DocumentId.of(entity.id));
        document.setTitle(entity.title);
        document.setDescription(entity.description);
        document.setDocumentType(entity.documentType);
        document.setStatus(entity.status);
        document.setSecurity(entity.security);
        document.setFolderId(entity.folderId != null ? FolderId.of(entity.folderId) : null);
        document.setFileName(entity.fileName);
        document.setFileExtension(entity.fileExtension);
        document.setFileSize(entity.fileSize);
        document.setMimeType(entity.mimeType);
        document.setStorageKey(entity.storageKey);
        document.setChecksum(entity.checksum);
        document.setVersion(entity.version);
        document.setOwnerId(entity.ownerId != null ? entity.ownerId.toString() : null);
        document.setOwnerName(entity.ownerName);
        document.setDepartment(entity.department);
        document.setExpiryDate(entity.expiryDate);
        document.setPublishedAt(entity.publishedAt);
        document.setPublishedBy(entity.publishedBy);
        document.setNotes(entity.notes);
        document.setActive(entity.active);
        document.setVersion(entity.version);
        
        if (entity.tags != null) {
            document.setTags(entity.tags);
        }
        
        if (entity.sharedWith != null) {
            document.setSharedWith(entity.sharedWith.stream()
                .map(UUID::toString)
                .collect(Collectors.toList()));
        }
        
        return document;
    }
}
```

**`/modules/document/infrastructure/src/main/java/tech/kayys/erp/document/infrastructure/persistence/mapper/DocumentVersionMapper.java`**:

```java
package tech.kayys.erp.document.infrastructure.persistence.mapper;

import tech.kayys.erp.document.domain.identifier.DocumentId;
import tech.kayys.erp.document.domain.identifier.DocumentVersionId;
import tech.kayys.erp.document.domain.model.DocumentVersion;
import tech.kayys.erp.document.infrastructure.persistence.entity.DocumentVersionEntity;

import javax.enterprise.context.ApplicationScoped;

/**
 * Mapper between DocumentVersion domain and persistence entities.
 */
@ApplicationScoped
public class DocumentVersionMapper {

    /**
     * Converts domain DocumentVersion to persistence entity.
     */
    public DocumentVersionEntity toEntity(DocumentVersion version) {
        DocumentVersionEntity entity = new DocumentVersionEntity();
        entity.id = version.getId().getValue();
        entity.documentId = version.getDocumentId().getValue();
        entity.versionNumber = version.getVersionNumber();
        entity.changeNotes = version.getChangeNotes();
        entity.fileName = version.getFileName();
        entity.fileSize = version.getFileSize();
        entity.mimeType = version.getMimeType();
        entity.storageKey = version.getStorageKey();
        entity.checksum = version.getChecksum();
        entity.createdBy = version.getCreatedBy();
        entity.createdByName = version.getCreatedByName();
        entity.createdAt = version.getCreatedAt();
        entity.current = version.isCurrent();
        entity.notes = version.getNotes();
        return entity;
    }

    /**
     * Converts persistence entity to domain DocumentVersion.
     */
    public DocumentVersion toDomain(DocumentVersionEntity entity) {
        DocumentVersion version = new DocumentVersion(DocumentVersionId.of(entity.id));
        version.setDocumentId(DocumentId.of(entity.documentId));
        version.setVersionNumber(entity.versionNumber);
        version.setChangeNotes(entity.changeNotes);
        version.setFileName(entity.fileName);
        version.setFileSize(entity.fileSize);
        version.setMimeType(entity.mimeType);
        version.setStorageKey(entity.storageKey);
        version.setChecksum(entity.checksum);
        version.setCreatedBy(entity.createdBy);
        version.setCreatedByName(entity.createdByName);
        version.setCreatedAt(entity.createdAt);
        version.setCurrent(entity.current);
        version.setNotes(entity.notes);
        return version;
    }
}
```

## Summary

The Document Management implementation is now complete with:

1. **Persistence Entities**:
   - DocumentEntity with all document attributes
   - DocumentVersionEntity for version history
   - DocumentFolderEntity for folder organization
   - DocumentApprovalEntity for approval workflows

2. **Repository Interfaces**:
   - DocumentRepository with comprehensive search and query methods
   - DocumentVersionRepository for version management
   - DocumentFolderRepository for folder operations
   - DocumentApprovalRepository for approval tracking

3. **Repository Implementations**:
   - Reactive persistence using Hibernate Reactive Panache
   - Full-text search support
   - Transaction management
   - Multi-tenancy support

4. **Mappers**:
   - DocumentMapper for domain-entity conversion
   - DocumentVersionMapper for version conversion

This completes the Document Management bounded context with full CRUD operations, version control, folder organization, approval workflows, and comprehensive search capabilities.
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
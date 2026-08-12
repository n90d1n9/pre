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
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
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
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
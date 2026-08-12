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
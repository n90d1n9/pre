package tech.kayys.syirkah.accounting.domain.document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Root aggregate representing an enterprise document and its version history.
 */
public final class Document {

    private final DocumentId id;
    private final DocumentType type;
    private final DocumentClassification classification;
    private DocumentStatus status;
    private DocumentMetadata metadata;
    private final List<DocumentVersion> versions = new ArrayList<>();

    public Document(DocumentId id, DocumentType type, DocumentClassification classification,
                    DocumentMetadata metadata, String initialHash, String initialStorageKey) {
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
        this.classification = Objects.requireNonNull(classification);
        this.metadata = Objects.requireNonNull(metadata);
        this.status = DocumentStatus.ACTIVE;

        DocumentVersion initialVersion = new DocumentVersion(
                DocumentVersionId.generate(), 1, initialHash, initialStorageKey, Instant.now());
        this.versions.add(initialVersion);
    }

    public DocumentVersion addVersion(String newHash, String newStorageKey, DocumentMetadata updatedMetadata) {
        if (status != DocumentStatus.ACTIVE)
            throw new IllegalStateException("Cannot add version to non-active document: " + status);
        int nextNum = versions.size() + 1;
        DocumentVersion v = new DocumentVersion(
                DocumentVersionId.generate(), nextNum, newHash, newStorageKey, Instant.now());
        versions.add(v);
        if (updatedMetadata != null) {
            this.metadata = updatedMetadata;
        }
        return v;
    }

    public void archive() {
        if (status == DocumentStatus.DELETED) throw new IllegalStateException("Cannot archive deleted document");
        this.status = DocumentStatus.ARCHIVED;
    }

    public void markDeleted() {
        this.status = DocumentStatus.DELETED;
    }

    public DocumentId id() { return id; }
    public DocumentType type() { return type; }
    public DocumentClassification classification() { return classification; }
    public DocumentStatus status() { return status; }
    public DocumentMetadata metadata() { return metadata; }
    public List<DocumentVersion> versions() { return List.copyOf(versions); }
    public DocumentVersion latestVersion() { return versions.get(versions.size() - 1); }
}
